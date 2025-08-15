package roart.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import roart.common.synchronization.MyLock;
import roart.common.synchronization.MyObjectLockData;
import roart.common.synchronization.MySemaphore;

public class IndexFilesLock {

    @JsonIgnore
    private MyLock flock;
    @JsonIgnore
    private MyLock lock;
    @JsonIgnore
    private Object lockqueue;

    @JsonIgnore
    private MySemaphore semaphoreflock;
    @JsonIgnore
    private MySemaphore semaphorelock;
    @JsonIgnore
    private Object semaphorelockqueue;

    private MyObjectLockData objectflock;

    private MyObjectLockData objectlock;

    public void setFlock(MyLock flock) {
        this.flock = flock;
    }

    public MyLock getFlock() {
        return flock;
    }

    public void setLock(MyLock lock) {
        this.lock = lock;
    }

    public MyLock getLock() {
        return lock;
    }

    public Object getLockqueue() {
        return lockqueue;
    }

    public void setLockqueue(Object lockqueue) {
        this.lockqueue = lockqueue;
    }

    public void setSemaphoreflock(MySemaphore semaphoreflock) {
        this.semaphoreflock = semaphoreflock;
    }

    public MySemaphore getSemaphoreflock() {
        return semaphoreflock;
    }

    public void setSemaphorelock(MySemaphore semaphorelock) {
        this.semaphorelock = semaphorelock;
    }

    public MySemaphore getSemaphorelock() {
        return semaphorelock;
    }

    public Object getSemaphorelockqueue() {
        return semaphorelockqueue;
    }

    public void setSemaphorelockqueue(Object semaphorelockqueue) {
        this.semaphorelockqueue = semaphorelockqueue;
    }

    public MyObjectLockData getObjectflock() {
        return objectflock;
    }

    public void setObjectflock(MyObjectLockData objectflock) {
        this.objectflock = objectflock;
    }

    public MyObjectLockData getObjectlock() {
        return objectlock;
    }

    public void setObjectlock(MyObjectLockData objectlock) {
        this.objectlock = objectlock;
    }

}
