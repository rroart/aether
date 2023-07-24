package roart.thread;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.hcutil.GetHazelcastInstance;
import roart.service.ControlService;
import roart.common.leader.impl.MyLeaderFactory;
import roart.common.leader.MyLeader;
import roart.common.constants.Constants;

public class LeaderRunner implements Runnable {
    static Logger log = LoggerFactory.getLogger(ClientQueueRunner.class);

    private static final java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();

    private static final int LIMIT = 100;

    ThreadPoolExecutor /*ExecutorService*/ pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

    @SuppressWarnings("squid:S2189")
    public void run() {
        MyLeader leader = new MyLeaderFactory().create(ControlService.nodename, ControlService.curatorClient, GetHazelcastInstance.instance());
        while (true) {
            boolean leading = leader.await(1, TimeUnit.SECONDS);
            if (!leading) {
                log.info("I am not leader");
            } else {
                log.info("I am leader");                
            }
            log.info("Leader status: {}", leader.isLeader());
            try {
                TimeUnit.SECONDS.sleep(3600);
            } catch (InterruptedException e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
    }

}
