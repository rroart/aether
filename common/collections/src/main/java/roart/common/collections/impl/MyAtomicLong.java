package roart.common.collections.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MySet;

/**
 * 
 * @author roart
 * 
 * Abstract class for misc implementations of AtomicLong
 * 
 */

public abstract class MyAtomicLong {
    protected static Logger log = LoggerFactory.getLogger(MySet.class);
    
    /**
     * for AtomicLong, add a delta value, add return the new value
     * 
     * @param delta value
     * @return new value after adding
     */
    
    public abstract long addAndGet(long delta);

    public abstract long incrementAndGet();

    public abstract long decrementAndGet();

    /**
     * 
     * @return the value of the AtomicLong
     * 
     */
    
    public abstract long get();

    public abstract void set(long value);

    public abstract void destroy();
}
