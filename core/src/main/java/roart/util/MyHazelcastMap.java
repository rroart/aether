package roart.util;

import java.util.Map;

import roart.common.collections.MyMap;
import roart.common.constants.Constants;
import roart.hcutil.GetHazelcastInstance;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

public class MyHazelcastMap<K, V> extends MyMap<K, V> {
    Map<K, V> map = null;
    
    @Override
    public V put(K k, V v) {
        try {
            return map.put(k, v);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return null;
    }

    @Override
    public V remove(K k) {
        try {
            return map.remove(k);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return null;
    }

    public MyHazelcastMap(String mapname) {
        HazelcastInstance hz = GetHazelcastInstance.instance();
        map = hz.getMap(mapname);       
    }

    @Override
    public Map<K, V> getAll() {
        return map;
    }
    
    @Override
    public int size() {
        return map.size();
    }
}
