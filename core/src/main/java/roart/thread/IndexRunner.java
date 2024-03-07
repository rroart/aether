package roart.thread;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MyQueue;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.queue.QueueElement;
import roart.common.synchronization.MySemaphore;
import roart.common.synchronization.impl.MySemaphoreFactory;
import roart.hcutil.GetHazelcastInstance;
import roart.queue.Queues;
import roart.search.Search;
import roart.service.ControlService;

public class IndexRunner implements Runnable {

    private static Logger log = LoggerFactory.getLogger(IndexRunner.class);

    public static volatile int timeout = 3600;

    private NodeConfig nodeConf;

    private ControlService controlService;

    public IndexRunner(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
    }

    public void run() {
        int nThreads = ControlRunner.getThreads();
        nThreads = nodeConf.getMPThreadsIndex();
        int running = 0;
        log.info("nthreads {}", nThreads);

        for(int i = running; i < nThreads; i++) {
            Runnable run = () -> {
                try {
                    Queue<MySemaphore> semaphores = new ConcurrentLinkedQueue<>();
                    while (true) {
                        if (new Queues(nodeConf, controlService).getIndexQueueSize() == 0) {
                            log.info("Index queue empty, sleeping");
                            try {
                                TimeUnit.SECONDS.sleep(10);
                            } catch (InterruptedException e) {
                                log.error(Constants.EXCEPTION, e);
                            }
                            continue;
                        }
                        doIndexTimeout(semaphores);
                    }
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                } catch (Error e) {
                    System.gc();
                    log.error("Error " + Thread.currentThread().getId());
                    log.error(Constants.ERROR, e);
                }
                finally {
                    //log.info("myend");
                }
                return; //myMethod();
            };      
            new Thread(run).start();
        }
    }

    public String doIndexTimeout(Queue<MySemaphore> semaphores) {
        class IndexTimeout implements Runnable {
            private QueueElement el;
            IndexTimeout(QueueElement el) {
                this.el = el;
            }

            public void run() {
                try {
                    if (nodeConf.wantAsync()) {
                        new Search(nodeConf, controlService).indexmeQueue(el);
                    } else {
                        new Search(nodeConf, controlService).indexme(el);
                    }
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
            }
        }

        MyQueue<QueueElement> queue = new Queues(nodeConf, controlService).getIndexQueue();
        QueueElement el = queue.poll(QueueElement.class);
        if (el == null) {
            log.error("empty queue");
            return null;
        }
        try {
            if (!nodeConf.wantAsync()) {
                MySemaphore lock = MySemaphoreFactory.create(el.getMd5(), nodeConf.getLocker(), controlService.curatorClient, GetHazelcastInstance.instance(nodeConf));
                boolean locked = lock.tryLock();
                if (!locked) {
                    queue.offer(el);
                    return null;
                }
                el.getIndexFiles().setSemaphorelock(lock);
                el.getIndexFiles().setSemaphorelockqueue(semaphores);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            queue.offer(el);
        }
        IndexTimeout indexRunnable = new IndexTimeout(el);
        Thread indexWorker = new Thread(indexRunnable);
        indexWorker.setName("IndexTimeout");
        indexWorker.start();
        try {
            unlockSemaphores(semaphores);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e); 
        }                   
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            log.error(Constants.EXCEPTION, e); 
        }                   
        return null;
    }

    private void unlockSemaphores(Queue<MySemaphore> locks) {
        if (!locks.isEmpty()) {
            log.info("unlock");
        }
        while (!locks.isEmpty()) {
            MySemaphore lock = locks.poll();
            lock.unlock();
            if (locks.isEmpty()) {
                log.info("unlock");
            }
        }
    }

}
