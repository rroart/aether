package roart.common.collections.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import roart.common.collections.MySet;

public class MyJavaSet<T> extends MySet<T> {
    private Set<T> set;

    @Override
    public boolean add(T o) {
        return set.add(o);
    }
    
    @Override
    public boolean remove(T o) {
        return set.remove(o);
    }
    
    public MyJavaSet() {
        set = Collections.synchronizedSet(new HashSet<>());
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
