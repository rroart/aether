package roart.database;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import roart.config.NodeConfig;

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
    public String nodename;
    public NodeConfig conf;
}
