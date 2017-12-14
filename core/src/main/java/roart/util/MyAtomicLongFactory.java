package roart.util;

import roart.config.MyConfig;

public class MyAtomicLongFactory extends MyFactory {

    public MyAtomicLong create(String listid) {
        if (MyConfig.conf.wantDistributedTraverse()) {
            return new MyHazelcastAtomicLong(listid);
        } else {
            return new MyJavaAtomicLong();
        }
    }
}