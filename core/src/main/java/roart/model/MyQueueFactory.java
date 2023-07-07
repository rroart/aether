package roart.model;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import roart.common.config.MyConfig;

public class MyQueueFactory extends MyFactory {
    
    public MyQueue create(String listid) {
        if (MyConfig.conf.wantDistributedTraverse()) {
            return new MyHazelcastQueue(listid);
        } else {
            return new MyJavaQueue();
        }
    }
}
