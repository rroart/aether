package roart.content;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.NodeConfig;
import roart.common.service.ServiceParam;
import roart.function.AbstractFunction;
import roart.function.FunctionFactory;
import roart.queue.Queues;

public class ClientHandler {

    private static Logger log = LoggerFactory.getLogger(ClientHandler.class);

    public static final int timeout = 3600;

    private NodeConfig nodeConf;
    
    public ClientHandler(NodeConfig nodeConf) {
        super();
        this.nodeConf = nodeConf;
    }

    public List doClient()  {
        ServiceParam el = null;
        if (el == null) {
            log.error("empty queue " + System.currentTimeMillis());
            return null;
        }
        // vulnerable spot
        new Queues(nodeConf).incClients();

        return doClient(el);
    }

    public List doClient(ServiceParam el)  {
        List list = null;
        AbstractFunction function = FunctionFactory.factory(el, nodeConf);
        list = function.doClient(el);
        new Queues(nodeConf).decClients();
        return list;
    }

}
