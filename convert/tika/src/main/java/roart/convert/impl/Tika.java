package roart.convert.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;
import roart.common.inmemory.common.Inmemory;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.inmemory.util.InmemoryUtil;
import roart.common.util.JsonUtil;
import roart.common.zkutil.ZKUtil;
import roart.convert.ConvertAbstract;

//import roart.queue.TikaQueueElement;

public class Tika extends ConvertAbstract {

    private static Logger log = LoggerFactory.getLogger(Tika.class);

    public Tika(String configname, String configid, NodeConfig nodeConf, CuratorFramework curatorClient) {
        super(configname, configid, nodeConf, curatorClient);        
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
        if (nodeConf.wantsTikaOCR()) {
            Integer timeoutOCR = nodeConf.getTikaOCRTimeout();
            if (timeoutOCR != null && timeoutOCR > 0) {
                timeout = timeoutOCR;
            }
        }
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
                log.info("Otherworker finished {} {}", otherWorker, otherRunnable);
                return (ConvertResult) param2[1]; // "end"
            }
        }
        // note ThreadDeath still in use, still works as of Java 17
        otherWorker.stop(); // .interrupt();
        log.error("Otherworker killed {} {}", otherWorker, otherRunnable);
        return (ConvertResult) param2[1];
   }

    public ConvertResult convert2(Object[] param2) {
        ConvertResult result = new ConvertResult();
        ConvertParam param = (ConvertParam) param2[0];
        param2[1] = result;
        Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
        try (InputStream content = inmemory.getInputStream(param.message)) {
            if (!InmemoryUtil.validate(param.message.getMd5(), content)) {
                log.error("Invalid msg {}", param.message.getMd5());
                return result;
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        String inmd5 = param.message.getId();
        Map<String, String> metadata = new HashMap<>();
        result.metadata = metadata;
        String[] ret = new String[1];
        try (
                InputStream content = inmemory.getInputStream(param.message);
                ByteArrayOutputStream outputStream = process(content, ret, metadata, inmd5);
                ) {
            if (outputStream != null) {
                byte[] outputArray = outputStream.toByteArray();
                String md5 = DigestUtils.md5Hex(outputArray);
                log.info("Size {} {} {}", inmd5, md5, outputArray.length);
                if (outputArray.length > 0) {
                    InmemoryMessage msg = inmemory.send(EurekaConstants.CONVERT + param.message.getId(), new ByteArrayInputStream(outputArray), md5);
                    result.message = msg;
                    curatorClient.create().creatingParentsIfNeeded().forPath(ZKUtil.getAppidPath(Constants.DATA) + msg.getId(), JsonUtil.convert(msg).getBytes());
                } else {
                    result.error = this.getClass().getSimpleName() + " " + "Tika empty";
                }
                //param2[1] = result;
            } else {
                log.error("Tika with no output for {}", inmd5);
                result.error = this.getClass().getSimpleName() + " no output " + ret[0];
                //param2[1] = result;
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return result;
    }

    public ByteArrayOutputStream process(InputStream is, String[] error, Map<String, String> map, String filename) {
        Metadata metadata = new Metadata();
        try (
                InputStream input = TikaInputStream.get(is);
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ) {
            if (false) {
                listConfig();
            }
            ParseContext context = new ParseContext();
            log.info("OCR {}", nodeConf.wantsTikaOCR());
            if (nodeConf.wantsTikaOCR()) {
                PDFParserConfig pdfConfig = new PDFParserConfig();
                pdfConfig.setExtractInlineImages(false);
                context.set(PDFParserConfig.class, pdfConfig);
                // djvu?
                //context.set(TesseractOCRConfig.class, new TesseractOCRConfig());
            } else {
                //PDFParserConfig pdfConfig = new PDFParserConfig();
                //pdfConfig.setExtractInlineImages(false);
                //context.set(PDFParserConfig.class, pdfConfig);
                // djvu?
                TesseractOCRConfig ocrConfig = new TesseractOCRConfig();
                ocrConfig.setSkipOcr(true);
                context.set(TesseractOCRConfig.class, ocrConfig);
            }
            Detector detector = new DefaultDetector();
            Parser parser = new AutoDetectParser(detector);
            context.set(Parser.class, parser);
            ContentHandler handler = new BodyContentHandler(getOutputWriter(output, null));
            parser.parse(input, handler, metadata, context);
            for (String name : metadata.names()) {
                String value = metadata.get(name);
                map.put(name, value);
            }
            log.info("Metadata {}", map);
            return output;
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
        } catch (java.lang.ThreadDeath e) {
            // note ThreadDeath still in use
            log.error("Error expected {} {}", Thread.currentThread().getId(), filename);
            log.error(Constants.ERROR, e);
            error[0] = "tika timeout " + e.getClass().getName() + " ";
        } catch (Error e) {
            System.gc();
            log.error("Error {} {}", Thread.currentThread().getId(), filename);
            log.error(Constants.ERROR, e);
            error[0] = "tika error " + e.getClass().getName() + " ";
        }
        return null;
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
