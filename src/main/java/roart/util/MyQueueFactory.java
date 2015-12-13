package roart.util;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MyQueueFactory extends MyFactory {
    
    public MyQueue create(String listid) {
        if (roart.service.ControlService.distributedtraverse) {
            return new MyHazelcastQueue(listid);
        } else {
            return new MyJavaQueue();
        }
    }
}
