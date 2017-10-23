package roart.util;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import roart.config.MyConfig;

public class MyListFactory extends MyFactory {
    
    public MyList create(String listid) {
        if (MyConfig.conf.wantDistributedTraverse()) {
            return new MyHazelcastList(listid);
        } else {
            return new MyJavaList();
        }
    }
}
