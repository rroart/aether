package roart.zkutil;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

public class ZKBlockWriteLock {
  private ZKWriteLock writeLock;
  private CountDownLatch signal = new CountDownLatch(1);

  public ZKBlockWriteLock(ZooKeeper zookeeper, String path) {
    this.writeLock = new ZKWriteLock(zookeeper, path, new ZKBlockWriteLockListener());
  }

  public void lock() throws InterruptedException, KeeperException {
    writeLock.lock();
    signal.await();
  }

  public void unlock() {
    writeLock.unlock();
  }

  class ZKBlockWriteLockListener implements ZKLockListener {
    @Override public void acquire() {
      signal.countDown();
    }

    @Override public void release() {
    }
  }
}

