package roart.model;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import roart.common.collections.MyQueue;
import roart.common.config.MyConfig;

public class MyQueueFactory extends MyFactory {
    
    public MyQueue create(String listid) {
        if (MyConfig.conf.wantDistributedTraverse()) {
            if (MyConfig.conf.getRedis() != null) {
                return new MyRedisQueue(MyConfig.conf.getRedis(), listid);
            } else {
                return new MyHazelcastQueue(listid);
            }
        } else {
            return new MyJavaQueue();
        }
    }
}
