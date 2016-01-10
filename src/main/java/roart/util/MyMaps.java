package roart.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MyMaps extends MyCollections {
    
    public static MyMap get(String id) {
        return (MyMap) get(id, new MyMapFactory());
     }

    public static void put(String id) {
        put(id, new MyMapFactory());
    }
}
