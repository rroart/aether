package roart.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class MyJavaAtomicLong extends MyAtomicLong {
    public volatile AtomicLong mylong;

    public MyJavaAtomicLong() {
        mylong = new AtomicLong();
    }

    @Override
    public long addAndGet(long delta) {
        return mylong.addAndGet(delta);
    }

    @Override
    public long get() {
        return mylong.get();
    }
}
