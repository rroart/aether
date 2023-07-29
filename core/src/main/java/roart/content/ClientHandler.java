package roart.content;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.NodeConfig;
import roart.common.service.ServiceParam;
import roart.function.AbstractFunction;
import roart.function.FunctionFactory;
import roart.queue.Queues;
import roart.service.ControlService;

public class ClientHandler {

    private static Logger log = LoggerFactory.getLogger(ClientHandler.class);

    public static final int timeout = 3600;

    private NodeConfig nodeConf;

    private ControlService controlService;
    
    public ClientHandler(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
    }

    public List doClient()  {
        ServiceParam el = null;
        if (el == null) {
            log.error("empty queue " + System.currentTimeMillis());
            return null;
        }
        // vulnerable spot
        new Queues(nodeConf, controlService).incClients();

        return doClient(el);
    }

    public List doClient(ServiceParam el)  {
        List list = null;
        AbstractFunction function = FunctionFactory.factory(el, nodeConf, controlService);
        list = function.doClient(el);
        new Queues(nodeConf, controlService).decClients();
        return list;
    }

}
