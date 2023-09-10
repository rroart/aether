package roart.common.synchronization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MyObjectLock {
    protected static Logger log = LoggerFactory.getLogger(MyLock.class);
    public abstract boolean tryLock(String id) throws Exception;
    public abstract void unlock();
    public abstract boolean isLocked();

}
