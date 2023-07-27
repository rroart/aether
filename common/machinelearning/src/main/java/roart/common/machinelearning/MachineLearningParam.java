package roart.common.machinelearning;

import roart.common.config.NodeConfig;
import roart.common.inmemory.model.InmemoryMessage;

public abstract class MachineLearningParam {
    public String configname;
    public String configid;
    public NodeConfig conf;
    public InmemoryMessage iconf;
    public String iserver;
    public String iconnection;
}
