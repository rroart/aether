package roart.convert.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
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
import roart.common.inmemory.model.InmemoryUtil;

//import roart.queue.TikaQueueElement;

public class Pdftotext extends ConvertAbstract {

    private static Logger log = LoggerFactory.getLogger(Pdftotext.class);

    public Pdftotext(String nodename, NodeConfig nodeConf) {
        super(nodename, nodeConf);        
    }

    @Override
    public ConvertResult convert(ConvertParam param) {
        ConvertResult result = new ConvertResult();
        Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
        InputStream validateStream = inmemory.getInputStream(param.message);
        if (!InmemoryUtil.validate(param.message.getMd5(), validateStream)) {
            return result;
        }        
        String output = null;
        Converter converter = param.converter;
        Path inPath = Paths.get("/tmp", param.filename);
        try (InputStream contentStream = inmemory.getInputStream(param.message)) {
            Files.deleteIfExists(inPath);
            Files.copy(contentStream, inPath);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        String in = inPath.toString();
        Path outPath = null;
        String out = null;
        try {
            outPath = Files.createTempFile(null, ".txt");
            out = outPath.toString();
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        String retlistid = null;

        String[] arg = { in, out };
        String[] ret = new String[1];
        output = ConvertUtil.executeTimeout("/usr/bin/pdftotext", arg, retlistid, ret, converter.getTimeout());
        if ("end".equals(output)) {
            String md5 = null;
            try (InputStream md5is = new FileInputStream(out)) {
                md5 = DigestUtils.md5Hex(md5is);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
            try (InputStream is = new FileInputStream(out)) {
                InmemoryMessage msg = inmemory.send(md5, is, md5);
                result.message = msg;
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
            try (InputStream is = new FileInputStream(outPath.toString())) {
                Files.delete(inPath);
                Files.delete(outPath);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
        if (output == null) {
            log.info("Calibre with no output");
            result.error = ret[0];
            return result;
        }
        return result;
    }
}
