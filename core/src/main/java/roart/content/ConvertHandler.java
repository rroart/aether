package roart.content;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.classification.ClassifyDao;
import roart.common.collections.MyList;
import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyLists;
import roart.common.collections.impl.MyQueues;
import roart.common.config.Converter;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.OperationConstants;
import roart.common.convert.ConvertResult;
import roart.common.filesystem.MyFile;
import roart.common.inmemory.common.Inmemory;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.inmemory.util.InmemoryUtil;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.util.IOUtil;
import roart.common.util.JsonUtil;
import roart.common.util.QueueUtil;
import roart.convert.ConvertDAO;
import roart.database.IndexFilesDao;
import roart.dir.Traverse;
import roart.filesystem.FileSystemDao;
import roart.hcutil.GetHazelcastInstance;
import roart.lang.LanguageDetect;
import roart.lang.LanguageDetectFactory;
import roart.queue.Queues;
import roart.service.ControlService;
import roart.util.ISBNUtil;
import roart.common.queue.QueueElement;
import roart.common.synchronization.MyLock;

public class ConvertHandler {
    private Logger log = LoggerFactory.getLogger(ConvertHandler.class);

    private NodeConfig nodeConf;

    private ControlService controlService;
    
    public ConvertHandler(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
    }

    public void doConvert(QueueElement element, NodeConfig nodeConf) {
        FileObject filename = element.getFileObject();
        String md5 = element.getMd5();
        // TODO trylock
        IndexFiles index = element.getIndexFiles();
        //List<ResultItem> retlist = el.retlistid;
        //List<ResultItem> retlistnot = el.retlistnotid;
        Map<String, String> metadata = element.getMetadata();
        log.info("incTikas {}", filename);
        new Queues(nodeConf, controlService).convertTimeoutQueue.add(filename.toString());
        int size = 0;

        //String content = new TikaHandler().getString(el.fsData.getInputStream());
        //Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
        //InmemoryMessage message = inmemory.send(el.md5, content);
        // may not exist
        InmemoryMessage message = new FileSystemDao(nodeConf, controlService).readFile(element.getFileObject());
        element.setMessage(message);
        element.getIndexFiles().setFailedreason(null);

	log.info("file {}", element.getFileObject());
	
	// find converters
	
        String converterString = nodeConf.getConverters();
        Converter[] converters = JsonUtil.convert(converterString, Converter[].class);
        Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
        String mimetype = null;
        try (InputStream origcontent = inmemory.getInputStream(message)) {
            mimetype = getMimetype(origcontent, Paths.get(filename.object).getFileName().toString());
            log.info("Mimetype {}", mimetype);
            if (mimetype != null) {
                metadata.put(Constants.FILESCONTENTTYPE, mimetype);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            log.error("File copy error");
            element.getIndexFiles().setFailedreason("File copy error");
            converters = new Converter[0];
        }
        
        // before convert
        
        // null mime isbn
        InmemoryMessage str = null;
        for (int i = 0; i < converters.length; i++) {
            Converter converter = converters[i];
            if (converter.getMimetypes().length > 0) {
                if (!Arrays.asList(converter.getMimetypes()).contains(mimetype)) {
                    continue;
                }
            }
            if (converter.getSuffixes().length > 0) {
                String myfilename = element.getFileObject().object.toLowerCase();
                if (!Arrays.asList(converter.getSuffixes()).stream().anyMatch(myfilename::endsWith)) {
                    continue;
                }
            }
            // TODO error
	    long now = System.currentTimeMillis();
            try {
                ConvertResult result = new ConvertDAO(nodeConf, controlService).convert(converter, message, metadata, Paths.get(filename.object).getFileName().toString(), element.getIndexFiles());
                str = handleConvertResult(metadata, index, result);

            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
	    long time = System.currentTimeMillis() - now;
            if (str != null) {
                element.getIndexFiles().setConvertsw(converter.getName());
		element.getIndexFiles().setConverttime("" + time);
	        break;
            } else {
            }
        }
        inmemory.delete(message);
        try {
            controlService.curatorClient.delete().forPath("/" + Constants.AETHER + "/" + Constants.DATA + "/" + message.getId());
        } catch (Exception e) {
            log.info(Constants.EXCEPTION, e);
        }
        
        // after convert
        
        element.setMessage(str);
        log.info("Mimetype {}", mimetype);
        if (mimetype != null) {
            metadata.put(Constants.FILESCONTENTTYPE, mimetype);
        }
        if (str != null) {
            String content = InmemoryUtil.convertWithCharset(IOUtil.toByteArrayMax(inmemory.getInputStream(str)));
            String lang = null;
            try {
                LanguageDetect languageDetect = LanguageDetectFactory.getMe(LanguageDetectFactory.Detect.OPTIMAIZE);
                lang = languageDetect.detect(content);
                if (lang != null) {
                    element.getIndexFiles().setLanguage(lang);
                }
                if (lang != null && languageDetect.isSupportedLanguage(lang)) {
                    long now = System.currentTimeMillis();
                    String classification = new ClassifyDao(nodeConf, controlService).classify(str, lang);
                    long time = System.currentTimeMillis() - now;
                    log.info("classtime {} {}", filename, time);
                    element.getIndexFiles().setTimeclass("" + time);
                    element.getIndexFiles().setClassification(classification);
                }
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
            try {
                element.getIndexFiles().setIsbn(new ISBNUtil().extract(content, false));
                log.info("ISBN {}", element.getIndexFiles().getIsbn());
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
            //size = SearchLucene.getMyid()me("all", md5, inputStream);
            //QueueElement elem = new QueueElement(el.getMyid(), filename, md5, index, metadata, str);
            //elem.content = content;
            //Inmemory inmemory = InmemoryFactory.get(config.getInmemoryServer(), config.getInmemoryHazelcast(), config.getInmemoryRedis());
            //Inmemory inmemory2 = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
            //InmemoryMessage message = inmemory2.send(el.md5, content);
            //elem.setMessage(str);
            new Queues(nodeConf, controlService).getIndexQueue().offer(element);
            //Queues.getIndexQueueSize().incrementAndGet();

        } else {
            log.info("Not converted {} {} {}", filename, md5, size);
            FileLocation aFl = element.getIndexFiles().getaFilelocation();
            ResultItem ri = IndexFiles.getResultItem(element.getIndexFiles(), element.getIndexFiles().getLanguage(), controlService.getConfigName(), aFl);
            ri.get().set(IndexFiles.FILENAMECOLUMN, filename);
            MyQueue<ResultItem> retlistnot = (MyQueue<ResultItem>) MyQueues.get(QueueUtil.retlistnotQueue(element.getMyid()), nodeConf, controlService.curatorClient, GetHazelcastInstance.instance(nodeConf.getInmemoryHazelcast())); 
            retlistnot.offer(ri);
            Boolean isIndexed = index.getIndexed();
            if (isIndexed == null || isIndexed.booleanValue() == false) {
                index.incrFailed();
                //index.save();
            }
            index.setPriority(1);
            // file unlock dbindex
            // config with finegrained distrib
            new IndexFilesDao(nodeConf, controlService).add(index);
        }
        boolean success = new Queues(nodeConf, controlService).convertTimeoutQueue.remove(filename.toString());
        if (!success) {
            log.error("queue not having {}", filename);
        }
        log.info("ending {} {}", element.getMd5(), element.getFileObject());
        MyLock lock = index.getLock();
        if (lock != null) {
            lock.unlock();
        }
    }

    private String getMimetype(InputStream content, String filename) throws IOException {
        //try {
	    Path tempFile = Paths.get("/tmp", filename);
	    Files.deleteIfExists(tempFile);
            //Path tempFile = Files.createFile(Paths.get("/tmp", filename));
	    log.info("File {} {}", filename, tempFile.toString());
            Files.copy(content, tempFile);
            String mimetype = Files.probeContentType(tempFile);
            Files.delete(tempFile);
            return mimetype;
        //} catch (Exception e) {
        //    log.error(Constants.EXCEPTION, e);
        //}
        //return null;
    }

    public void doConvertQueue(QueueElement element, NodeConfig nodeConf) {
        FileObject filename = element.getFileObject();
        String md5 = element.getMd5();
        // TODO trylock
        IndexFiles index = element.getIndexFiles();
        //List<ResultItem> retlist = el.retlistid;
        //List<ResultItem> retlistnot = el.retlistnotid;
        Map<String, String> metadata = element.getMetadata();
        log.info("incTikas {}", filename);
        new Queues(nodeConf, controlService).convertTimeoutQueue.add(filename.toString());
        Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
        int size = 0;

        if (element.getOpid() == null) {

            //String content = new TikaHandler().getString(el.fsData.getInputStream());
            //Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
            //InmemoryMessage message = inmemory.send(el.md5, content);
            // may not exist
            element.setQueue(QueueUtil.getConvertQueue());
            new FileSystemDao(nodeConf, controlService).readFileQueue(element, element.getFileObject());
            return;
        }
        if (element.getOpid() != null && element.getOpid().equals(OperationConstants.READFILE)) {
            Map<String, InmemoryMessage> map = element.getFileSystemMessageResult().message;
            element.setFileSystemMessageResult(null);
            InmemoryMessage message = map.get(element.getFileObject().object);
            element.setMessage(message);

            element.getIndexFiles().setFailedreason(null);

            log.info("file {}", element.getFileObject());

            // find converters

            String converterString = nodeConf.getConverters();
            Converter[] converters = JsonUtil.convert(converterString, Converter[].class);
            String mimetype = null;
            try (InputStream origcontent = inmemory.getInputStream(message)) {
                mimetype = getMimetype(origcontent, Paths.get(filename.object).getFileName().toString());
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
                log.error("File copy error");
                element.getIndexFiles().setFailedreason("File copy error");
                converters = new Converter[0];
            }

            // before convert

            // null mime isbn
            InmemoryMessage str = null;
            List<Converter> converterList = new ArrayList<>();
            for (int i = 0; i < converters.length; i++) {
                Converter converter = converters[i];
                if (converter.getMimetypes().length > 0) {
                    if (!Arrays.asList(converter.getMimetypes()).contains(mimetype)) {
                        continue;
                    }
                }
                if (converter.getSuffixes().length > 0) {
                    String myfilename = element.getFileObject().object.toLowerCase();
                    if (!Arrays.asList(converter.getSuffixes()).stream().anyMatch(myfilename::endsWith)) {
                        continue;
                    }
                }
                converterList.add(converter);
            }
            try {
                new ConvertDAO(nodeConf, controlService).convertQueue(element, converterList, message, metadata, Paths.get(filename.object).getFileName().toString(), element.getIndexFiles());
                return;
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
        // after convert
        ClassifyDao classifyDao = new ClassifyDao(nodeConf, controlService);
        if (element.getOpid() != null && element.getOpid().equals(OperationConstants.CONVERT)) {
            inmemory.delete(element.getMessage());
            try {
                controlService.curatorClient.delete().forPath("/" + Constants.AETHER + "/" + Constants.DATA + "/" + element.getMessage().getId());
            } catch (Exception e) {
                log.info(Constants.EXCEPTION, e);
            }
            ConvertResult result = element.getConvertResult();
            InmemoryMessage msg = handleConvertResult(element.getMetadata(), index, result);
            element.setMessage(msg);
            element.setConvertResult(null);
            InmemoryMessage str = element.getMessage();
            if (str != null) {
                String content = InmemoryUtil.convertWithCharset(IOUtil.toByteArrayMax(inmemory.getInputStream(str)));
                try {
                    element.getIndexFiles().setIsbn(new ISBNUtil().extract(content, false));
                    log.info("ISBN {}", element.getIndexFiles().getIsbn());
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }

                String lang = null;
                try {
                    LanguageDetect languageDetect = LanguageDetectFactory.getMe(LanguageDetectFactory.Detect.OPTIMAIZE);
                    lang = languageDetect.detect(content);
                    if (lang != null) {
                        element.getIndexFiles().setLanguage(lang);
                    }
                    if (lang != null && languageDetect.isSupportedLanguage(lang)) {
                        if (classifyDao.classify != null) {
                            element.setQueue(QueueUtil.getConvertQueue());
                            classifyDao.classifyQueue(element, str, lang);
                            return;
                        }
                    }
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
                //size = SearchLucene.getMyid()me("all", md5, inputStream);
                //QueueElement elem = new QueueElement(el.getMyid(), filename, md5, index, metadata, str);
                //elem.content = content;
                //Inmemory inmemory = InmemoryFactory.get(config.getInmemoryServer(), config.getInmemoryHazelcast(), config.getInmemoryRedis());
                //Inmemory inmemory2 = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
                //InmemoryMessage message = inmemory2.send(el.md5, content);
                //elem.setMessage(str);
                //Queues.getIndexQueueSize().incrementAndGet();
                // no TODO
            } else {
                log.info("Not converted {} {} {}", filename, md5, size);
                FileLocation aFl = element.getIndexFiles().getaFilelocation();
                ResultItem ri = IndexFiles.getResultItem(element.getIndexFiles(), element.getIndexFiles().getLanguage(), controlService.getConfigName(), aFl);
                ri.get().set(IndexFiles.FILENAMECOLUMN, filename);
                MyQueue<ResultItem> retlistnot = (MyQueue<ResultItem>) MyQueues.get(QueueUtil.retlistnotQueue(element.getMyid()), nodeConf, controlService.curatorClient, GetHazelcastInstance.instance(nodeConf.getInmemoryHazelcast())); 
                retlistnot.offer(ri);
                Boolean isIndexed = index.getIndexed();
                if (isIndexed == null || isIndexed.booleanValue() == false) {
                    index.incrFailed();
                    //index.save();
                }
                index.setPriority(1);
                // file unlock dbindex
                // config with finegrained distrib
                new IndexFilesDao(nodeConf, controlService).add(index);
                return; // TODO
            }

        }//????

        if (element.getOpid() != null && element.getOpid().equals(OperationConstants.CLASSIFY)) {
            String classification = element.getMachineLearningClassifyResult().result;
            element.setMachineLearningClassifyResult(null);
            element.getIndexFiles().setClassification(classification);
        }
        
        // TODO?
        boolean success = new Queues(nodeConf, controlService).convertTimeoutQueue.remove(filename.toString());
        if (!success) {
            log.error("queue not having {}", filename);
        }
        log.info("ending {} {}", element.getMd5(), element.getFileObject());
        
        element.setOpid(null);
        new Queues(nodeConf, controlService).getIndexQueue().offer(element);

        
    }

    private InmemoryMessage handleConvertResult(Map<String, String> metadata, IndexFiles index, ConvertResult result) {
        if (result == null) {
            return null;
        }
        // get md from Tika and use it, even if Tika fails
        if (result.metadata != null) {
            metadata.putAll(result.metadata);
        }
        if (result.error != null) {
            index.setFailedreason(result.error);
        }
        if (result.message == null) {
            return null;
        }
        return result.message;
    }

}
