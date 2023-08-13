package roart.common.model;

import roart.common.config.NodeConfig;
import roart.common.inmemory.model.InmemoryMessage;

public class ConfigParam {
    private String configname;

    private String configid;

    private NodeConfig conf;

    private InmemoryMessage iconf;

    private String iserver;

    private String iconnection;

    public ConfigParam() {
        super();
    }

    public String getConfigname() {
        return configname;
    }

    public void setConfigname(String configname) {
        this.configname = configname;
    }

    public String getConfigid() {
        return configid;
    }

    public void setConfigid(String configid) {
        this.configid = configid;
    }

    public NodeConfig getConf() {
        return conf;
    }

    public void setConf(NodeConfig conf) {
        this.conf = conf;
    }

    public InmemoryMessage getIconf() {
        return iconf;
    }

    public void setIconf(InmemoryMessage iconf) {
        this.iconf = iconf;
    }

    public String getIserver() {
        return iserver;
    }

    public void setIserver(String iserver) {
        this.iserver = iserver;
    }

    public String getIconnection() {
        return iconnection;
    }

    public void setIconnection(String iconnection) {
        this.iconnection = iconnection;
    }

}
