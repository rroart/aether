package roart.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

import roart.common.synchronization.MyLock;
import roart.common.synchronization.impl.MyHazelcastLock;
import roart.hcutil.GetHazelcastInstance;

public class MyHazelcastLockTest {
   
    @Test
    public void test() throws Exception {
        int numberOfThreads = 4;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads * 100);
        CountDownLatch latchDone = new CountDownLatch(numberOfThreads * 100);
        for (int i = 0; i < numberOfThreads * 100; i++) {
            service.submit(() -> {
                String id = MyLocalLockTestUtil.getId();
                MyLock lock = new MyHazelcastLock(id, GetHazelcastInstance.instance("localhost"));
                try {
                    lock.lock();
                    lock.unlock();
                    latchDone.countDown();
               } catch (Exception e) {
                    System.out.println(e);
                    e.printStackTrace();
                    System.out.println("Num " + latch.getCount());
                }
                latch.countDown();
            });
        }
        latch.await();
        assertEquals(0, latchDone.getCount());
    }
}
