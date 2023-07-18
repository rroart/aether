package roart.common.synchronization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MyLock {
    protected static Logger log = LoggerFactory.getLogger(MyLock.class);
    public abstract void lock(String path) throws Exception;
    public abstract boolean tryLock(String path) throws Exception;
    public abstract void unlock();
    public abstract boolean isLocked();
}
