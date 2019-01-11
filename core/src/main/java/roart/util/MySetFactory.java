package roart.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import roart.common.collections.MySet;
import roart.common.config.MyConfig;

public class MySetFactory extends MyFactory {
    
    public MySet create(String setid) {
        if (MyConfig.conf.wantDistributedTraverse()) {
            return new MyHazelcastSet(setid);
        } else {
            return new MyJavaSet();
        }
    }
}
