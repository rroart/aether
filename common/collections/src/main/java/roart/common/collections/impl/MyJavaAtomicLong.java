package roart.common.collections.impl;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author roart
 *
 * Use the Java core implementation of AtomicLong
 */

public class MyJavaAtomicLong extends MyAtomicLong {
    private volatile AtomicLong mylong;

    /**
     * Create a Java core AtomicLong
     */
    
    public MyJavaAtomicLong() {
        mylong = new AtomicLong();
    }

    @Override
    public long addAndGet(long delta) {
        return mylong.addAndGet(delta);
    }

    @Override
    public long incrementAndGet() {
        return mylong.incrementAndGet();
    }

    @Override
    public long decrementAndGet() {
        return mylong.decrementAndGet();
    }

    @Override
    public long get() {
        return mylong.get();
    }

    @Override
    public void set(long value) {
        mylong.set(value);
    }
}
