package roart.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import roart.config.MyConfig;

public class MyMapFactory extends MyFactory {
    
    public MyMap create(String mapid) {
        if (MyConfig.conf.distributedtraverse) {
            return new MyHazelcastMap(mapid);
        } else {
            return new MyJavaMap();
        }
    }
}
