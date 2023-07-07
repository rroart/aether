package roart.model;

public class MyAtomicLongs extends MyCollections {
    
   public static MyAtomicLong get(String id) {
       return (MyAtomicLong) get(id, new MyAtomicLongFactory());
    }

   public static void put(String id) {
       put(id, new MyAtomicLongFactory());
   }

}
