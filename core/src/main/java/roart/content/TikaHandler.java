package roart.content;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;

import roart.classification.ClassifyDao;
import roart.common.constants.Constants;
import roart.common.filesystem.MyFile;
import roart.common.model.FileLocation;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.database.IndexFilesDao;
import roart.dir.Traverse;
import roart.filesystem.FileSystemAccess;
import roart.filesystem.FileSystemFactory;
import roart.lang.LanguageDetect;
import roart.lang.LanguageDetectFactory;
import roart.queue.IndexQueueElement;
import roart.queue.Queues;
import roart.queue.TikaQueueElement;
import roart.service.ControlService;
import roart.util.MyList;
import roart.util.MyLists;

public class TikaHandler {
    private Logger log = LoggerFactory.getLogger(TikaHandler.class);

    public ByteArrayOutputStream process(TikaQueueElement el) throws Exception {
        String filename = el.filename;
        Metadata metadata = el.metadata;
        MyFile fsData = el.fsData;
        IndexFiles index = el.index;
        
        InputStream is = fsData.getInputStream();
        InputStream input = TikaInputStream.get(is);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            ParseContext context = new ParseContext();
            Detector detector = new DefaultDetector();
            Parser parser = new AutoDetectParser(detector);
            context.set(Parser.class, parser);
            ContentHandler handler = new BodyContentHandler(getOutputWriter(output, null));
            parser.parse(input, handler, metadata, context);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            index.setFailedreason(index.getFailedreason() + "tika exception " + e.getClass().getName() + " ");
            output = null;
        } catch (java.lang.ThreadDeath e) {
            log.error("Error expected {} {}", Thread.currentThread().getId(), filename);
            log.error(Constants.ERROR, e);
            index.setFailedreason(index.getFailedreason() + "tika timeout " + e.getClass().getName() + " ");
            output = null;
        } catch (Error e) {
            System.gc();
            log.error("Error {} {}", Thread.currentThread().getId(), filename);
            log.error(Constants.ERROR, e);
            index.setFailedreason(index.getFailedreason() + "tika error " + e.getClass().getName() + " ");
            output = null;
        } finally {
            input.close();
            System.out.flush();
        }
        return output;
    }

    private Writer getOutputWriter(OutputStream output, String encoding)
            throws UnsupportedEncodingException {
        if (encoding != null) {
            return new OutputStreamWriter(output, encoding);
        } else if (System.getProperty("os.name")
                .toLowerCase().startsWith("mac os x")) {
            // TIKA-324: Override the default encoding on Mac OS X              
            return new OutputStreamWriter(output, "UTF-8");
        } else {
            return new OutputStreamWriter(output);
        }
    }

    //private static int doTika(String dbfilename, String filename, String md5, Index index, List<String> retlist) {
    public void doTika(TikaQueueElement el) {
        /*
	  TikaQueueElement el = Queues.tikaQueue.poll();
	  if (el == null) {
	  log.error("empty queue");
	  return;
	  }
         */
        // vulnerable spot
        //Queues.incTikas();
        //Queues.tikaRunQueue.add(el);
        long now = System.currentTimeMillis();
        String dbfilename = el.dbfilename;
        String filename = el.filename;
        String md5 = el.md5;
        IndexFiles index = el.index;
        //List<ResultItem> retlist = el.retlistid;
        //List<ResultItem> retlistnot = el.retlistnotid;
        Metadata metadata = el.metadata;
        log.info("incTikas {}", dbfilename);
        Queues.tikaTimeoutQueue.add(dbfilename);
        int size = 0;
        try {
            OutputStream outputStream = process(el);
            long time = System.currentTimeMillis() - now;
            el.index.setConverttime("" + time);
            InputStream inputStream = null;
            if (outputStream != null) {
                inputStream = new ByteArrayInputStream(((ByteArrayOutputStream) outputStream).toByteArray());
                size = ((ByteArrayOutputStream)outputStream).size();
                log.info("size1 " + dbfilename + " / " + filename + " " + size + " : " + time);
            } else {
                size = -1;
                log.info("size1 " + dbfilename + " / " + filename + " crash " + " : " + time); 
            }

            //retlist.add(new ResultItem(new String("tika handling filename " + dbfilename + " " + size + " : " + time)));
            //int limit = mylimit(dbfilename);
            if (size <= 0) {
                if (dbfilename.equals(filename)) {
                    String fn = null;
                    String tmpfn = null;
                    FileSystemAccess fsAccess = FileSystemFactory.getFileSystem(filename);
                    fn = fsAccess.getLocalFilesystemFile(filename);
                    el.filename = fn;
                    /*
                    if (FsUtil.isRemote(filename)) {
                        tmpfn = RemoteFileSystemAccess.copyFileToTmp(filename);
                        fn = tmpfn;
                    } else {
                        FileObject file = FileSystemDao.get(filename);  
                        fn = FileSystemDao.getAbsolutePath(file);
                        if (fn.charAt(4) == ':') {
                            fn = fn.substring(5);
                        }
                    }
                    */
                    log.info("for mime type " + fn);
                    Path path = new File(fn).toPath();
                    String mimetype = Files.probeContentType(path);
                    metadata.add(Constants.FILESCONTENTTYPE, mimetype);
                    el.mimetype = mimetype;
                    if (tmpfn != null && tmpfn.contains("/tmp/")) {
                        File delFile = new File(tmpfn);
                        delFile.delete();
                    }
                    if (size == 0) {
                        log.info("size null for mime type " + mimetype + " " + path.toString() + " " + dbfilename + " / " + filename);
                    } else {
                        log.info("crash for mime type " + mimetype + " " + path.toString() + " " + dbfilename + " / " + filename);
                    }
                }
            }
            // images may give 0 size and be index, except djvu, it needs special handling
            boolean isImage = false;
            if (el.mimetype != null) {
                if (el.mimetype.startsWith("image/") && !el.mimetype.equals("image/vnd.djvu")) {
                    isImage = true;
                }
            }
            log.info("size2 " + md5 + " " + filename + " " + size + " mimetype " + el. mimetype + " image " + isImage);
            if (size > 0 || isImage) {
                //log.info("sizes " + size + " " + limit);
                //log.info("handling filename " + dbfilename + " " + size + " : " + time);

                String content = getString(inputStream);

                LanguageDetect languageDetect = LanguageDetectFactory.getMe(LanguageDetectFactory.Detect.OPTIMAIZE);
                String lang = languageDetect.detect(content);
                if (lang != null && languageDetect.isSupportedLanguage(lang)) {
                    now = System.currentTimeMillis();
                    String classification = ClassifyDao.classify(content, lang);
                    time = System.currentTimeMillis() - now;
                    log.info("classtime " + dbfilename + " " + time);
                    //System.out.println("classtime " + time);
                    el.index.setTimeclass("" + time);
                    el.index.setClassification(classification);
                }
                if (lang != null) {
                    el.index.setLanguage(lang);
                }

                //size = SearchLucene.indexme("all", md5, inputStream);
                IndexQueueElement elem = new IndexQueueElement("all", md5, inputStream, index, el.retlistid, el.retlistnotid, dbfilename, metadata);
                elem.lang = lang;
                elem.content = content;
                if (el.convertsw != null) {
                    elem.convertsw = el.convertsw;
                } else {
                    elem.convertsw = "tika";
                }
                Queues.indexQueue.add(elem);
            } else {
                //log.info("filenames " + dbfilename + " " + filename);
                if (dbfilename.equals(filename)) {
                    FileSystemAccess fsAccess = FileSystemFactory.getFileSystem(filename);
                    String fn = fsAccess.getLocalFilesystemFile(filename);
                    el.filename = fn;
                    /*
                    if (FsUtil.isRemote(filename)) {
                        String fn = RemoteFileSystemAccess.copyFileToTmp(filename);
                        el.filename = fn;
                    }
                    */
                    el.size = size;
                    Queues.otherQueue.add(el);
                } else {
                    log.info("Too small " + dbfilename + " / " + filename + " " + md5 + " " + size);
                    FileLocation maybeFl = Traverse.getExistingLocalFilelocationMaybe(el.index);
                    ResultItem ri = IndexFiles.getResultItem(el.index, el.index.getLanguage(), ControlService.nodename, maybeFl);
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
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (el.filename.startsWith("/tmp/other")/* || el.filename.startsWith(FileSystemDao.FILE + "/tmp/")*/) {
                log.info("delete file " + el.filename);
                File delFile = new File(el.filename);
                delFile.delete();
            }
        } catch (Exception e) {
            el.index.setFailedreason(el.index.getFailedreason() + "tika exception " + e.getClass().getName() + " ");
            log.error(Constants.EXCEPTION, e);
        } finally {
            //stream.close();            // close the stream
        }
        //Queues.decTikas();
        //Queues.tikaRunQueue.remove(el);

        boolean success = Queues.tikaTimeoutQueue.remove(dbfilename);
        if (!success) {
            log.error("queue not having " + dbfilename);
        }
        log.info("ending " + el.md5 + " " + el.dbfilename);
    }

	public int mylimit(String filename) {
	String lowercase = filename.toLowerCase();
	if (lowercase.endsWith(".pdf")) {
	    return 4096;
	}
	if (lowercase.endsWith(".djvu") || lowercase.endsWith(".djv")) {
	    return 4096;
	}
	if (lowercase.endsWith(".mp3")) {
	    return 16;
	}
	if (lowercase.endsWith(".flac")) {
	    return 16;
	}
	return 4096;
	}

	private String getString(InputStream inputStream) {
	try {
	    if (inputStream == null) {
		return "";
	    }
	    DataInputStream in = new DataInputStream(inputStream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String line = null;
	// index some data
	    StringBuilder result = new StringBuilder();
	    while ((line = br.readLine()) != null) {
		result.append(line);
	    }
	    return  result.toString();
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	    return null;
	}
	}

}
