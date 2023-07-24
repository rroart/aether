package roart.thread;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientQueueRunner implements Runnable {
    static Logger log = LoggerFactory.getLogger(ClientQueueRunner.class);

    private static final java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();

    private static final int LIMIT = 100;

    ThreadPoolExecutor /*ExecutorService*/ pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

    @SuppressWarnings("squid:S2189")
    public void run() {
    }
}
