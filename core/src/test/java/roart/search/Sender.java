package roart.search;

import roart.common.service.ServiceParam;
import roart.common.webflux.WebFluxUtil;

public class Sender {
    public Object send(Object param, String webpath) {
        return WebFluxUtil.sendMe(Object.class, "http://localhost:23456/", param, webpath);
        
    }
    public Object send(Object param, String webpath, String port) {
        return WebFluxUtil.sendMe(Object.class, "http://localhost:" + port + "/", param, webpath);
        
    }
}
