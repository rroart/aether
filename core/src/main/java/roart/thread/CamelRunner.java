package roart.thread;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.util.Constants;

public class CamelRunner implements Runnable {
	
	private static Logger log = LoggerFactory.getLogger(CamelRunner.class);
	
    public void run() {
        CamelContext context = new DefaultCamelContext();
        try {
            context.start();
            Thread.sleep(86400);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
    }

}
