package roart.model;

import java.util.Set;

import roart.common.collections.MySet;
import roart.common.constants.Constants;
import roart.hcutil.GetHazelcastInstance;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

public class MyHazelcastSet<T> extends MySet<T> {
    Set<T> set = null;
    
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
            log.error(roart.common.constants.Constants.EXCEPTION, e);
        }
        return false;
    }

    public MyHazelcastSet(String setname) {
        HazelcastInstance hz = GetHazelcastInstance.instance();
        set = hz.getSet(setname);       
    }

    @Override
    public Set<T> getAll() {
        return set;
    }
    
    @Override
    public int size() {
        return set.size();
    }
}
