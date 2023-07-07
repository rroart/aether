package roart.model;

import roart.common.synchronization.MyLock;

public class MyDummyLock extends MyLock {
    
    @Override
    public void lock(String path) throws Exception {
    }

    @Override
    public void unlock() {
    }

}
