package roart.content;

import java.io.*;

import java.util.Arrays;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TikaHandler {
    private Log log = LogFactory.getLog(this.getClass());

    private class NoDocumentMetHandler extends DefaultHandler{

        protected PrintWriter writer;

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

        public void process(InputStream input, OutputStream output)
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

    private Metadata metadata;

    private OutputType type = TEXT;

    private LanguageProfilerBuilder ngp = null;

    /**                                                                         
     * Output character encoding, or <code>null</code> for platform default     
     */
    private String encoding = null;

    private boolean pipeMode = true;

    private boolean serverMode = false;

    private boolean fork = false;

    private String profileName = null;

    private boolean prettyPrint;

    public void TikaHandler() throws Exception {
        context = new ParseContext();
        detector = new DefaultDetector();
        parser = new AutoDetectParser(detector);
        context.set(Parser.class, parser);
    }

    public ByteArrayOutputStream process(String filename) throws Exception {
	//TikaHandler();
        context = new ParseContext();
        detector = new DefaultDetector();
        parser = new AutoDetectParser(detector);
        context.set(Parser.class, parser);

	pipeMode = false;
	type = TEXT;
	metadata = new Metadata();
	File file = new File(filename);
	URL url = null;
	if (file.isFile()) {
	    url = file.toURI().toURL();
	}
	InputStream input = TikaInputStream.get(url, metadata);
	//PipedInputStream  writeIn = new PipedInputStream();
	//PipedOutputStream output = new PipedOutputStream(writeIn);
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	try {
	    type.process(input, output);
	    //type.process(input, System.out);
	} finally {
	    input.close();
	    System.out.flush();
	}
	return output;
    }

    private static Writer getOutputWriter(OutputStream output, String encoding)
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

}