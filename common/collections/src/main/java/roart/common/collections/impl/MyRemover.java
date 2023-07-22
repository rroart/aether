package roart.common.collections.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MyRemover {
    protected static Logger log = LoggerFactory.getLogger(MyRemover.class);
    public abstract void remove(String id);
}
