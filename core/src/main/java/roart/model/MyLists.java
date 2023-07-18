package roart.model;

import roart.common.collections.MyList;

public class MyLists extends MyCollections {
    
    public static MyList get(String id) {
        return (MyList) get(id, new MyListFactory());
     }

    public static void put(String id) {
        put(id, new MyAtomicLongFactory());
    }
 }
