package roart.util;

import java.util.Collection;

import roart.hcutil.GetHazelcastInstance;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;

public class MyHazelcastRemover extends MyRemover {
    public void remove(String id) {
	HazelcastInstance hzInstance = GetHazelcastInstance.instance();
	Collection<DistributedObject> objs = hzInstance.getDistributedObjects();
	for (DistributedObject obj : objs) {
	    if (obj.getName().equals(id)) {
		log.info("Hazelcast remove " + id);
		obj.destroy();
	    }
	}
	
    }
    
}
