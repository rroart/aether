package roart.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import roart.common.collections.MyMap;
import roart.common.config.MyConfig;

public class MyMapFactory extends MyFactory {
    
    public MyMap create(String mapid) {
        if (MyConfig.conf.wantDistributedTraverse()) {
            return new MyHazelcastMap(mapid);
        } else {
            return new MyJavaMap();
        }
    }
}
