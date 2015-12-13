package roart.util;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MyCollections<T> {
    protected static Logger log = LoggerFactory.getLogger(MyCollections.class);

    protected static volatile ConcurrentMap<String, Object> mycollections = new ConcurrentHashMap<String, Object>();

    public static Object get(String id, MyFactory myfactory) {
	Object obj = null;
	if (id != null) {
	    obj = mycollections.get(id);
	}
	if (obj == null) {
	    obj = myfactory.create(id);
	    put(id, obj);
	}
	return obj;
    }
    
    public static void put(String id, Object obj) {
        mycollections.put(id, obj);
    }

    public static void put(String id, MyFactory myfactory) {
	Object obj = myfactory.create(id);
	put(id, obj);
    }
    
    public static boolean remove(String id) {
        return mycollections.remove(id) != null;
    }
    
}
