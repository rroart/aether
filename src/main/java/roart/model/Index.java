package roart.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
//import org.hibernate.annotations.Index;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Entity
    @Table(name = "Index")
    @org.hibernate.annotations.Table(appliesTo = "Index")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    public class Index implements Serializable {
	private Log log = LogFactory.getLog(this.getClass());
	private String md5;
	private Boolean indexed;

	/**
	 * @hibernate.property
	 *  column="sl_vs_id"
	 *  index="drug_vsid"
	 */
	@Column(name = "md5")
	@Id
        public String getMd5() {
	    return md5;
	}

	public void setMd5(String md5) {
	    this.md5 = md5;
	}

	/**
	 * @hibernate.property
	 *  column="filename"
	 */
	/*
	@Column(name = "filename")
	
        public String getFilename() {
	    return filename;
	}

	public void setFilename(String filename) {
	    this.filename = filename;
	}
	*/

	/**
	 * @hibernate.property
	 *  column="indexed"
	 */
	@Column(name = "indexed")
	
        public Boolean getIndexed() {
	    return indexed;
	}

	public void setIndexed(Boolean indexed) {
	    this.indexed = indexed;
	}

	public static Index ensureExistence(String md5) throws Exception {
	    Index fi = getByMd5(md5);
	    if (fi == null) {
		fi = new Index();
		fi.setMd5(md5);
		HibernateUtil.currentSession().save(fi);
	    }
	    return fi;
	}

	public static Index getByMd5(String md5) throws Exception {
	    return (Index) HibernateUtil.getHibernateSession().createQuery("from Index where md5 = :md5").setParameter("md5", md5).uniqueResult();
	}

	public static List<Index> getAll() throws Exception {
	    return HibernateUtil.convert(HibernateUtil.currentSession().createQuery("from Index").list(), Index.class);
	}

    }
