package roart.convert;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.constants.Constants;
import roart.common.util.ExecCommand;

public class ConvertUtil {

    private static Logger log = LoggerFactory.getLogger(ConvertUtil.class);

    private static final java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();

    private static String execute(String filename, String[] arg, long[] pid) {
        String res = null;

        ExecCommand ec = new ExecCommand();
        ec.execute(filename, arg, pid, null);

        res = ec.getOutput() + ec.getError();
        log.info("output " + res);
        return res;
    }

    public static Object executeQueue() {
        Object [] objs = execQueue.poll();
        String filename = (String) objs[0];
        String [] arg = (String []) objs[1];
        long[] pid = (long[]) objs[2];
        return execute(filename, arg, pid);
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

        long[] pid = new long[1];
        
        Object [] objs = new Object[3];
        objs[0] = filename;
        objs[1] = arg;
        objs[2] = pid;
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
                log.info("Otherworker finished {} {} {} {}", filename, arg[0], otherWorker, otherRunnable);
                return "end";
            }
        }
        new ExecCommand().execute("/bin/kill", new String[] { "-9", "" + pid[0] }, null, null);
        //otherWorker.stop(); // .interrupt();
        el[0]= "othertimeout" + filename + " " + timeout + " ";
        log.info("Otherworker timeout {} {} {}", otherWorker, otherRunnable, pid[0]);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(Constants.EXCEPTION, e);
            // TODO Auto-generated catch block
        }
        log.info("Otherworker timeout {} {} {} {} {}", otherWorker, otherRunnable, otherWorker.isAlive(), otherWorker.isInterrupted(), otherWorker.interrupted());

        log.error("timeout running {} {}", filename, arg[0]);
        //retlist.add(new ResultItem("timeout running " + filename + " " + arg[0]));
        return (String) null;
    }

}
