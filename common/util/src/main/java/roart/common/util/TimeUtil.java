package roart.common.util;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.constants.Constants;

public class TimeUtil {
    private static Logger log = LoggerFactory.getLogger(TimeUtil.class);

    public static void sleep(int sec) {
        try {
            TimeUnit.SECONDS.sleep(sec);
        } catch (InterruptedException e) {
            log.error(Constants.EXCEPTION, e);
        }        
    }
}
