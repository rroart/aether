package roart.util;

import java.util.List;

/**
 * An abstract for misc List implementation, from native Java to Hazelcast
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MyCollection;

public abstract class MyList<T> extends MyCollection<T> {
    protected static Logger log = LoggerFactory.getLogger(MyList.class);
    
    /**
     * Add an item to the list
     * 
     * @param o to be added
     */
    
    public abstract void add(T o);
    public abstract List<T> getAll();
    //public abstract Set<T> get();
        //public abstract MyQueue<T>(String queue);
}
