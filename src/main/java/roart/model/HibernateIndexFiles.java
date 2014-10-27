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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.util.Constants;

@NamedQueries({
        @NamedQuery(name = "idxByFile",
		    query = "select idx from HibernateIndexFiles idx where :file in idx.filenames")
	    })

@Entity
    @Table(name = "Index")
    @org.hibernate.annotations.Table(appliesTo = "Index")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    public class HibernateIndexFiles implements Serializable {
	private static Logger log = LoggerFactory.getLogger("HibernateIndexFiles");
	private String md5;
	private Boolean indexed;
	private String timeindex;
	private String timestamp;
	private String timeclass;
	private String convertsw;
	private String converttime;
	private String classification;
	private Integer failed;
	private String failedreason;
	private String timeoutreason;
	private String noindexreason;
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
	 *  column="timeclass"
	 */
	@Column(name = "timeclass")
	
        public String getTimeclass() {
	    return timeclass;
	}

	public void setTimeclass(String timeclass) {
	    this.timeclass = timeclass;
	}

	/**
	 * @hibernate.property
	 *  column="timeindex"
	 */
	@Column(name = "timeindex")
	
        public String getTimeindex() {
	    return timeindex;
	}

	public void setTimeindex(String timeindex) {
	    this.timeindex = timeindex;
	}

	/**
	 * @hibernate.property
	 *  column="classification"
	 */
	@Column(name = "classification")
	
        public String getClassification() {
	    return classification;
	}

	public void setClassification(String classification) {
	    this.classification = classification;
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
	 *  column="converttime"
	 */
	@Column(name = "converttime")
	
        public String getConverttime() {
	    return converttime;
	}

	public void setConverttime(String converttime) {
	    this.converttime = converttime;
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

	@Column(name = "noindexreason")
        public String getNoindexreason() {
	    return noindexreason;
	}

	public void setNoindexreason(String noindexreason) {
	    this.noindexreason = noindexreason;
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
		log.error(Constants.EXCEPTION, e);
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
		log.error(Constants.EXCEPTION, e);
	    }
	    return null;
	}

	public static Set<String> getFilenamesByMd5(String md5) {
	    try {
		SQLQuery sqlQuery = HibernateUtil.currentSession().createSQLQuery("select filename from files where md5 = :md5");
		sqlQuery.setParameter("md5", md5);
		List<?> qResults = sqlQuery.list();
		log.info("results " + qResults.size() + " " + md5);
		Set<String> filenames = new HashSet<String>();
		for (Object row : qResults) {
		    String filename = (String) row;
		    filenames.add(filename);
		}
		return filenames;
	    } catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	    }
	    return null;
	}

	public static void flush() {
            try {
                roart.model.HibernateUtil.currentSession().flush();
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
	}

	public static void commit() {
            try {
                roart.model.HibernateUtil.commit();
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
	}

    }
