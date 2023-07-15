package roart.model;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import roart.common.collections.MySet;
import roart.common.config.MyConfig;

public class MySetFactory extends MyFactory {
    
    public MySet create(String setid) {
        if (MyConfig.conf.wantDistributedTraverse()) {
            if (MyConfig.conf.getRedis() != null) {
                return new MyRedisSet(MyConfig.conf.getRedis(), setid);
            } else {
                return new MyHazelcastSet(setid);
            }
        } else {
            return new MyJavaSet();
        }
    }
}
