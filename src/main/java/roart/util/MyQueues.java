package roart.util;

public class MyQueues extends MyCollections {
    
    public static MyQueue get(String id) {
        return (MyQueue) get(id, new MyQueueFactory());
     }

    public static void put(String id) {
        put(id, new MyQueueFactory());
    }
 }
