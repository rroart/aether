package roart.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import roart.common.collections.MySet;

public class MySets extends MyCollections {
    
    public static MySet get(String id) {
        return (MySet) get(id, new MySetFactory());
     }

    public static void put(String id) {
        put(id, new MySetFactory());
    }
}
