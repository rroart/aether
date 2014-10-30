package roart.model;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.transaction.Transaction;

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
    private static EntityTransaction transaction = null;
    private static EntityManager em = null;
    private static PersistenceManager pm = null;
    public static EntityManager getEm() {
    	return em;
    }
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
    		EntityManagerFactory emf = Persistence.createEntityManagerFactory("IndexFiles");
    		em = emf.createEntityManager();
    		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("IndexFiles");
    		pm = pmf.getPersistenceManager();
    		session = new DataNucleusUtil();
    	}

	if (transaction == null) {
	    transaction = em.getTransaction();
	    transaction.begin();
	}

	if (session != null) {
	    
	}
	return session;
    }

    public static void commit() throws Exception {
	log.info("Doing hibernate commit");
	transaction.commit();
	em.close();
	em = null;
	transaction = null;
	session = null;
    }

    public static <T> List<T> convert(List l, Class<T> type) {
        return (List<T>)l;
    }

	public void flush() {
		em.flush();
	}

	public Query createQuery(String string) {
		Query q = em.createQuery(string);
		return q;
	}

	public void save(DataNucleusIndexFiles fi) {
		em.persist(fi);
	}

}
