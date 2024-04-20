package roart.thread;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MyQueue;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.queue.QueueElement;
import roart.common.synchronization.MyObjectLock;
import roart.common.synchronization.MyObjectLockData;
import roart.common.synchronization.MySemaphore;
import roart.common.synchronization.impl.MyObjectLockFactory;
import roart.common.synchronization.impl.MySemaphoreFactory;
import roart.common.util.TimeUtil;
import roart.content.ConvertHandler;
import roart.common.hcutil.GetHazelcastInstance;
import roart.queue.Queues;
import roart.service.ControlService;
import roart.util.TraverseUtil;

public class ConvertRunner implements Runnable {

    private static Logger log = LoggerFactory.getLogger(ConvertRunner.class);

    public static volatile int timeout = 3600;

    final int NTHREDS = 2;

    private NodeConfig nodeConf;

    private ControlService controlService;

    public ConvertRunner(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
    }

    public NodeConfig getNodeConf() {
        return nodeConf;
    }

    public void setNodeConf(NodeConfig nodeConf) {
        this.nodeConf = nodeConf;
    }

    public void run() {
        int nThreads = ControlRunner.getThreads();
        nThreads = nodeConf.getMPThreadsConvert();
        int running = 0;
        log.info("nthreads {}", nThreads);

        for(int i = running; i < nThreads; i++) {
            Runnable run = () -> {
                Queue<MySemaphore> semaphores = new ConcurrentLinkedQueue<>();
                while (true) {
                    try {
                        unlockSemaphores(semaphores);
                        if (new Queues(nodeConf, controlService).getConvertQueueSize() == 0) {
                            log.debug("Convert queue empty, sleeping");
                            TimeUtil.sleep(10);
                            continue;
                        }
                        if (new Queues(nodeConf, controlService).indexQueueHeavyLoaded()) {
                            log.info("Index queue heavy loaded, sleeping");
                            TimeUtil.sleep(1);
                            continue;
                        }
                        new Queues(nodeConf, controlService).incConverts();
                        doConvertTimeout(nodeConf, semaphores);
                        new Queues(nodeConf, controlService).decConverts();
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e); 
                        TimeUtil.sleep(10);
                    }
                }
            };      
            new Thread(run).start();
        }
        try {
            TimeUnit.DAYS.sleep(1000);
            return;
        } catch (InterruptedException e) {
            log.error(Constants.EXCEPTION, e); 
        }                   
    }

    public String doConvertTimeout(NodeConfig nodeConf, Queue<MySemaphore> semaphores) {
        class ConvertTimeout implements Runnable {
            private QueueElement el;
            ConvertTimeout(QueueElement el) {
                this.el = el;
            }

            public void run() {
                try {
                    TraverseUtil.doCounters(el, 1, nodeConf, controlService);
                    if (nodeConf.wantAsync()) {
                        new ConvertHandler(nodeConf, controlService).doConvertQueue(el, nodeConf);
                    } else {
                        new ConvertHandler(nodeConf, controlService).doConvert(el, nodeConf);
                    }
                    TraverseUtil.doCounters(el, -1, nodeConf, controlService);
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                } catch (Error e) {
                    log.error("Error {} {}", Thread.currentThread().getId(), el.getMd5());
                    log.error(Constants.ERROR, e);
                    TraverseUtil.doCounters(el, -1, nodeConf, controlService);
                }
            }
        }

        MyQueue<QueueElement> queue = new Queues(nodeConf, controlService).getConvertQueue();
        QueueElement element = null;
        try {
            element = queue.poll(QueueElement.class);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e); 
        }
        if (element == null) {
            log.error("empty queue");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                log.error(Constants.EXCEPTION, e);
            }
            return null;
        }

        try {
            if (nodeConf.wantAsync()) {
                MyObjectLock lock = MyObjectLockFactory.create(element.getMd5(), nodeConf.getLocker(), controlService.curatorClient);
                boolean locked = lock.tryLock(element.getId());
                if (locked) {
                    queue.offer(element);
                    return null;
                }       
                element.getIndexFiles().setObjectlock(new MyObjectLockData(element.getMd5()));
            } else {
                MySemaphore lock = MySemaphoreFactory.create(element.getMd5(), nodeConf.getLocker(), controlService.curatorClient, GetHazelcastInstance.instance(nodeConf));
                boolean locked = lock.tryLock();
                if (!locked) {
                    queue.offer(element);
                    return null;
                }
                element.getIndexFiles().setSemaphorelock(lock);
                element.getIndexFiles().setSemaphorelockqueue(semaphores);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            queue.offer(element);
            return null;
        }
        ConvertTimeout convertRunnable = new ConvertTimeout(element);
        Thread convertWorker = new Thread(convertRunnable);
        convertWorker.setName("ConvertTimeout");
        convertWorker.run();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            log.error(Constants.EXCEPTION, e); 
        }                   
        return null;
    }

    // not used

    public String doConvertTimeout2(NodeConfig nodeConf) {
        Callable<Object> callable = new Callable<Object>() {
            public Object call() throws Exception {
                /*
                ConvertQueueElement el = Queues.convertQueue.poll();
                if (el == null) {
                        log.error("empty queue");
                    return null;
                }
                 */
                new ConvertHandler(nodeConf, controlService).doConvert(null, nodeConf); // CHECK fix if changes
                return null;
            }
        };
        ExecutorService executorService = Executors.newCachedThreadPool();

        Future<Object> task = executorService.submit(callable);
        Object result = null;
        //ConvertQueueElement result = null;

        try {
            // ok, wait for n seconds max
            result = (QueueElement) task.get(timeout, TimeUnit.SECONDS);
            log.info("Finished with result: " + result);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            //log.error("timeout...", e);
            log.error("timeout and removing " + task);
            //Queues.decConverts();
            //ConvertQueueElement el = result;
            //Queues.convertRunQueue.remove(el);
        } catch (InterruptedException e) {
            log.error("interrupted", e);
        }
        List list = executorService.shutdownNow();
        log.error("Shutdown now list size " + list.size());
        return (String) null;
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
