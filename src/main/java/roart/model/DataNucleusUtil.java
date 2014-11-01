package roart.model;

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
    private static Logger log = LoggerFactory.getLogger("DataNucleusUtil");

    private static DataNucleusUtil session = null;
    private static PersistenceManagerFactory pmf = null;
    private static PersistenceManager pm = null;
    private static Transaction transaction = null;
    public static PersistenceManager getPm() {
    	return pm;
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
    		session = new DataNucleusUtil();
    	}

    	if (pm == null) {
    		pm = pmf.getPersistenceManager();	
    	}
    	
    	if (transaction == null) {
    		transaction = pm.currentTransaction();
    		transaction.begin();
    	}

    	return session;
    }

    public static void commit() throws Exception {
	log.info("Doing DataNucleus commit");
	transaction.commit();
	 if (transaction.isActive())
     {
         transaction.rollback();
     }
	pm.close();
	pm = null;
	transaction = null;
    }

    public static <T> List<T> convert(List l, Class<T> type) {
        return (List<T>)l;
    }

	public void flush() {
		pm.flush();
	}

	public Query createQuery(String string) {
		Query q = pm.newQuery(string);
		return q;
	}

	public void save(DataNucleusIndexFiles fi) {
		pm.makePersistent(fi);
	}

}
