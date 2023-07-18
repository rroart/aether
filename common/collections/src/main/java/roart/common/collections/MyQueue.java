package roart.common.collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MyQueue<T> extends MyCollection<T> {
    protected static Logger log = LoggerFactory.getLogger(MyQueue.class);
    public abstract void offer(T o);
    public abstract T poll();
    public abstract T poll(Class<T> clazz);
    public abstract int size();
    public abstract void clear();
    //public abstract MyQueue<T>(String queue);
}
