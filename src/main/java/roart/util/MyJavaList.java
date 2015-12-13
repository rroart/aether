package roart.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MyJavaList<T> extends MyList<T> {
    public volatile List list;

    @Override
    public void add(T o) {
        list.add(o);
    }
    
    public MyJavaList() {
        list = new ArrayList<T>();
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
