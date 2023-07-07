package roart.model;

public class MyLists extends MyCollections {
    
    public static MyList get(String id) {
        return (MyList) get(id, new MyListFactory());
     }

    public static void put(String id) {
        put(id, new MyAtomicLongFactory());
    }
 }
