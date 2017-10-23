package roart.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import roart.config.MyXMLConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunUtil {
    private static Logger log = LoggerFactory.getLogger(RunUtil.class);

    private static final java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();

    public static void run() {
        //MyXMLConfig instance = MyXMLConfig.instance();
        //instance.config();
        Runnable eureka = new JarThread("aether-eureka-0.10-SNAPSHOT.jar");
        new Thread(eureka).start();
        /*
        try {
            Thread.sleep(15000);
        } catch (InterruptedException ex) {
            // TODO Auto-generated catch block
            log.error("Exception", ex);
        }
        */
        Runnable core = new JarThread("aether-core-0.10-SNAPSHOT.jar");
        new Thread(core).start();
        Runnable local = new JarThread("aether-local-0.10-SNAPSHOT.jar");
        new Thread(local).start();
    }
    
    public static String execute(String filename, String[] arg) {
        String res = null;

        ExecCommand ec = new ExecCommand();
        ec.execute(filename, arg);

        res = ec.getOutput() + ec.getError();
        log.info("output " + res);
        return res;
    }
    

}
