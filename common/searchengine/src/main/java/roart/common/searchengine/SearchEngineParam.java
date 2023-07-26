package roart.common.searchengine;

import roart.common.config.NodeConfig;
import roart.common.inmemory.model.InmemoryMessage;

public abstract class SearchEngineParam {
    public String configname;
    public String configid;
    public NodeConfig conf;
    public InmemoryMessage iconf;
    public String iserver;
    public String iconnection;
}
