package roart.common.collections.impl;

import java.util.Map;
import java.util.Set;

import roart.common.collections.MyMap;
import roart.common.constants.Constants;
import roart.common.inmemory.hazelcast.GetHazelcastInstance;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

public class MyHazelcastMap<K, V> extends MyMap<K, V> {
    HazelcastInstance hz;
    
    Map<K, V> map = null;
    
    public MyHazelcastMap(String server, String mapname) {
        this.hz = GetHazelcastInstance.instance(server);
        map = hz.getMap(mapname);       
    }

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

    @Override
    public V remove(K k, Class<V> clazz) {
        try {
            return map.remove(k);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return null;
    }

    @Override
    public Map<K, V> getAll() {
        return map;
    }
    
    @Override
    public Set<K> keySet() {
        return map.keySet();
    }
    
    @Override
    public int size() {
        return map.size();
    }
    
    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public void destroy() {
        ((IMap) map).destroy();
    }
}
