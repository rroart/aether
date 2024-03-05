package roart.common.collections.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

import roart.common.collections.MyQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RedisTest {
    @Test
    public void concurrencyJavaTest() throws Exception {
        MyQueue<String> queue = new MyJavaQueue<String>();
        concurrencyTest(queue);
    }
    
    @Test
    public void concurrencyJedisTest() throws Exception {
        MyQueue<String> queue = new MyRedisQueue<String>("http://localhost:6379", "num");
        concurrencyTest(queue);
    }
    
    @Test
    public void concurrencyRedissonTest() throws Exception {
        MyQueue<String> queue = new MyRedissonQueue<String>("redis://localhost:6379", "num");
        concurrencyTest(queue);
    }
    
    @Test
    public void concurrencyMoreJedisTest() throws Exception {
        MyQueue<String>[] queues = new MyQueue[3];
        queues[0] = new MyRedisQueue<String>("http://localhost:6379", "num");
        queues[1] = new MyRedisQueue<String>("http://free:6379", "num");
        queues[2] = new MyRedisQueue<String>("http://127.0.0.1:6379", "num");
        concurrencyTest(queues);
    }
    
    public void concurrencyTest(MyQueue queue) throws Exception {
       for (int i = 0; i < 163830; i++) {
            queue.offer("" + i);
        }
        //System.out.println("p" + queue.poll());
        ExecutorService service = Executors.newFixedThreadPool(10);
        final Set<String>[] sets = new HashSet[3];
        sets[0] = new HashSet<>();
        sets[1] = new HashSet<>();
        sets[2] = new HashSet<>();
        
        for (int i = 0; i < 3; i++) {
            final int iii = i;
            service.submit(() -> {
                //private final int iii = i;
                final Set<String> set = sets[iii];
                try {
                    String outs = "";
                    while (outs != null) {        
                        final String s = (String) queue.poll();
                        //System.out.println("p" + iii + " " + queue.poll());
                        if (s != null) {
                            set.add(s);
                        }
                        outs = s;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        try {
            Thread.sleep(10 * 1000);
        } catch (Exception e) {
            
        }
        for (int i = 0; i < 3; i++) {
            System.out.println("eln " + sets[i].size());
        }
        final Set<String>[] setsCopy = new HashSet[3];
        for (int i = 0; i < 3; i++) {
            setsCopy[i] = new HashSet(sets[i]);
            setsCopy[i].removeAll(sets[i]);
            //System.out.println("")
            assertEquals(0, setsCopy[i].size());
       }
    }
    
    public void concurrencyTest(final MyQueue[] queue) throws Exception {
        for (int i = 0; i < 163830; i++) {
             queue[0].offer("" + i);
         }
         //System.out.println("p" + queue.poll());
         ExecutorService service = Executors.newFixedThreadPool(10);
         final Set<String>[] sets = new HashSet[3];
         sets[0] = new HashSet<>();
         sets[1] = new HashSet<>();
         sets[2] = new HashSet<>();
         
         for (int i = 0; i < 3; i++) {
             final int iii = i;
             service.submit(() -> {
                 //private final int iii = i;
                 final Set<String> set = sets[iii];
                 try {
                     String outs = "";
                     while (outs != null) {        
                         final String s = (String) queue[iii].poll();
                         //System.out.println("p" + iii + " " + queue.poll());
                         if (s != null) {
                             set.add(s);
                         }
                         outs = s;
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             });
         }
         try {
             Thread.sleep(10 * 1000);
         } catch (Exception e) {
             
         }
         for (int i = 0; i < 3; i++) {
             System.out.println("eln " + sets[i].size());
         }
         final Set<String>[] setsCopy = new HashSet[3];
         for (int i = 0; i < 3; i++) {
             setsCopy[i] = new HashSet(sets[i]);
             setsCopy[i].removeAll(sets[i]);
             //System.out.println("")
             assertEquals(0, setsCopy[i].size());
        }
     }
     
}
