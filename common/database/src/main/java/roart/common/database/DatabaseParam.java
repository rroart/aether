package roart.common.database;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import roart.common.config.NodeConfig;
import roart.common.inmemory.model.InmemoryMessage;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(  
        use = JsonTypeInfo.Id.NAME,  
        include = JsonTypeInfo.As.PROPERTY,  
        property = "_class")  
@JsonSubTypes({  
    @Type(value = DatabaseConstructorParam.class, name = "roart.model.DatabaseConstructorParam"),  
    @Type(value = DatabaseFileLocationParam.class, name = "roart.model.DatabaseFileLocationParam"),  
    @Type(value = DatabaseIndexFilesParam.class, name = "roart.model.DatabaseIndexFilesParam"),  
    @Type(value = DatabaseMd5Param.class, name = "roart.model.DatabaseMd5Param") })  
public abstract class DatabaseParam {
    private String configname;

    private String configid;

    private NodeConfig conf;

    private InmemoryMessage iconf;

    private String iserver;

    private String iconnection;

    public DatabaseParam() {
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
