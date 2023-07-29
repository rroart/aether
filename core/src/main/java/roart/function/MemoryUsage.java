package roart.function;

import java.util.ArrayList;
import java.util.List;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.ResultItem;
import roart.common.service.ServiceParam;
import roart.service.ControlService;

public class MemoryUsage extends AbstractFunction {

    public MemoryUsage(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        super(param, nodeConf, controlService);
    }

    @Override
    public List doClient(ServiceParam param) {
        List<ResultItem> retlist = new ArrayList<>();
        try {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long allocatedMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            java.text.NumberFormat format = java.text.NumberFormat.getInstance();
            retlist.add(new ResultItem("free memory: " + format.format(freeMemory / 1024)));
            retlist.add(new ResultItem("allocated memory: " + format.format(allocatedMemory / 1024)));
            retlist.add(new ResultItem("max memory: " + format.format(maxMemory / 1024)));
            retlist.add(new ResultItem("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024)));
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        List<List> retlistlist = new ArrayList<>();
        retlistlist.add(retlist);
        return retlistlist;
    }

}
