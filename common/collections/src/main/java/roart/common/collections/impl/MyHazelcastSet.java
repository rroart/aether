package roart.common.collections.impl;

import java.util.Set;

import roart.common.collections.MySet;
import roart.common.constants.Constants;

import com.hazelcast.core.HazelcastInstance;

public class MyHazelcastSet<T> extends MySet<T> {
    HazelcastInstance hz;
    
    Set<T> set = null;

    public MyHazelcastSet(HazelcastInstance hz, String setname) {
        this.hz = hz;
        set = hz.getSet(setname);       
    }
    
    @Override
    public boolean add(T o) {
        try {
            return set.add(o);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return false;
    }

    @Override
    public boolean remove(T o) {
        try {
            return set.remove(o);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return false;
    }

    @Override
    public Set<T> getAll() {
        return set;
    }
    
    @Override
    public int size() {
        return set.size();
    }
    
    @Override
    public void clear() {
        set.clear();
    }
}
