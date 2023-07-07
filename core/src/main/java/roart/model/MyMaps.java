package roart.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import roart.common.collections.MyMap;

public class MyMaps extends MyCollections {
    
    public static MyMap get(String id) {
        return (MyMap) get(id, new MyMapFactory());
     }

    public static void put(String id) {
        put(id, new MyMapFactory());
    }
}
