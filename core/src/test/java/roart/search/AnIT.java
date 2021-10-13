package roart.search;

import org.junit.jupiter.api.Test;

import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.common.service.ServiceParam;
import roart.common.service.ServiceParam.Function;
import roart.common.service.ServiceResult;
import roart.eureka.util.EurekaUtil;

public class AnIT {

    @Test
    public void myTest() {
        
    }
    @Test
    public void my2Test() throws Exception {
        new Util(new Sender()).traverse("/home/roart/src/aethermicro/books");
    }
    
    
}
