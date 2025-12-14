package roart.classification;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import roart.common.constants.EurekaConstants;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.machinelearning.MachineLearningClassifyParam;
import roart.search.Sender;
import roart.search.Util;
import roart.common.config.NodeConfig;
import roart.common.inmemory.redis.InmemoryJedis;

public class ClassifyIT {

    @Test
    public void testClassify() throws Exception {
        InmemoryJedis ij = new InmemoryJedis("http://localhost:6379");
        String str = "This is a sample message about technology and programming.";
        String md5 = DigestUtils.md5Hex( str );
        InmemoryMessage msg = ij.send("1", str, md5);
 
        Util util = new Util(new Sender());
        MachineLearningClassifyParam param = new MachineLearningClassifyParam();
        param.configid = System.getenv("classifyconfigid");
        param.language = "en";
        param.message = msg;
        Object result = util.sender.send(param, EurekaConstants.CLASSIFY, System.getenv("classifyport"));
        System.out.println("Classification Result: " + result);

        ij.delete(msg);
    }
}
