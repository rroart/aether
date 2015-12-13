package roart.util;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MyAtomicLong {
    protected static Logger log = LoggerFactory.getLogger(MySet.class);
    public abstract long addAndGet(long delta);
    public abstract long get();
}
