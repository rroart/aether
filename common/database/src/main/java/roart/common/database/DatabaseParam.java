package roart.common.database;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import roart.common.config.NodeConfig;

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
        
}
