package roart.common.collections.impl;

import roart.common.collections.MyCollections;
import roart.common.collections.MySet;
import roart.common.config.NodeConfig;

import org.apache.curator.framework.CuratorFramework;

public class MySets extends MyCollections {
    
    public static MySet get(String id, NodeConfig nodeConf, CuratorFramework curatorFramework) {
        return (MySet) get(id, nodeConf, new MySetFactory(), curatorFramework);
     }

    public static void put(String id) {
        put(id, new MySetFactory());
    }
}
