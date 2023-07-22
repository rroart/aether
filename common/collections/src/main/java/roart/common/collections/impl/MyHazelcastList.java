package roart.common.collections.impl;

import java.util.List;

import roart.common.collections.MyList;

import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @author roart
 *
 * Use Hazelcast list for the list implementation
 * 
 * @param <T>
 */

public class MyHazelcastList<T> extends MyList<T> {
    HazelcastInstance hz;
    
    List<T> list = null;
    
    /**
     * Create a Hazelcast list
     * 
     * @param listname
     */
    
    public MyHazelcastList(HazelcastInstance hz, String listname) {
        this.hz = hz;
        list = hz.getList(listname);       
    }

    @Override
    public void add(T o) {
        try {
            list.add(o);
        } catch (Exception e) {
            log.error(roart.common.constants.Constants.EXCEPTION, e);
        }
    }

    @Override
    public List<T> getAll() {
        return list;
    }

    @Override
    public int size() {
        return list.size();
    }
    
    @Override
    public void clear() {
        list.clear();
    }
}
