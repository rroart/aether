package roart.content;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.service.ServiceParam;
import roart.function.AbstractFunction;
import roart.function.FunctionFactory;
import roart.queue.Queues;

public class ClientHandler {

    private static Logger log = LoggerFactory.getLogger(ClientHandler.class);

    public static final int timeout = 3600;

    public static List doClient()  {
        ServiceParam el = null;
        if (el == null) {
            log.error("empty queue " + System.currentTimeMillis());
            return null;
        }
        // vulnerable spot
        Queues.incClients();

        return doClient(el);
    }

    public static List doClient(ServiceParam el)  {
        List list = null;
        AbstractFunction function = FunctionFactory.factory(el);
        list = function.doClient(el);
        Queues.decClients();
        return list;
    }

}
