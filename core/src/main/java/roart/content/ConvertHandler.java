package roart.content;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.classification.ClassifyDao;
import roart.common.config.Converter;
import roart.common.config.MyConfig;
import roart.common.constants.Constants;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.Inmemory;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.inmemory.model.InmemoryUtil;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.util.JsonUtil;
import roart.convert.ConvertDAO;
import roart.database.IndexFilesDao;
import roart.dir.Traverse;
import roart.filesystem.FileSystemDao;
import roart.lang.LanguageDetect;
import roart.lang.LanguageDetectFactory;
import roart.queue.Queues;
import roart.service.ControlService;
import roart.util.ISBNUtil;
import roart.util.MyList;
import roart.util.MyLists;
import roart.queue.ConvertQueueElement;
import roart.queue.IndexQueueElement;

public class ConvertHandler {
    private Logger log = LoggerFactory.getLogger(ConvertHandler.class);

    public void doConvert(ConvertQueueElement el) {
        FileObject dbfilename = el.dbfilename;
        FileObject filename = el.filename;
        String md5 = el.md5;
        IndexFiles index = el.index;
        //List<ResultItem> retlist = el.retlistid;
        //List<ResultItem> retlistnot = el.retlistnotid;
        Map<String, String> metadata = el.metadata;
        log.info("incTikas {}", dbfilename);
        Queues.convertTimeoutQueue.add(dbfilename.toString());
        int size = 0;

        //String content = new TikaHandler().getString(el.fsData.getInputStream());
        //Inmemory inmemory = InmemoryFactory.get(MyConfig.conf.getInmemoryServer(), MyConfig.conf.getInmemoryHazelcast(), MyConfig.conf.getInmemoryRedis());
        //InmemoryMessage message = inmemory.send(el.md5, content);
        InmemoryMessage message = FileSystemDao.readFile(el.fsData.fileObject[0]);
        el.message = message;

	log.info("file " + el.fsData.fileObject[0].object);
	log.info("file " + el.fsData.absolutePath);
        String converterString = MyConfig.conf.getConverters();
        Converter[] converters = JsonUtil.convert(converterString, Converter[].class);
        Inmemory inmemory = InmemoryFactory.get(MyConfig.conf.getInmemoryServer(), MyConfig.conf.getInmemoryHazelcast(), MyConfig.conf.getInmemoryRedis());
        String origcontent = inmemory.read(message);
        String mimetype = getMimetype(origcontent, Paths.get(filename.object).getFileName().toString());
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
                if (!Arrays.asList(converter.getMimetypes()).stream().anyMatch(myfilename::endsWith)) {
                    continue;
                }
            }
            // TODO error
	    long now = System.currentTimeMillis();
            str = ConvertDAO.convert(converter, message, metadata, Paths.get(filename.object).getFileName().toString());
	    long time = System.currentTimeMillis() - now;
            if (str != null) {
                el.convertsw = converter.getName();
		el.index.setConverttime("" + time);
	        break;
            }
        }
        el.mimetype = mimetype;
        log.info("Mimetype {}", mimetype);
        if (mimetype != null) {
            metadata.put(Constants.FILESCONTENTTYPE, mimetype);
        }
        if (str != null) {
            String content = inmemory.read(str);
            String lang = null;
            try {
                LanguageDetect languageDetect = LanguageDetectFactory.getMe(LanguageDetectFactory.Detect.OPTIMAIZE);
                lang = languageDetect.detect(content);
                if (lang != null && languageDetect.isSupportedLanguage(lang)) {
                    long now = System.currentTimeMillis();
                    String classification = ClassifyDao.classify(str, lang);
                    long time = System.currentTimeMillis() - now;
                    log.info("classtime " + dbfilename + " " + time);
                    //System.out.println("classtime " + time);
                    el.index.setTimeclass("" + time);
                    el.index.setClassification(classification);
                }
                if (lang != null) {
                    el.index.setLanguage(lang);
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
            IndexQueueElement elem = new IndexQueueElement(null, md5, index, el.retlistid, el.retlistnotid, dbfilename, metadata, str);
            elem.lang = lang;
            //elem.content = content;
            //Inmemory inmemory = InmemoryFactory.get(config.getInmemoryServer(), config.getInmemoryHazelcast(), config.getInmemoryRedis());
            //Inmemory inmemory2 = InmemoryFactory.get(MyConfig.conf.getInmemoryServer(), MyConfig.conf.getInmemoryHazelcast(), MyConfig.conf.getInmemoryRedis());
            //InmemoryMessage message = inmemory2.send(el.md5, content);
            elem.message = str;
            Queues.indexQueue.add(elem);

        } else {
            log.info("Too small " + dbfilename + " / " + filename + " " + md5 + " " + size);
            FileLocation aFl = el.index.getaFilelocation();
            ResultItem ri = IndexFiles.getResultItem(el.index, el.index.getLanguage(), ControlService.nodename, aFl);
            ri.get().set(IndexFiles.FILENAMECOLUMN, dbfilename);
            MyList<ResultItem> retlistnot = (MyList<ResultItem>) MyLists.get(el.retlistnotid); 
            retlistnot.add(ri);
            Boolean isIndexed = index.getIndexed();
            if (isIndexed == null || isIndexed.booleanValue() == false) {
                index.incrFailed();
                //index.save();
            }
            index.setPriority(1);
            // file unlock dbindex
            // config with finegrained distrib
            IndexFilesDao.add(index);

        }
        boolean success = Queues.tikaTimeoutQueue.remove(dbfilename);
        if (!success) {
            log.error("queue not having " + dbfilename);
        }
        log.info("ending " + el.md5 + " " + el.dbfilename);
    }

    private String getMimetype(String content, String filename) {
        try {
	    Path tempPath = Paths.get("/tmp", filename);
	    Files.deleteIfExists(tempPath);
            Path tempFile = Files.createFile(Paths.get("/tmp", filename));
	    log.info("File " + filename + " " + tempFile.toString());
            Files.write(tempFile, content.getBytes());
            String mimetype = Files.probeContentType(tempFile);
            Files.delete(tempFile);
            return mimetype;
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return null;
    }
}
