package roart.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import roart.common.collections.MyList;

/**
 * 
 * @author roart
 *
 * Use the native Java for list implementation
 * 
 * @param <T>
 */

public class MyJavaList<T> extends MyList<T> {
    private List<T> list;

    /**
     * Create a native Java list
     */
    
    public MyJavaList() {
        list = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void add(T o) {
        list.add(o);
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
