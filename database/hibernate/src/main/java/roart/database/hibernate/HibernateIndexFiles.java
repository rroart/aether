package roart.database.hibernate;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
//import org.hibernate.annotations.Index;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
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

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.database.DatabaseConstructorParam;
import roart.common.model.FileLocation;
import roart.common.model.IndexFiles;

@NamedQueries({
        @NamedQuery(name = "idxByFile",
		    query = "select idx from HibernateIndexFiles idx left join idx.filenames filename where filename = :file")
	    })

@Entity
    @Table(name = "Index")
    @org.hibernate.annotations.Table(appliesTo = "Index")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    public class HibernateIndexFiles implements Serializable {
	private Logger log = LoggerFactory.getLogger(HibernateIndexFiles.class);
	private String md5;
	private Boolean indexed;
	private String timeindex;
	private String timestamp;
	private String timeclass;
	private Integer size;
	private Integer convertsize;
	private String convertsw;
	private String converttime;
	private String classification;
	private String mimetype;
	private Integer failed;
	private String failedreason;
	private String timeoutreason;
	private String noindexreason;
	private Set<String> filenames;
	private String language;
	private String isbn;
        private String created;
        private String checked;
	
	private String configname;
	private NodeConfig nodeconf;
	
	public HibernateIndexFiles(String configname, NodeConfig nodeConf) {
        this.configname = configname;
        this.nodeconf = nodeConf;
    }

	public HibernateIndexFiles() {
	}
	       
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
         *  column="mimetype"
         */
        @Column(name = "mimetype")
        
        public String getMimetype() {
            return mimetype;
        }

        public void setMimetype(String mimetype) {
            this.mimetype = mimetype;
        }

       /**
         * @hibernate.property
         *  column="size"
         */
        @Column(name = "size")
        
        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        /**
         * @hibernate.property
         *  column="convertsw"
         */
        @Column(name = "convertsize")
        
        public Integer getConvertsize() {
            return convertsize;
        }

        public void setConvertsize(Integer convertsize) {
            this.convertsize = convertsize;
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

    	/**
    	 * @hibernate.property
    	 *  column="language"
    	 */
    	@Column(name = "language")
    	
            public String getLanguage() {
    	    return language;
    	}

    	public void setLanguage(String language) {
    	    this.language = language;
    	}

        @Column(name = "isbn")
        
        public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * @hibernate.property
     *  column="timestamp"
     */
    @Column(name = "created")
    
    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    /**
     * @hibernate.property
     *  column="timestamp"
     */
    @Column(name = "checked")
    
    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

	public HibernateIndexFiles ensureExistence(String md5) throws Exception {
	    HibernateIndexFiles fi = getByMd5(md5);
	    if (fi == null) {
		fi = new HibernateIndexFiles(configname, null);
		fi.setMd5(md5);
		log.info("Saving " + this.hashCode() + " " + md5);
		HibernateUtil.currentSession(getH2Dir()).persist(fi);
	    }
	    return fi;
	}

    @Transient
	public HibernateIndexFiles getByMd5(String md5) throws Exception {
	    return (HibernateIndexFiles) HibernateUtil.getHibernateSession(getH2Dir()).createQuery("from HibernateIndexFiles where md5 = :md5").setParameter("md5", md5).uniqueResult();
	    // this is slower:
	    // return (HibernateIndexFiles) HibernateUtil.getHibernateSession().get(HibernateIndexFiles.class, md5);
	}

	@Transient
	public List<HibernateIndexFiles> getAll() throws Exception {
	    return HibernateUtil.convert(HibernateUtil.currentSession(getH2Dir()).createQuery("from HibernateIndexFiles").list(), HibernateIndexFiles.class);
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
	/*
        @ManyToMany(targetEntity=String.class)
        @JoinTable(name = "files",
        joinColumns = @JoinColumn(name = "md5"),
        inverseJoinColumns = @JoinColumn(name = "filename")
        )
        */
	@ElementCollection	
	@CollectionTable(name = "files", joinColumns = @JoinColumn(name = "md5", unique = false), uniqueConstraints = {@UniqueConstraint(columnNames={"md5", "filename"})})
	@Column(name = "filename", length = 511, nullable = false, unique = false)
	public Set<String> getFilenames() {
	    return filenames;
	}

	public void setFilenames(Set<String> files) {
	    this.filenames = files;
	}

    @Transient
	public HibernateIndexFiles getByFilename(String filename) {
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

	// this is hql, but slow?
    @Transient
	public String getMd5ByFilenameNot(String filename) {
	    try {
			HibernateIndexFiles hif = (HibernateIndexFiles) HibernateUtil.getHibernateSession(getH2Dir()).createNamedQuery("idxByFile", HibernateIndexFiles.class).setParameter("file", filename).uniqueResult();
			if (hif == null) {
				return null;
			}
			return hif.getMd5();
	    } catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	    }
	    return null;
	}

	// using sql is ideally incorrect, but faster
    @Transient
	public String getMd5ByFilename(String filename) {
	    try {
		String md5 = (String) HibernateUtil.getHibernateSession(getH2Dir()).createNativeQuery("select md5 from files where filename = :file").setParameter("file", filename).uniqueResult();
		return md5;
	    } catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	    }
	    return null;
	}

    @Transient
	public Set<FileLocation> getFilelocationsByMd5(String md5) {
	    try {
	    	HibernateIndexFiles ifile = getByMd5(md5);
	    	if (ifile != null) {
	    		Set<String> fls = ifile.getFilenames();
	    		return FileLocation.getFilelocations(fls, configname);
	    	}
	    } catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	    }
	    return null;
	}

	public void flush() throws Exception {
                roart.database.hibernate.HibernateUtil.currentSession(getH2Dir()).flush();
	}

	public void commit() throws Exception {
                roart.database.hibernate.HibernateUtil.commit();
                //HibernateUtil.currentSession(nodeconf.getH2dir()).clear();
	}

	public void close() throws Exception {
                roart.database.hibernate.HibernateUtil.close();
	}

    @Transient
	public Set<String> getAllMd5() throws Exception {
		return (Set<String>) HibernateUtil.convert(HibernateUtil.currentSession(getH2Dir()).createSelectionQuery("select md5 from HibernateIndexFiles").list(), String.class);
	}

    @Transient
	public Set<String> getLanguages() throws Exception {
		return new HashSet<String>(HibernateUtil.convert(HibernateUtil.currentSession(getH2Dir()).createSelectionQuery("select distinct language from HibernateIndexFiles").list(), String.class));
	}

    public void delete(IndexFiles index) throws Exception {
        HibernateIndexFiles hif = getByMd5(index.getMd5());
        roart.database.hibernate.HibernateUtil.currentSession(getH2Dir()).remove(hif);
    }

    public void destroy() throws Exception {
       HibernateUtil.close();
    }

    @Transient
    public String getH2Dir() {
        return nodeconf.getH2dir();
    }

    public void clear(DatabaseConstructorParam param) {
        try {
            HibernateUtil.currentSession(getH2Dir()).createNativeMutationQuery("delete from FILES").executeUpdate();
            HibernateUtil.currentSession(getH2Dir()).createNativeMutationQuery("delete from INDEX").executeUpdate();
            //HibernateUtil.currentSession(getH2Dir()).createNativeMutationQuery("delete from HibernateIndexFiles").executeUpdate();
            //HibernateUtil.currentSession(getH2Dir());
    } catch (Exception e) {
        log.error(Constants.EXCEPTION, e);
    }
    }

    public void drop(DatabaseConstructorParam param) {
 try {
     //HibernateUtil.currentSession(getH2Dir()).createQuery("DROP ALL OBJECTS DELETE FILES");
     HibernateUtil.currentSession(getH2Dir()).createNativeMutationQuery("DROP TABLE FILES").executeUpdate();
     HibernateUtil.currentSession(getH2Dir()).createNativeMutationQuery("DROP TABLE INDEX").executeUpdate();
     HibernateUtil.currentSession(getH2Dir()).createNativeMutationQuery("DROP ALL OBJECTS DELETE FILES").executeUpdate();
 
    } catch (Exception e) {
        log.error(Constants.EXCEPTION, e);
    }
    }

    public void save() {
        try {
        HibernateUtil.currentSession(getH2Dir()).saveOrUpdate(this);
    } catch (Exception e) {
        log.error(Constants.EXCEPTION, e);
    }
    }    
    }
