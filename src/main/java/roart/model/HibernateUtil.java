package roart.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Index;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.AnnotationConfiguration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
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

public class HibernateUtil {
    private static Logger log = LoggerFactory.getLogger("HibernateUtil");

    private static SessionFactory factory = null;
    private static Session session = null;
    private static Transaction transaction = null;

    public static Session getCurrentSession() throws /*MappingException,*/ HibernateException, Exception {
	return getHibernateSession();
    }

    public static Session currentSession() throws /*MappingException,*/ HibernateException, Exception {
	return getHibernateSession();
    }

    public static Session getHibernateSession() throws /*MappingException,*/ HibernateException, Exception {
	if (factory == null) {
	    AnnotationConfiguration configuration = new AnnotationConfiguration();
	    factory = configuration.configure().buildSessionFactory();
	    //Object o = new net.sf.ehcache.hibernate.EhCacheRegionFactory();
	}

	if (session == null) {
	    //Session sess = factory.openSession();
	    session = factory.getCurrentSession();
	}

	if (transaction == null) {
	    transaction = session.beginTransaction();
	}

	if (session != null) {
	    if (!session.isOpen()) {
		session = factory.openSession();
	    }
	}
	return session;
    }

    public static void commit() throws /*MappingException,*/ HibernateException, Exception {
	log.info("Doing hibernate commit");
	transaction.commit();
	if (session.isOpen()) {
	    session.close();
	}
	transaction = null;
	session = null;
    }

    public static <T> List<T> convert(List l, Class<T> type) {
        return (List<T>)l;
    }

}
