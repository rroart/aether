package roart.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MySetFactory extends MyFactory {
    
    public MySet create(String setid) {
        if (roart.service.ControlService.distributedtraverse) {
            return new MyHazelcastSet(setid);
        } else {
            return new MyJavaSet();
        }
    }
}
