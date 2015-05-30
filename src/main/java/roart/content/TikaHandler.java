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
import roart.filesystem.FileSystemDao;

import java.io.*;

import java.util.Arrays;
import java.util.List;

import java.net.URL;

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

    private class NoDocumentMetHandler extends DefaultHandler{

	private Metadata metadata;

        private PrintWriter writer;

        private boolean metOutput;

        public NoDocumentMetHandler(PrintWriter writer){
            this.writer = writer;
            this.metOutput = false;
        }

        @Override
	    public void endDocument() {
            String[] names = metadata.names();
            Arrays.sort(names);
            outputMetadata(names);
            writer.flush();
            this.metOutput = true;
        }

        public void outputMetadata(String[] names) {
	    for (String name : names) {
		writer.println(name + ": " + metadata.get(name));
	    }
        }

        public boolean metOutput(){
            return this.metOutput;
        }

    }

    private class OutputType {

        public void process(InputStream input, OutputStream output, Metadata metadata)
	    throws Exception {
            Parser p = parser;
            if (fork) {
                p = new ForkParser(TikaHandler.class.getClassLoader(), p);
            }
            ContentHandler handler = getContentHandler(output);
            p.parse(input, handler, metadata, context);
            // fix for TIKA-596: if a parser doesn't generate                              // XHTML output, the lack of an output document prevents            
            // metadata from being output: this fixes that                      
            if (handler instanceof NoDocumentMetHandler){
                NoDocumentMetHandler metHandler = (NoDocumentMetHandler)handler;
                if(!metHandler.metOutput()){
                    metHandler.endDocument();
                }
            }
        }

        protected ContentHandler getContentHandler(OutputStream output)
	    throws Exception {
            throw new UnsupportedOperationException();
        }

    }

    private final OutputType TEXT = new OutputType() {
        @Override
	    protected ContentHandler getContentHandler(OutputStream output)
	    throws Exception {
            return new BodyContentHandler(getOutputWriter(output, encoding));
        }
	};

    private final OutputType TEXT_MAIN = new OutputType() {
        @Override
	    protected ContentHandler getContentHandler(OutputStream output)
	    throws Exception {
            return new BoilerpipeContentHandler(getOutputWriter(output, encoding));
        }
	};

    private ParseContext context;

    private Detector detector;

    private Parser parser;

    private OutputType type = TEXT;

    /**                                                                         
     * Output character encoding, or <code>null</code> for platform default     
     */
    private String encoding = null;

    private boolean fork = false;

    /*
    public TikaHandler() throws Exception {
        context = new ParseContext();
        detector = new DefaultDetector();
        parser = new AutoDetectParser(detector);
        context.set(Parser.class, parser);
    }
    */

    public ByteArrayOutputStream process(String filename, Metadata metadata, IndexFiles index) throws Exception {
	//TikaHandler();
        context = new ParseContext();
        detector = new DefaultDetector();
        parser = new AutoDetectParser(detector);
        context.set(Parser.class, parser);

	/*
	if (filename.endsWith(".MP3") || filename.endsWith(".mp3") || filename.endsWith(".flac") || filename.endsWith("Improve Your Confidence An.pdf") || filename.endsWith("EdgarCayceReadings.chm")) {
		log.error("manual mp3 skip " + filename);
		return new ByteArrayOutputStream();
	}
	*/
	type = TEXT;
	FileObject file = FileSystemDao.get(filename);
	InputStream is = FileSystemDao.getInputStream(file);
	InputStream input = TikaInputStream.get(is);
	/*
	File file = new File(filename);
	URL url = null;
	if (file.isFile()) {
	    url = file.toURI().toURL();
	}
	if (url == null) {
	    index.setFailedreason(index.getFailedreason() + "tika url null ");
	    log.error("tika url null for " + filename);
	    return null;
	}
	InputStream input = TikaInputStream.get(url, metadata);
	*/
	//PipedInputStream  writeIn = new PipedInputStream();
	//PipedOutputStream output = new PipedOutputStream(writeIn);
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	try {
	    type.process(input, output, metadata);
	    //type.process(input, System.out);
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	    index.setFailedreason(index.getFailedreason() + "tika exception " + e.getClass().getName() + " ");
	    output = null;
	} catch (Error e) {
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
	try {
	String dbfilename = el.dbfilename;
	String filename = el.filename;
	String md5 = el.md5;
	IndexFiles index = el.index;
	List<ResultItem> retlist = el.retlist;
	List<ResultItem> retlistnot = el.retlistnot;
	Metadata metadata = el.metadata;
	log.info("incTikas " + dbfilename);
	Queues.tikaTimeoutQueue.add(dbfilename);
	int size = 0;
	try {
	    OutputStream outputStream = process(filename, metadata, index);
	    InputStream inputStream = null;
	    if (outputStream != null) {
	    inputStream = new ByteArrayInputStream(((ByteArrayOutputStream) outputStream).toByteArray());
	    size = ((ByteArrayOutputStream)outputStream).size();
	    log.info("size1 " + filename + " " + size);
	    } else {
	        size = -1;
	        log.info("size1 " + filename + " crash"); 
	    }
	
	    long time = System.currentTimeMillis() - now;
	    el.index.setConverttime(time);
	    log.info("timerStop " + filename + " " + time);
	    //retlist.add(new ResultItem(new String("tika handling filename " + dbfilename + " " + size + " : " + time)));
	    //int limit = mylimit(dbfilename);
	    if (size >= 0) {
		//log.info("sizes " + size + " " + limit);
		log.info("handling filename " + dbfilename + " " + size + " : " + time);
	
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
		IndexQueueElement elem = new IndexQueueElement("all", md5, inputStream, index, retlist, retlistnot, dbfilename, metadata, el.ui);
		elem.lang = lang;
		elem.content = content;
		if (el.convertsw != null) {
		    elem.convertsw = el.convertsw;
		} else {
		    elem.convertsw = "tika";
		}
		Queues.indexQueue.add(elem);
	    } else {
	    	if (dbfilename.equals(filename)) {
		    if (filename.startsWith(FileSystemDao.HDFS)) {
			int i = filename.lastIndexOf("/");
			String fn = "/tmp/hdfs" + filename.substring(i + 1);
			log.info("copy to local filenames " + filename + " " + fn);
			FileObject file = FileSystemDao.get(filename);
			InputStream in = FileSystemDao.getInputStream(file);
			OutputStream out = new FileOutputStream(new File(fn));
			IOUtils.copy(in, out);
			in.close();
			out.close();
			el.filename = fn;
		    }
	    	    el.size = size;
	    	    Queues.otherQueue.add(el);
	    	} else {
		    log.info("Too small " + filename + " " + md5 + " " + size);
			SearchDisplay display = SearchService.getSearchDisplay(el.ui);
		    ResultItem ri = IndexFiles.getResultItem(el.index, el.index.getLanguage(), display);
		    ri.get().set(IndexFiles.FILENAMECOLUMN, dbfilename);
		    retlistnot.add(ri);
		    Boolean isIndexed = index.getIndexed();
		    if (isIndexed == null || isIndexed.booleanValue() == false) {
			index.incrFailed();
			//index.save();
		    }
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
	} catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	}
	finally {
		log.info("ending " + el.dbfilename);
	}
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
