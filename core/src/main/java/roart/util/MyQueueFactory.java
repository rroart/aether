package roart.util;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import roart.config.MyConfig;

public class MyQueueFactory extends MyFactory {
    
    public MyQueue create(String listid) {
        if (MyConfig.conf.wantDistributedTraverse()) {
            return new MyHazelcastQueue(listid);
        } else {
            return new MyJavaQueue();
        }
    }
}
