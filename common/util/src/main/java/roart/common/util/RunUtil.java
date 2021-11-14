package roart.common.util;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunUtil {
    private static Logger log = LoggerFactory.getLogger(RunUtil.class);

    private static final java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();

    public static String execute(String filename, String[] arg) {
        String res = null;

        ExecCommand ec = new ExecCommand();
        ec.execute(filename, arg, null);

        res = ec.getOutput() + ec.getError();
        log.info("output {}", res);
        return res;
    }
    

}
