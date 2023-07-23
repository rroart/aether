package roart.common.collections.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.cp.IAtomicLong;

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
    
    public MyHazelcastAtomicLong(HazelcastInstance hz, String id) {
        CPSubsystem cpSubsystem = hz.getCPSubsystem();
        mylong =  cpSubsystem.getAtomicLong(id);     
    }
 
    @Override
    public long addAndGet(long delta) {
        return mylong.addAndGet(delta);
    }

    @Override
    public long incrementAndGet() {
        return mylong.incrementAndGet();
    }

    @Override
    public long decrementAndGet() {
        return mylong.decrementAndGet();
    }

    @Override
    public long get() {
        return mylong.get();
    }

    @Override
    public void set(long value) {
        mylong.set(value);
    }

    @Override
    public void destroy() {
        ((IAtomicLong) mylong).destroy();
    }
}  