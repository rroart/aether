package roart.util;

import java.util.List;

import roart.hcutil.GetHazelcastInstance;

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
    List<T> list = null;
    
    /**
     * Create a Hazelcast list
     * 
     * @param listname
     */
    
    public MyHazelcastList(String listname) {
        HazelcastInstance hz = GetHazelcastInstance.instance();
        list = hz.getList(listname);       
    }

    @Override
    public void add(T o) {
        try {
            list.add(o);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
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
}
