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
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.Inmemory;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.inmemory.model.InmemoryUtil;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.util.IOUtil;
import roart.common.util.JsonUtil;
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
import roart.queue.ConvertQueueElement;
import roart.queue.IndexQueueElement;

public class ConvertHandler {
    private Logger log = LoggerFactory.getLogger(ConvertHandler.class);

    private NodeConfig nodeConf;
    
    public ConvertHandler(NodeConfig nodeConf) {
        super();
        this.nodeConf = nodeConf;
    }

    public void doConvert(ConvertQueueElement el, NodeConfig nodeConf) {
        FileObject filename = el.filename;
        String md5 = el.md5;
        IndexFiles index = el.index;
        //List<ResultItem> retlist = el.retlistid;
        //List<ResultItem> retlistnot = el.retlistnotid;
        Map<String, String> metadata = el.metadata;
        log.info("incTikas {}", filename);
        new Queues(nodeConf).convertTimeoutQueue.add(filename.toString());
        int size = 0;

        //String content = new TikaHandler().getString(el.fsData.getInputStream());
        //Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
        //InmemoryMessage message = inmemory.send(el.md5, content);
        InmemoryMessage message = new FileSystemDao(nodeConf).readFile(el.filename);
        el.message = message;
        el.index.setFailedreason(null);

	log.info("file {}", el.filename);
	
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
            el.index.setFailedreason("File copy error");
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
                String myfilename = el.filename.object.toLowerCase();
                if (!Arrays.asList(converter.getSuffixes()).stream().anyMatch(myfilename::endsWith)) {
                    continue;
                }
            }
            // TODO error
	    long now = System.currentTimeMillis();
            try {
                str = new ConvertDAO(nodeConf).convert(converter, message, metadata, Paths.get(filename.object).getFileName().toString(), el.index);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
	    long time = System.currentTimeMillis() - now;
            if (str != null) {
                el.convertsw = converter.getName();
		el.index.setConverttime("" + time);
	        break;
            } else {
            }
        }
        inmemory.delete(message);
        
        // after convert
        
        el.message = null;
        el.mimetype = mimetype;
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
                    el.index.setLanguage(lang);
                }
                if (lang != null && languageDetect.isSupportedLanguage(lang)) {
                    long now = System.currentTimeMillis();
                    String classification = new ClassifyDao(nodeConf).classify(str, lang);
                    long time = System.currentTimeMillis() - now;
                    log.info("classtime {} {}", filename, time);
                    el.index.setTimeclass("" + time);
                    el.index.setClassification(classification);
                }
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
            try {
                el.index.setIsbn(new ISBNUtil().extract(content, false));
                log.info("ISBN {}", el.index.getIsbn());
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
            //size = SearchLucene.indexme("all", md5, inputStream);
            IndexQueueElement elem = new IndexQueueElement(null, md5, index, el.retlistid, el.retlistnotid, filename, metadata, str);
            elem.lang = lang;
            //elem.content = content;
            //Inmemory inmemory = InmemoryFactory.get(config.getInmemoryServer(), config.getInmemoryHazelcast(), config.getInmemoryRedis());
            //Inmemory inmemory2 = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
            //InmemoryMessage message = inmemory2.send(el.md5, content);
            elem.message = str;
            elem.convertsw = el.convertsw;
            new Queues(nodeConf).getIndexQueue().offer(elem);
            //Queues.getIndexQueueSize().incrementAndGet();

        } else {
            log.info("Not converted {} {} {}", filename, md5, size);
            FileLocation aFl = el.index.getaFilelocation();
            ResultItem ri = IndexFiles.getResultItem(el.index, el.index.getLanguage(), ControlService.getConfigName(), aFl);
            ri.get().set(IndexFiles.FILENAMECOLUMN, filename);
            MyQueue<ResultItem> retlistnot = (MyQueue<ResultItem>) MyQueues.get(el.retlistnotid, nodeConf, ControlService.curatorClient, GetHazelcastInstance.instance()); 
            retlistnot.offer(ri);
            Boolean isIndexed = index.getIndexed();
            if (isIndexed == null || isIndexed.booleanValue() == false) {
                index.incrFailed();
                //index.save();
            }
            index.setPriority(1);
            // file unlock dbindex
            // config with finegrained distrib
            new IndexFilesDao(nodeConf).add(index);
        }
        boolean success = new Queues(nodeConf).convertTimeoutQueue.remove(filename.toString());
        if (!success) {
            log.error("queue not having {}", filename);
        }
        log.info("ending {} {}", el.md5, el.filename);
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
