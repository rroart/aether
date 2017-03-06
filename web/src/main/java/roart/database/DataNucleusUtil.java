package roart.database;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// dummy
//import net.sf.ehcache.hibernate.EhCacheRegionFactory;

public class DataNucleusUtil {
    private static Logger log = LoggerFactory.getLogger(DataNucleusUtil.class);

    private static DataNucleusUtil session = null;
    private static PersistenceManagerFactory pmf = null;
    private static PersistenceManagerFactory pmf2 = null;
    private static PersistenceManager pm = null;
    private static PersistenceManager pm2 = null;
    private static Transaction transaction = null;
    private static Transaction transaction2 = null;
    public static PersistenceManager getPm() {
    	return pm;
    }
    public static PersistenceManager getPm2() {
    	return pm2;
    }
    
    public static DataNucleusUtil getCurrentSession() throws Exception {
	return getDataNucleusSession();
    }

    public static DataNucleusUtil currentSession() throws Exception {
	return getDataNucleusSession();
    }

    public static DataNucleusUtil getDataNucleusSession() throws Exception {
    	if (session == null) {
    		pmf = JDOHelper.getPersistenceManagerFactory("IndexFiles");
    		pmf2 = JDOHelper.getPersistenceManagerFactory("Files");
    		session = new DataNucleusUtil();
    	}

    	if (pm == null) {
    		pm = pmf.getPersistenceManager();	
    	}
    	if (pm2 == null) {
    		pm2 = pmf2.getPersistenceManager();	
    	}
    	
    	if (transaction == null) {
    		transaction = pm.currentTransaction();
    		transaction.begin();
    	}
    	if (transaction2 == null) {
    		transaction2 = pm2.currentTransaction();
    		transaction2.begin();
    	}

    	return session;
    }

    public static void close() throws Exception {
	if (pm != null) {
	    pm.close();
	    pm = null;
	}
    }
    
    public static void commit() throws Exception {
	log.info("Doing DataNucleus commit");
	if (transaction != null) {
	transaction.commit();
	 if (transaction.isActive())
     {
         transaction.rollback();
     }
		transaction = null;
	}
	/*
	if (pm != null) {
	pm.close();
	pm = null;
	}
	*/

	if (transaction2 != null) {
	transaction2.commit();
	 if (transaction2.isActive())
     {
         transaction2.rollback();
     }
		transaction2 = null;
	}
	if (pm2 != null) {
	pm2.close();
	pm2 = null;
	}
    }

    public static <T> List<T> convert(List l, Class<T> type) {
        return (List<T>)l;
    }

	public void flush() {
		if (pm != null) {
		pm.flush();
		}
		if (pm2 != null) {
		pm2.flush();
		}
	}

	public Query createQuery(String string) {
		Query q = pm.newQuery(string);
		return q;
	}

	public void save(DataNucleusIndexFiles fi) {
		pm.makePersistent(fi);
	}

    public void save(DataNucleusFiles fi) {
        pm2.makePersistent(fi);
    }

}
