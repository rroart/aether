package roart.common.collections.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import roart.common.collections.MyCollections;
import roart.common.collections.MyMap;
import roart.common.config.NodeConfig;

import org.apache.curator.framework.CuratorFramework;

public class MyMaps extends MyCollections {
    
    public static MyMap get(String id, NodeConfig nodeConf, CuratorFramework curatorFramework) {
        return (MyMap) get(id, nodeConf, new MyMapFactory(), curatorFramework);
     }

    public static void put(String id) {
        put(id, new MyMapFactory());
    }
}
