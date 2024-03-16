package roart.common.collections.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.curator.framework.CuratorFramework;

import roart.common.collections.MyFactory;
import roart.common.collections.MyList;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;

public class MyListFactory extends MyFactory {
    
    public MyList create(String listid, NodeConfig nodeConf, CuratorFramework curatorFramework) {
        if (nodeConf.wantDistributedTraverse() || nodeConf.wantAsync()) {
            return new MyHazelcastList(nodeConf.getInmemoryHazelcast(), listid);
        } else {
            return new MyJavaList();
        }
    }
}
