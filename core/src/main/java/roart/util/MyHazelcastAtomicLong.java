package roart.util;

import roart.hcutil.GetHazelcastInstance;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;

/**
 * 
 * @author roart
 *
 * Use the Hazelcast implementation of AtomicLong
 * 
 */

public class MyHazelcastAtomicLong extends MyAtomicLong {
    private volatile IAtomicLong mylong = null;
 
    /**
     * Create/get a Hazelcast AtomicLong with given id
     * 
     * @param id Id
     */
    
    public MyHazelcastAtomicLong(String id) {
        HazelcastInstance hz = GetHazelcastInstance.instance();
        mylong = hz.getAtomicLong(id);       
    }
 
    @Override
    public long addAndGet(long delta) {
        return mylong.addAndGet(delta);
    }

    @Override
    public long get() {
        return mylong.get();
    }
}  