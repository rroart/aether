package roart.zkutil;

import org.apache.zookeeper.KeeperException;

public interface ZKExecute {
    public boolean execute() throws KeeperException, InterruptedException;
}

