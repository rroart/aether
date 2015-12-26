package roart.content;

import roart.classification.ClassifyDao;
import roart.lang.LanguageDetect;
import roart.model.FileObject;
import roart.model.IndexFiles;
import roart.model.ResultItem;
import roart.model.SearchDisplay;
import roart.queue.IndexQueueElement;
import roart.queue.Queues;
import roart.queue.TikaQueueElement;
import roart.service.SearchService;
import roart.util.Constants;
import roart.util.MyList;
import roart.util.MyLists;
import roart.filesystem.FileSystemDao;
import roart.database.IndexFilesDao;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ParsingReader;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.sax.XHTMLContentHandler;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.fork.ForkParser;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.io.IOUtils;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.language.LanguageProfilerBuilder;
import org.apache.tika.language.ProfilingHandler;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.NetworkParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserDecorator;
import org.apache.tika.parser.html.BoilerpipeContentHandler;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.XMPContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TikaHandler {
    private Logger log = LoggerFactory.getLogger(TikaHandler.class);

    public ByteArrayOutputStream process(String filename, Metadata metadata, IndexFiles index) throws Exception {
    FileObject file = FileSystemDao.get(filename);
	InputStream is = FileSystemDao.getInputStream(file);
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
	} catch (Error e) {
	    System.gc();
	    log.error("Error " + Thread.currentThread().getId() + " " + filename);
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
	log.info("incTikas " + dbfilename);
	Queues.tikaTimeoutQueue.add(dbfilename);
	int size = 0;
	try {
	    OutputStream outputStream = process(filename, metadata, index);
	    long time = System.currentTimeMillis() - now;
	    el.index.setConverttime(time);
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
		    if (filename.startsWith(FileSystemDao.HDFS)) {
			tmpfn = copyFileToTmp(filename);
			fn = tmpfn;
		    } else {
			FileObject file = FileSystemDao.get(filename);  
			fn = FileSystemDao.getAbsolutePath(file);
			if (fn.charAt(4) == ':') {
			    fn = fn.substring(5);
			}
		    }
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

		String lang = LanguageDetect.detect(content);
		if (lang != null && LanguageDetect.isSupportedLanguage(lang)) {
		    now = System.currentTimeMillis();
		    String classification = ClassifyDao.classify(content, lang);
		    time = System.currentTimeMillis() - now;
		    log.info("classtime " + dbfilename + " " + time);
		    //System.out.println("classtime " + time);
		    el.index.setTimeclass(time);
		    el.index.setClassification(classification);
		}
		if (lang != null) {
		    el.index.setLanguage(lang);
		}

		//size = SearchLucene.indexme("all", md5, inputStream);
		IndexQueueElement elem = new IndexQueueElement("all", md5, inputStream, index, el.retlistid, el.retlistnotid, dbfilename, metadata, el.display);
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
		    if (filename.startsWith(FileSystemDao.HDFS)) {
			String fn = copyFileToTmp(filename);
			el.filename = fn;
		    }
		    el.size = size;
		    Queues.otherQueue.add(el);
		} else {
		    log.info("Too small " + dbfilename + " / " + filename + " " + md5 + " " + size);
		    SearchDisplay display = el.display;
		    ResultItem ri = IndexFiles.getResultItem(el.index, el.index.getLanguage(), display);
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

    private String copyFileToTmp(String filename) throws FileNotFoundException,
            IOException {
        int i = filename.lastIndexOf("/");
        String fn = "/tmp/hdfs" + filename.substring(i + 1);
        log.info("copy to local filenames " + filename + " " + fn);
        FileObject file = FileSystemDao.get(filename);
        InputStream in = FileSystemDao.getInputStream(file);
        OutputStream out = new FileOutputStream(new File(fn));
        IOUtils.copy(in, out);
        in.close();
        out.close();
        return fn;
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
