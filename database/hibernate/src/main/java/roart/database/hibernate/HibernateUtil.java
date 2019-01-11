package roart.database.hibernate;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// dummy
//import net.sf.ehcache.hibernate.EhCacheRegionFactory;

public class HibernateUtil {
    private static Logger log = LoggerFactory.getLogger(HibernateUtil.class);

    private static SessionFactory factory = null;
    private static Session session = null;
    private static Transaction transaction = null;

    public static Session getCurrentSession(String h2dir) throws /*MappingException,*/ HibernateException, Exception {
	return getHibernateSession(h2dir);
    }

    public static Session currentSession(String h2dir) throws /*MappingException,*/ HibernateException, Exception {
	return getHibernateSession(h2dir);
    }

    public static Session getHibernateSession(String h2dir) throws /*MappingException,*/ HibernateException, Exception {
	if (factory == null) {
		/*
	    AnnotationConfiguration configuration = new AnnotationConfiguration();
	    factory = configuration.configure().buildSessionFactory();*/
		/*
		Configuration configuration = new Configuration().configure();
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
				applySettings(configuration.getProperties());
		factory = configuration.buildSessionFactory(builder.build());
		*/
		factory = new Configuration().configure().setProperty("hibernate.connection.url", getUrl(h2dir)) .buildSessionFactory();
	    //Object o = new net.sf.ehcache.hibernate.EhCacheRegionFactory();
	}

	if (session == null) {
	    //Session sess = factory.openSession();
	    session = factory.getCurrentSession();
	}

	if (session != null) {
	    if (!session.isOpen()) {
		session = factory.openSession();
	    }
	}

	if (transaction == null) {
	    transaction = session.beginTransaction();
	}

	return session;
    }

    public static void close() throws /*MappingException,*/ HibernateException, Exception {
	if (session != null && session.isOpen()) {
	    session.close();
	session = null;
	}
    }
    
    public static void commit() throws /*MappingException,*/ HibernateException, Exception {
	log.info("Doing hibernate commit");
	if (transaction != null) {
	transaction.commit();
	transaction = null;
	}
	/*
	if (session != null && session.isOpen()) {
	    session.close();
	session = null;
	}
	*/
    }

    public static <T> List<T> convert(List l, Class<T> type) {
        return (List<T>)l;
    }

    private static String getUrl(String h2dir) throws SQLException {
        return "jdbc:h2:" + h2dir;
    }
    
    private Connection getConnection(String h2dir) throws SQLException {
        String url = "jdbc:h2:" + h2dir;
        return DriverManager.getConnection(url);
    }
    
}
