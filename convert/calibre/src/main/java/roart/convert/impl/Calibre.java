package roart.convert.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.Converter;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.Inmemory;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.inmemory.model.InmemoryUtil;
import roart.convert.ConvertAbstract;
import roart.convert.ConvertUtil;

public class Calibre extends ConvertAbstract {

    private static Logger log = LoggerFactory.getLogger(Calibre.class);

    public Calibre(String nodename, NodeConfig nodeConf) {
        super(nodename, nodeConf);        
    }

    @Override
    public ConvertResult convert(ConvertParam param) {
        ConvertResult result = new ConvertResult();
        Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
        try (InputStream validateStream = inmemory.getInputStream(param.message)) {
            if (!InmemoryUtil.validate(param.message.getMd5(), validateStream)) {
                return result;
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        String output = null;
        Converter converter = param.converter;
        Path inPath = Paths.get("/tmp", param.filename);
        try (InputStream contentStream = inmemory.getInputStream(param.message)) {
            Files.deleteIfExists(inPath);
            Files.copy(contentStream, inPath);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            log.error("File copy error");
            result.error = e.getMessage();
            return result;
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
        output = ConvertUtil.executeTimeout("/usr/bin/ebook-convert", arg, retlistid, ret, converter.getTimeout());
        if (new File(out).length() == 0) {
            output = null;
        }
        if ("end".equals(output)) {
            String md5 = null;
            try (InputStream md5is = new FileInputStream(out)) {
                md5 = DigestUtils.md5Hex(md5is);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
            try (InputStream is = new FileInputStream(out)) {
                InmemoryMessage msg = inmemory.send(EurekaConstants.CONVERT + param.message.getId(), is, md5);
                result.message = msg;
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
        try {
            Files.delete(inPath);
            Files.delete(outPath);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        if (output == null) {
            log.info("Calibre with no output");
            result.error = ret[0];
            return result;
        }
        return result;
    }
}
