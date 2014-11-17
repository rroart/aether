package roart.zkutil;

import java.util.concurrent.TimeUnit;

import roart.thread.ZKRunner;
import roart.util.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKLockUtil {

    static Logger log = LoggerFactory.getLogger(ZKLockUtil.class);

	public static void unlockme(ZKWriteLock writelock) {
	log.info("unlockme");
	writelock.unlock();
	}

	public static ZKWriteLock lockme() {
	final String lockdir = "/" + Constants.AETHER + "/" + Constants.LOCK;
		ZKWriteLock writelock = new ZKWriteLock(ZKInitialize.zk, lockdir);
	log.info("lockme");
	boolean locked;
	try {
	    do {
		locked = writelock.lock();
		log.info("lockme " + locked);
		if (!locked) {
			TimeUnit.SECONDS.sleep(60);
		}
	    } while (!locked);
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
	return writelock;
	}

	public static void unlockme(ZKBlockWriteLock writelock) {
	log.info("unlockme");
	writelock.unlock();
	}

	public static ZKBlockWriteLock blocklockme() {
	final String lockdir = "/" + Constants.AETHER + "/" + Constants.LOCK;
		ZKBlockWriteLock writelock = new ZKBlockWriteLock(ZKInitialize.zk, lockdir);
	log.info("lockme");
	try {
	    writelock.lock();
	    log.info("locked");
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
	return writelock;
	}
}
