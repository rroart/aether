package roart.common.collections.impl;

import java.util.Collection;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;

public class MyHazelcastRemover extends MyRemover {
    HazelcastInstance hz;
    
    public MyHazelcastRemover(HazelcastInstance hz) {
        super();
        this.hz = hz;
    }

    public void remove(String id) {
	Collection<DistributedObject> objs = hz.getDistributedObjects();
	for (DistributedObject obj : objs) {
	    if (obj.getName().equals(id)) {
		log.info("Hazelcast remove {}", id);
		obj.destroy();
	    }
	}
	
    }
    
}
