package roart.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import roart.config.MyConfig;

public class MySetFactory extends MyFactory {
    
    public MySet create(String setid) {
        if (MyConfig.conf.distributedtraverse) {
            return new MyHazelcastSet(setid);
        } else {
            return new MyJavaSet();
        }
    }
}
