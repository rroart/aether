package roart.util;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MyList<T> extends MyCollection<T> {
    protected static Logger log = LoggerFactory.getLogger(MySet.class);
    public abstract void add(T o);
    public abstract List<T> getAll();
    //public abstract Set<T> get();
        //public abstract MyQueue<T>(String queue);
}
