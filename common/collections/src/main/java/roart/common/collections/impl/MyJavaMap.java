package roart.common.collections.impl;

import java.util.HashMap;
import java.util.Queue;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import roart.common.collections.MyMap;

public class MyJavaMap<K, V> extends MyMap<K, V> {
    public volatile Map<K, V> map;

    @Override
    public V put(K k, V v) {
        return map.put(k, v);
    }
    
    @Override
    public V remove(K k) {
        return map.remove(k);
    }
    
    public MyJavaMap() {
        map = new HashMap<K, V>();
    }

    @Override
    public Map<K, V> getAll() {
        return map;
    }

    @Override
    public int size() {
        return map.size();
    }
    
    @Override
    public void clear() {
        map.clear();
    }
}
