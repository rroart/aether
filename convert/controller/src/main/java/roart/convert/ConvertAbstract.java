package roart.convert;

import roart.common.config.NodeConfig;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;

public abstract class ConvertAbstract {
    
    private String nodename;
    protected NodeConfig nodeConf;

    public ConvertAbstract(String nodename, NodeConfig nodeConf) {
        this.nodename = nodename;
        this.nodeConf = nodeConf;
    }
    
    public abstract ConvertResult convert(ConvertParam index);
}
