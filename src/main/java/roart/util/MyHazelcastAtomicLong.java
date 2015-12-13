package roart.util;

import roart.hcutil.GetHazelcastInstance;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;

public class MyHazelcastAtomicLong extends MyAtomicLong {
    public volatile IAtomicLong mylong = null;
 
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