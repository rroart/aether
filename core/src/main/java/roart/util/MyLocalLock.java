package roart.util;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import roart.common.synchronization.MyLock;

public class MyLocalLock extends MyLock {

    private static ConcurrentHashMap<String, ReentrantLock> map = new ConcurrentHashMap();
    
    private String path;
    
    @SuppressWarnings("squid:S2222")
    @Override
    public void lock(String path) throws Exception {
        log.info("before lock {}", path);
        //Lock lock = map.get(path);
        //synchronized (MyLocalLock.class) {
                 //if (lock == null) {
        lock = map.computeIfAbsent(path, e -> new ReentrantLock());
       // }
        //}
        lock.lock();
        log.info("after lock {}", path);
        this.path = path;
    }

    ReentrantLock lock;
    
    @Override
    public void unlock() {
        synchronized (MyLocalLock.class) {
        /*
            lock = map.get(path);
        if (lock == null) {
            System.out.println("Map " + path + " " + map);
        }
        */
        if (!lock.hasQueuedThreads()) {
            map.remove(path);
            if (lock.hasQueuedThreads()) {
                map.put(path, lock);
                //System.out.println("Back " + path);
            }
            //System.out.println("Rem " + path + " " + lock.getHoldCount() + " " + lock.getQueueLength());
        } else {
            //System.out.println("Lock " + path + " " + lock.getHoldCount() + " " + lock.getQueueLength());
        }
        lock.unlock();
        }
        log.info("after unlock {}", path);
    }

}