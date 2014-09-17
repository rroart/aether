package roart.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
//import org.hibernate.annotations.Index;

import org.hibernate.Session;
import org.hibernate.SQLQuery;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
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

@NamedQueries({
        @NamedQuery(name = "idxByFile",
		    query = "select idx from HibernateIndexFiles idx where :file in idx.filenames")
	    })

@Entity
    @Table(name = "Index")
    @org.hibernate.annotations.Table(appliesTo = "Index")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    public class HibernateIndexFiles implements Serializable {
	private static Log log = LogFactory.getLog("HibernateIndexFiles");
	private String md5;
	private Boolean indexed;
	private String timestamp;
	private String convertsw;
	private Integer failed;
	private String failedreason;
	private String timeoutreason;
	private Set<String> filenames;

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

	/**
	 * @hibernate.property
	 *  column="timestamp"
	 */
	@Column(name = "timestamp")
	
        public String getTimestamp() {
	    return timestamp;
	}

	public void setTimestamp(String timestamp) {
	    this.timestamp = timestamp;
	}

	/**
	 * @hibernate.property
	 *  column="convertsw"
	 */
	@Column(name = "convertsw")
	
        public String getConvertsw() {
	    return convertsw;
	}

	public void setConvertsw(String convertsw) {
	    this.convertsw = convertsw;
	}

	/**
	 * @hibernate.property
	 *  column="failed"
	 */
	@Column(name = "failed")
	
        public Integer getFailed() {
	    if (failed == null) {
		failed = new Integer(0);
	    }
	    return failed;
	}

	public void setFailed(Integer failed) {
	    this.failed = failed;
	}

        public void incrFailed() {
	    if (failed == null) {
		failed = new Integer(0);
	    }
	    failed++;
	}

	public static HibernateIndexFiles ensureExistence(String md5) throws Exception {
	    HibernateIndexFiles fi = getByMd5(md5);
	    if (fi == null) {
		fi = new HibernateIndexFiles();
		fi.setMd5(md5);
		HibernateUtil.currentSession().save(fi);
	    }
	    return fi;
	}

	public static HibernateIndexFiles getByMd5(String md5) throws Exception {
	    return (HibernateIndexFiles) HibernateUtil.getHibernateSession().createQuery("from HibernateIndexFiles where md5 = :md5").setParameter("md5", md5).uniqueResult();
	}

	public static List<HibernateIndexFiles> getAll() throws Exception {
	    return HibernateUtil.convert(HibernateUtil.currentSession().createQuery("from HibernateIndexFiles").list(), HibernateIndexFiles.class);
	}

	@Column(name = "timeoutreason")
        public String getTimeoutreason() {
	    return timeoutreason;
	}

	public void setTimeoutreason(String timeoutreason) {
	    this.timeoutreason = timeoutreason;
	}

	@Column(name = "failedreason")
        public String getFailedreason() {
	    return failedreason;
	}

	public void setFailedreason(String failedreason) {
	    this.failedreason = failedreason;
	}

	/*
    @OneToMany
    @JoinTable(name = "files",
	       joinColumns = @JoinColumn(name = "md5", nullable =
					 false),
	       inverseJoinColumns = @JoinColumn(name = "filename", nullable = false))
						@Cascade({ CascadeType.ALL })
	*/
	//@OneToMany(targetEntity=String.class, mappedBy="college", fetch=FetchType.EAGER)
	//@Column
	@ElementCollection	
	@CollectionTable(name = "files", joinColumns = @JoinColumn(name = "md5"))

	@Column(name = "filename")
        public Set<String> getFilenames() {
	    return filenames;
	}

	public void setFilenames(Set<String> files) {
	    this.filenames = files;
	}

	public static HibernateIndexFiles getByFilename(String filename) {
	    try {
		String md5 = getMd5ByFilename(filename);
		if (md5 == null) {
		    return null;
		}
		return getByMd5(md5);
	    } catch (Exception e) {
		log.error("Exception", e);
	    }
	    return null;
	}

	public static String getMd5ByFilename(String filename) {
	    try {
		/*
		return (HibernateIndexFiles) HibernateUtil.getHibernateSession().getNamedQuery("idxByFile").setParameter("file", filename).uniqueResult();
		*/
		SQLQuery sqlQuery = HibernateUtil.currentSession().createSQLQuery("select md5 from files where filename = :filename");
		sqlQuery.setParameter("filename", filename);
		List<?> qResults = sqlQuery.list();
		log.info("results " + qResults.size() + " " + filename);
		String md5 = null;
		for (Object row : qResults) {
		    md5 = (String) row;
		}
		return md5;
	    } catch (Exception e) {
		log.error("Exception", e);
	    }
	    return null;
	}

    }
