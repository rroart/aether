package roart.convert;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.constants.Constants;
import roart.common.util.ExecCommand;

public class ConvertUtil {

    private static Logger log = LoggerFactory.getLogger(ConvertUtil.class);

    private static final java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();

    private static String execute(String filename, String[] arg) {
        String res = null;

        ExecCommand ec = new ExecCommand();
        ec.execute(filename, arg);

        res = ec.getOutput() + ec.getError();
        log.info("output " + res);
        return res;
    }

    public static Object executeQueue() {
        Object [] objs = execQueue.poll();
        String filename = (String) objs[0];
        String [] arg = (String []) objs[1];
        return execute(filename, arg);
    }

    public static String executeTimeout(String filename, String [] arg, String retlistid, String[] el, int timeout) {
        class OtherTimeout implements Runnable {
            public void run() {
                try {
                    executeQueue();
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
            }
        }

        Object [] objs = new Object[2];
        objs[0] = filename;
        objs[1] = arg;
        execQueue.add(objs);

        OtherTimeout otherRunnable = new OtherTimeout();
        Thread otherWorker = new Thread(otherRunnable);
        otherWorker.setName("OtherTimeout");
        otherWorker.start();
        long start = System.currentTimeMillis();
        boolean b = true;
        while (b) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(Constants.EXCEPTION, e);
                // TODO Auto-generated catch block
            }
            long now = System.currentTimeMillis();
            if ((now - start) > 1000 * timeout) {
                b = false;
            }
            if (!otherWorker.isAlive()) {
                log.info("Otherworker finished " + filename + " " + arg[0] + " " + otherWorker + " " + otherRunnable);
                return "end";
            }
        }
        otherWorker.stop(); // .interrupt();
        el[0]= "othertimeout" + filename + " " + timeout + " ";
        log.info("Otherworker timeout " + otherWorker + " " + otherRunnable);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(Constants.EXCEPTION, e);
            // TODO Auto-generated catch block
        }
        log.info("Otherworker timeout " + otherWorker + " " + otherRunnable + " " + otherWorker.isAlive() + " " + otherWorker.isInterrupted() + " " + otherWorker.interrupted());

        log.error("timeout running " + filename + " " + arg[0]);
        //retlist.add(new ResultItem("timeout running " + filename + " " + arg[0]));
        return (String) null;
    }

}
