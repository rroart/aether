package roart.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

import roart.common.synchronization.MyLock;
import roart.model.MyLocalLock;

public class MyLocalLockTest {
   
    @Test
    public void test() throws Exception {
        int numberOfThreads = 4;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads * 100);
        CountDownLatch latchDone = new CountDownLatch(numberOfThreads * 100);
        for (int i = 0; i < numberOfThreads * 100; i++) {
            service.submit(() -> {
                MyLock lock = new MyLocalLock();
                String id = MyLocalLockTestUtil.getId();
                try {
                    lock.lock(id);
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
