package roart.model;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import roart.common.collections.MyList;
import roart.common.config.MyConfig;

public class MyListFactory extends MyFactory {
    
    public MyList create(String listid) {
        if (MyConfig.conf.wantDistributedTraverse()) {
            return new MyHazelcastList(listid);
        } else {
            return new MyJavaList();
        }
    }
}
