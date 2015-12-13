package roart.util;

import java.util.List;
import java.util.Set;

import roart.hcutil.GetHazelcastInstance;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

public class MyHazelcastList<T> extends MyList<T> {
    List<T> list = null;
    
    @Override
    public void add(T o) {
        try {
            list.add(o);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
        }
    }

    public MyHazelcastList(String listname) {
        HazelcastInstance hz = GetHazelcastInstance.instance();
        list = hz.getList(listname);       
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
