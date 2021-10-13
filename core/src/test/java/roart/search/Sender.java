package roart.search;

import roart.common.service.ServiceParam;
import roart.eureka.util.EurekaUtil;

public class Sender {
    public Object send(Object param, String webpath) {
        return EurekaUtil.sendMe(Object.class, "http://localhost:23456/", param, webpath);
        
    }
}
