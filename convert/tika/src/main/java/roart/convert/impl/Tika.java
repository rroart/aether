package roart.convert.impl;

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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.IOUtils;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.convert.ConvertAbstract;
import roart.convert.ConvertUtil;
import roart.common.config.Converter;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.Inmemory;
import roart.common.inmemory.model.InmemoryMessage;
import org.xml.sax.ContentHandler;

//import roart.queue.TikaQueueElement;

public class Tika extends ConvertAbstract {

    private static Logger log = LoggerFactory.getLogger(Tika.class);

    public Tika(String nodename, NodeConfig nodeConf) {
        super(nodename, nodeConf);        
    }

    @Override
    public ConvertResult convert(ConvertParam param) {
        Object[] param2 = new Object[2];
        param2[0] = param;
        class OtherTimeout implements Runnable {
            public void run() {
                try {
                    convert2(param2);
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
            }
        }
        OtherTimeout otherRunnable = new OtherTimeout();
        Thread otherWorker = new Thread(otherRunnable);
        otherWorker.setName("OtherTimeout");
        otherWorker.start();
        int timeout = param.converter.getTimeout();
        long start = System.currentTimeMillis();
        boolean b = true;
        while (b) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(Constants.EXCEPTION, e);
                // TODO Auto-generated catch block
            }
            long now = System.currentTimeMillis();
            if ((now - start) > 1000 * timeout) {
                b = false;
            }
            if (!otherWorker.isAlive()) {
                log.info("Otherworker finished " + " " + otherWorker + " " + otherRunnable);
                return (ConvertResult) param2[1]; // "end"
            }
        }
        otherWorker.stop(); // .interrupt();
        return null;
    }
    
    public ConvertResult convert2(Object[] param2) {
        ConvertParam param = (ConvertParam) param2[0];
        Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
        String content = inmemory.read(param.message);
        Converter converter = param.converter;
        String output = null;
        String md5 = null;
        ConvertResult result = new ConvertResult();
        try {
            String[] ret = new String[1];
            //output = ConvertUtil.executeTimeout("/usr/bin/ebook-convert", arg, retlistid, ret);
            Map<String, String> metadata = new HashMap<>();
            result.metadata = metadata;
            String inmd5 = param.message.getId();
            InputStream is = IOUtils.toInputStream(content);
            ByteArrayOutputStream outputStream = process(is, ret, metadata, inmd5);
            InputStream inputStream = null;
            if (outputStream != null) {
                inputStream = new ByteArrayInputStream(((ByteArrayOutputStream) outputStream).toByteArray());
                //size = ((ByteArrayOutputStream)outputStream).size();
                log.info("size1 " + inmd5);
            } else {
                //size = -1;
                log.info("size1 " + inmd5 + " crash "); 
            }
            if (inputStream != null) {
                output = getString(inputStream);
                md5 = DigestUtils.md5Hex(output );
            }
            result.error = ret[0];
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        if (output == null) {
            log.info("tika no output");
        }
        if (output != null) {
            InmemoryMessage msg = inmemory.send(md5, output);
            result.message = msg;
        }
        param2[1] = result;
        return result;
    }

    public ByteArrayOutputStream process(InputStream is, String[] error, Map<String, String> map, String filename) throws Exception {
        InputStream input = TikaInputStream.get(is);
        Metadata metadata = new Metadata();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            if (false) {
                listConfig();
            }
            ParseContext context = new ParseContext();
            Detector detector = new DefaultDetector();
            Parser parser = new AutoDetectParser(detector);
            context.set(Parser.class, parser);
            ContentHandler handler = new BodyContentHandler(getOutputWriter(output, null));
            parser.parse(input, handler, metadata, context);
            /*
            //ParseContext context = new ParseContext();
            //Detector detector = new DefaultDetector();
            AutoDetectParser parser = new AutoDetectParser();
            //context.set(Parser.class, parser);
            ContentHandler handler = new BodyContentHandler();
            parser.parse(input, handler, metadata);
            InputStream targetStream = IOUtils.toInputStream(handler.toString());
            IOUtils.copy(targetStream, output);
             */
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            error[0] = "tika exception " + e.getClass().getName() + " ";
            output = null;
        } catch (java.lang.ThreadDeath e) {
            log.error("Error expected {} {}", Thread.currentThread().getId(), filename);
            log.error(Constants.ERROR, e);
            error[0] = "tika timeout " + e.getClass().getName() + " ";
            output = null;
        } catch (Error e) {
            System.gc();
            log.error("Error {} {}", Thread.currentThread().getId(), filename);
            log.error(Constants.ERROR, e);
            error[0] = "tika error " + e.getClass().getName() + " ";
            output = null;
        } finally {
            input.close();
            System.out.flush();
        }
        for (String name : metadata.names()) {
            String value = metadata.get(name);
            map.put(name, value);
        }
        log.info("Metadata {}", "" + map);
        return output;
    }

    private void listConfig() {
        ParseContext context = new ParseContext();
        TikaConfig config = TikaConfig.getDefaultConfig();
        // Get the root parser
        CompositeParser parser = (CompositeParser)config.getParser();
        // Fetch the types it supports
        for (MediaType type : parser.getSupportedTypes(new ParseContext())) {
            String typeStr = type.toString();
            log.info("typestr"+typeStr);
        }
        // Fetch the parsers that make it up (note - may need to recurse if any are a CompositeParser too)
        for (Parser p : parser.getAllComponentParsers()) {
            String parserName = p.getClass().getName();
            log.info("pnam"+parserName);
            if (p instanceof CompositeParser) {
                // Check child ones too
            }
        }
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

    public String getString(InputStream inputStream) {
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
