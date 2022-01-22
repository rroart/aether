package roart.convert.impl;

import java.io.File;
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

public class Calibre extends ConvertAbstract {

    private static Logger log = LoggerFactory.getLogger(Calibre.class);

    public Calibre(String nodename, NodeConfig nodeConf) {
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
        Converter converter = param.converter;
        String output = null;
        try (InputStream contentStream = inmemory.getInputStream(param.message)) {
            Path myPath = Paths.get("/tmp", param.filename);
            Files.deleteIfExists(myPath);
            Path inPath = Files.createFile(myPath);
            Files.copy(contentStream, inPath);
            String in = inPath.toString();
            Path outPath = null;
            String out = null;
            outPath = Files.createTempFile(null, ".txt");
            out = outPath.toString();
            String retlistid = null;

            String[] arg = { in, out };
            String[] ret = new String[1];
            output = ConvertUtil.executeTimeout("/usr/bin/ebook-convert", arg, retlistid, ret, converter.getTimeout());
    
            if ("end".equals(output)) {
                output = InmemoryUtil.convertWithCharset(outPath);
            } else {
                output = null;
            }
            Files.delete(inPath);
            Files.delete(outPath);
            result.error = ret[0];
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        if (output == null) {
            log.info("Calibre with no output");
            return result;
        }
        String md5 = DigestUtils.md5Hex(output );
        InmemoryMessage msg = inmemory.send(md5, output, md5);
        result.message = msg;
        return result;
    }
}
