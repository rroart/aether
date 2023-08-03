package roart.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.synchronization.MySemaphore;
import roart.common.synchronization.impl.MyCuratorSemaphore;
import roart.dir.TraverseFile;
import roart.service.ControlService;

public class MyCuratorSemaphoreTest {

    private static Logger log = LoggerFactory.getLogger(MyCuratorSemaphoreTest.class);

    private static Random rand = new Random();
    
    ControlService controlService = mock(ControlService.class);
    @BeforeEach
    public void before() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);        
        String zookeeperConnectionString = "localhost:2181";
        controlService.curatorClient = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        controlService.curatorClient.start();
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
                MySemaphore lock = new MyCuratorSemaphore(id, controlService.curatorClient);
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
    
    @Test
    public void trytest() throws Exception {
        int numberOfThreads = 4;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads * 100);
        CountDownLatch latchDone = new CountDownLatch(numberOfThreads * 100);
        for (int i = 0; i < numberOfThreads * 100; i++) {
            service.submit(() -> {
                String id = MyLocalLockTestUtil.getId();
                MySemaphore lock = new MyCuratorSemaphore(id, controlService.curatorClient);
                try {
                    boolean locked = lock.tryLock();
                    if (locked) { 
                        lock.unlock();
                    }
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
    
    @Test
    public void try1test() throws Exception {
        int n = 10;
        int numberOfThreads = 4;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads * n);
        CountDownLatch latchDone = new CountDownLatch(numberOfThreads * n);
        for (int i = 0; i < numberOfThreads * n; i++) {
            log.info("Cnt {}", i);
            service.submit(() -> {
                String id = "a"; // MyLocalLockTestUtil.getId();
                MySemaphore lock = new MyCuratorSemaphore(id, controlService.curatorClient);
                try {
                    Thread.sleep(1);
                    Thread.sleep(10 + rand.nextInt(100), 0);
                    boolean locked = lock.tryLock();
                    if (locked) { 
                        //Thread.sleep(10 + rand.nextInt(100), 0);
                        Thread.sleep(10000);
                        lock.unlock();
                    } else {
                        System.out.println("not");
                        log.info("not");
                    }
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
    
    @Test
    public void try2test() throws Exception {
        MySemaphore locka1 = new MyCuratorSemaphore("a", controlService.curatorClient);
        MySemaphore locka2 = new MyCuratorSemaphore("b", controlService.curatorClient);
        MySemaphore locka3 = new MyCuratorSemaphore("a", controlService.curatorClient);
        locka1.tryLock();
        locka2.tryLock();
        locka2.unlock();
        locka1.unlock();
    }
}
