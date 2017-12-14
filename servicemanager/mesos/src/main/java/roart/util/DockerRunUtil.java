package roart.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import roart.config.MyXMLConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerRunUtil {
    private static Logger log = LoggerFactory.getLogger(DockerRunUtil.class);

    private static final java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();

    public static void run() {
        String VERSION = "0.10-SNAPSHOT";
        //MyXMLConfig instance = MyXMLConfig.instance();
        //instance.config();
        /*
        DockerThread eureka = new DockerThread();
        String addr = eureka.start("aether-eureka", null);
        DockerThread core = new DockerThread();
        core.start("aether-core", addr);
         DockerThread local = new DockerThread();
        local.start("aether-local", addr);
        */
        Runnable eureka = new JarThread("aether-eureka-0.10-SNAPSHOT.jar");
        new Thread(eureka).start();
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
