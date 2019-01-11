package roart.common.collections;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MySet<T> extends MyCollection<T> {
    protected static Logger log = LoggerFactory.getLogger(MySet.class);
    public abstract boolean add(T o);
    public abstract boolean remove(T o);
    public abstract Set<T> getAll();
    //public abstract Set<T> get();
        //public abstract MyQueue<T>(String queue);
}
