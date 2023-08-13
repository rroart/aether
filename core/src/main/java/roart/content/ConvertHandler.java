package roart.content;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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

public class ConvertHandler {
    private Logger log = LoggerFactory.getLogger(ConvertHandler.class);

    private NodeConfig nodeConf;

    private ControlService controlService;
    
    public ConvertHandler(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
    }

    public void doConvert(QueueElement el, NodeConfig nodeConf) {
        FileObject filename = el.getFileObject();
        String md5 = el.getMd5();
        // TODO trylock
        IndexFiles index = el.getIndexFiles();
        //List<ResultItem> retlist = el.retlistid;
        //List<ResultItem> retlistnot = el.retlistnotid;
        Map<String, String> metadata = el.getMetadata();
        log.info("incTikas {}", filename);
        new Queues(nodeConf, controlService).convertTimeoutQueue.add(filename.toString());
        int size = 0;

        //String content = new TikaHandler().getString(el.fsData.getInputStream());
        //Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
        //InmemoryMessage message = inmemory.send(el.md5, content);
        // may not exist
        InmemoryMessage message = new FileSystemDao(nodeConf, controlService).readFile(el.getFileObject());
        el.setMessage(message);
        el.getIndexFiles().setFailedreason(null);

	log.info("file {}", el.getFileObject());
	
	// find converters
	
        String converterString = nodeConf.getConverters();
        Converter[] converters = JsonUtil.convert(converterString, Converter[].class);
        Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
        String mimetype = null;
        try (InputStream origcontent = inmemory.getInputStream(message)) {
            mimetype = getMimetype(origcontent, Paths.get(filename.object).getFileName().toString());
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            log.error("File copy error");
            el.getIndexFiles().setFailedreason("File copy error");
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
                String myfilename = el.getFileObject().object.toLowerCase();
                if (!Arrays.asList(converter.getSuffixes()).stream().anyMatch(myfilename::endsWith)) {
                    continue;
                }
            }
            // TODO error
	    long now = System.currentTimeMillis();
            try {
                str = new ConvertDAO(nodeConf, controlService).convert(converter, message, metadata, Paths.get(filename.object).getFileName().toString(), el.getIndexFiles());
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
	    long time = System.currentTimeMillis() - now;
            if (str != null) {
                el.getIndexFiles().setConvertsw(converter.getName());
		el.getIndexFiles().setConverttime("" + time);
	        break;
            } else {
            }
        }
        inmemory.delete(message);
        
        // after convert
        
        el.setMessage(null);
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
                    el.getIndexFiles().setLanguage(lang);
                }
                if (lang != null && languageDetect.isSupportedLanguage(lang)) {
                    long now = System.currentTimeMillis();
                    String classification = new ClassifyDao(nodeConf, controlService).classify(str, lang);
                    long time = System.currentTimeMillis() - now;
                    log.info("classtime {} {}", filename, time);
                    el.getIndexFiles().setTimeclass("" + time);
                    el.getIndexFiles().setClassification(classification);
                }
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
            try {
                el.getIndexFiles().setIsbn(new ISBNUtil().extract(content, false));
                log.info("ISBN {}", el.getIndexFiles().getIsbn());
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
            new Queues(nodeConf, controlService).getIndexQueue().offer(el);
            //Queues.getIndexQueueSize().incrementAndGet();

        } else {
            log.info("Not converted {} {} {}", filename, md5, size);
            FileLocation aFl = el.getIndexFiles().getaFilelocation();
            ResultItem ri = IndexFiles.getResultItem(el.getIndexFiles(), el.getIndexFiles().getLanguage(), controlService.getConfigName(), aFl);
            ri.get().set(IndexFiles.FILENAMECOLUMN, filename);
            MyQueue<ResultItem> retlistnot = (MyQueue<ResultItem>) MyQueues.get(QueueUtil.retlistnotQueue(el.getMyid()), nodeConf, controlService.curatorClient, GetHazelcastInstance.instance(nodeConf.getInmemoryHazelcast())); 
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
        log.info("ending {} {}", el.getMd5(), el.getFileObject());
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
}
