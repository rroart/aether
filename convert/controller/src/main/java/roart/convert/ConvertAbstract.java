package roart.convert;

import org.apache.curator.framework.CuratorFramework;

import roart.common.config.NodeConfig;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;

public abstract class ConvertAbstract {
    
    private String configname;
    private String configid;
    protected NodeConfig nodeConf;
    protected CuratorFramework curatorClient;

    public ConvertAbstract(String configname, String configid, NodeConfig nodeConf, CuratorFramework curatorClient) {
        this.configname = configname;
        this.configid = configid;
        this.nodeConf = nodeConf;
        this.curatorClient = curatorClient;
    }
    
    public abstract ConvertResult convert(ConvertParam index);
   
}
