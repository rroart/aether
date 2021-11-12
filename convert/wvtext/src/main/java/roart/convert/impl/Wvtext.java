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

//import roart.queue.TikaQueueElement;

public class Wvtext extends ConvertAbstract {

    private static Logger log = LoggerFactory.getLogger(Wvtext.class);

    public Wvtext(String nodename, NodeConfig nodeConf) {
        super(nodename, nodeConf);        
    }

    @Override
    public ConvertResult convert(ConvertParam param) {
        Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
        String content = inmemory.read(param.message);
        Converter converter = param.converter;
        String output = null;
        ConvertResult result = new ConvertResult();
        try {
            Path inPath = Files.createFile(Paths.get("/tmp", param.filename));
            Files.write(inPath, content.getBytes());
            String in = inPath.toString();
            Path outPath = null;
            String out = null;
            outPath = Files.createTempFile(null, ".txt");
            out = outPath.toString();
            String retlistid = null;

            String[] arg = { in, out };
            String[] ret = new String[1];
            output = ConvertUtil.executeTimeout("/usr/bin/wvText", arg, retlistid, ret, converter.getTimeout());
            if ("end".equals(output)) {
                output = new String(Files.readAllBytes(outPath));
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
            log.info("Wvtext with no output");
        }
        String md5 = DigestUtils.md5Hex(output );
        InmemoryMessage msg = inmemory.send(md5, output);
        result.message = msg;
        return result;
    }
}
