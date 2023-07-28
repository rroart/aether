package roart.common.collections;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.hazelcast.core.HazelcastInstance;
import org.apache.curator.framework.CuratorFramework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.impl.MyRemover;
import roart.common.config.NodeConfig;

public abstract class MyCollections<T> {
    protected static Logger log = LoggerFactory.getLogger(MyCollections.class);

    protected static volatile ConcurrentMap<String, Object> mycollections = new ConcurrentHashMap<String, Object>();

    public static MyRemover remover = null;
    
    public static Object get(String id, NodeConfig nodeConf, MyFactory myfactory, CuratorFramework curatorFramework, HazelcastInstance hz) {
	Object obj = null;
	if (id != null) {
	    obj = mycollections.get(id);
	}
	if (obj == null) {
	    obj = myfactory.create(id, nodeConf, curatorFramework, hz);
	    put(id, obj);
	}
	return obj;
    }
    
    public static void put(String id, Object obj) {
        mycollections.put(id, obj);
    }

    public static void put(String id, NodeConfig nodeConf, MyFactory myfactory, CuratorFramework curatorFramework, HazelcastInstance hz) {
	Object obj = myfactory.create(id, nodeConf, curatorFramework, hz);
	put(id, obj);
    }
    
    public static boolean remove(String id) {
	if (remover != null) {
	    remover.remove(id);
	}
        return mycollections.remove(id) != null;
    }
    
}
