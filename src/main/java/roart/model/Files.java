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
    @Table(name = "Files")
    @org.hibernate.annotations.Table(appliesTo = "Files")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    public class Files implements Serializable {
	private Log log = LogFactory.getLog(this.getClass());

	private String md5;
	private String filename;
	//	private Boolean touched;

	/**
	 * @hibernate.property
	 *  column="sl_vs_id"
	 *  index="drug_vsid"
	 */
	@Column(name = "md5")
	
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
	@Column(name = "filename", length=511)
	@Id
        public String getFilename() {
	    return filename;
	}

	public void setFilename(String filename) {
	    this.filename = filename;
	}

	/**
	 * @hibernate.property
	 *  column="touched"
	 */
	/*
	@Column(name = "touched")
	
        public Boolean getTouched() {
	    return touched;
	}

	public void setTouched(Boolean touched) {
	    this.touched = touched;
	}
	*/

	public static Files ensureExistence(String filename) throws Exception {
	    Files fi = getByFilename(filename);
	    if (fi == null) {
		fi = new Files();
		fi.setFilename(filename);
		HibernateUtil.getCurrentSession().save(fi);
	    }
	    return fi;
	}

	public static Files getByFilename(String filename) throws Exception {
	    return (Files) HibernateUtil.currentSession().createQuery("from Files where filename = :filename").setParameter("filename", filename).uniqueResult();
	}

	public static List<Files> getByMd5(String md5) throws Exception {
	    return HibernateUtil.convert(HibernateUtil.currentSession().createQuery("from Files where md5 = :md5").setParameter("md5", md5).list(), Files.class);
	}

	public static List<Files> getAll() throws Exception {
	    return HibernateUtil.convert(HibernateUtil.currentSession().createQuery("from Files").list(), Files.class);
	}

    }
