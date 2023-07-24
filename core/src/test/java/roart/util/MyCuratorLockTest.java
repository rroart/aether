package roart.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import roart.common.synchronization.MyLock;
import roart.common.synchronization.impl.MyCuratorLock;
import roart.service.ControlService;

public class MyCuratorLockTest {
   
    @BeforeEach
    public void before() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);        
        String zookeeperConnectionString = "localhost:2181";
        ControlService.curatorClient = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        ControlService.curatorClient.start();
    }
    
    @Test
    public void test() throws Exception {
        int numberOfThreads = 4;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads * 100);
        CountDownLatch latchDone = new CountDownLatch(numberOfThreads * 100);
        for (int i = 0; i < numberOfThreads * 100; i++) {
            service.submit(() -> {
                String id = MyLocalLockTestUtil.getId();
                MyLock lock = new MyCuratorLock(id, ControlService.curatorClient);
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
