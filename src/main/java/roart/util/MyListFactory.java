package roart.util;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MyListFactory extends MyFactory {
    
    public MyList create(String listid) {
        if (roart.service.ControlService.distributedtraverse) {
            return new MyHazelcastList(listid);
        } else {
            return new MyJavaList();
        }
    }
}
