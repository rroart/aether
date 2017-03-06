package roart.util;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MyJavaSet<T> extends MySet<T> {
    public volatile Set set;

    @Override
    public boolean add(T o) {
        return set.add(o);
    }
    
    @Override
    public boolean remove(T o) {
        return set.remove(o);
    }
    
    public MyJavaSet() {
        set = new HashSet<T>();
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
