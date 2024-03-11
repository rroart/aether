package roart.common.collections;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MyMap<K, V> {
    protected static Logger log = LoggerFactory.getLogger(MyMap.class);
    public abstract V put(K k, V v);
    public abstract V remove(K k);
    public abstract V remove(K k, Class<V> clazz);
    public abstract Map<K, V> getAll();
    public abstract Set<K> keySet();
    public abstract int size();
    public abstract void clear();
    public abstract void destroy();
    //public abstract Map<T> get();
        //public abstract MyQueue<T>(String queue);
}
