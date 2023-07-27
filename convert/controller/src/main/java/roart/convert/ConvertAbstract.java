package roart.convert;

import roart.common.config.NodeConfig;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;

public abstract class ConvertAbstract {
    
    private String configname;
    private String configid;
    protected NodeConfig nodeConf;

    public ConvertAbstract(String configname, String configid, NodeConfig nodeConf) {
        this.configname = configname;
        this.configid = configid;
        this.nodeConf = nodeConf;
    }
    
    public abstract ConvertResult convert(ConvertParam index);
}
