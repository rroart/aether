package roart.database;

import javax.jdo.Query;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.datanucleus.store.query.QueryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.model.FileLocation;
import roart.util.Constants;

@PersistenceCapable(table="IndexFiles")
    public class DataNucleusIndexFiles /*implements Serializable*/ {
	/**
	 * @author roart
	 *
	 */
	
	private static Logger log = LoggerFactory.getLogger(DataNucleusIndexFiles.class);

	@Column(name = "md5")
	@Persistent	
	@PrimaryKey
	private String md5;
        public String getMd5() {
	    return md5;
	}

	public void setMd5(String md5) {
	    this.md5 = md5;
	}

	@Column(name = "indexed")
	@Persistent	
	private Boolean indexed;
	
        public Boolean getIndexed() {
	    return indexed;
	}

	public void setIndexed(Boolean indexed) {
	    this.indexed = indexed;
	}

	@Column(name = "timestamp")
	@Persistent	
	private String timestamp;
	
        public String getTimestamp() {
	    return timestamp;
	}

	public void setTimestamp(String timestamp) {
	    this.timestamp = timestamp;
	}

	@Column(name = "timeclass")
	@Persistent	
	private String timeclass;
	
        public String getTimeclass() {
	    return timeclass;
	}

	public void setTimeclass(String timeclass) {
	    this.timeclass = timeclass;
	}

	@Column(name = "timeindex")
	@Persistent
	private String timeindex;
	
        public String getTimeindex() {
	    return timeindex;
	}

	public void setTimeindex(String timeindex) {
	    this.timeindex = timeindex;
	}

	@Column(name = "classification")
	@Persistent	
	private String classification;
	
        public String getClassification() {
	    return classification;
	}

	public void setClassification(String classification) {
	    this.classification = classification;
	}

	@Column(name = "convertsw")
	@Persistent	
	private String convertsw;
	
        public String getConvertsw() {
	    return convertsw;
	}

	public void setConvertsw(String convertsw) {
	    this.convertsw = convertsw;
	}

	@Column(name = "converttime")
	@Persistent	
	private String converttime;
	
        public String getConverttime() {
	    return converttime;
	}

	public void setConverttime(String converttime) {
	    this.converttime = converttime;
	}

	@Column(name = "failed")
	@Persistent	
	private Integer failed;
	
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

    	@Column(name = "language")
    	@Persistent	
    	private String language;
    	
            public String getLanguage() {
    	    return language;
    	}

    	public void setLanguage(String language) {
    	    this.language = language;
    	}

	public static DataNucleusIndexFiles ensureExistence(String md5) throws Exception {
	    DataNucleusIndexFiles fi = getByMd5(md5);
	    if (fi == null) {
		fi = new DataNucleusIndexFiles();
		fi.setMd5(md5);
		DataNucleusUtil.currentSession().save(fi);
	    }
	    return fi;
	}

	public static DataNucleusIndexFiles getByMd5(String md5) throws Exception {
	    List<DataNucleusIndexFiles> dnifs = null;
	    //Query query = DataNucleusUtil.currentSession().getPm().newQuery("select from " + DataNucleusIndexFiles.class.getName() + " where md5 == :md5");
	    	// query.setFilter?
	    Query query = DataNucleusUtil.currentSession().getPm().newQuery(DataNucleusIndexFiles.class);
	    query.setFilter("md5 == mymd5");
	    	query.declareParameters(String.class.getName() + " mymd5");
	    	dnifs = (List<DataNucleusIndexFiles>) query.execute(md5);
	    	if (dnifs == null || dnifs.size() == 0) {
	    		return null;
	    	}
	    	return dnifs.get(0);
	    //return (DataNucleusIndexFiles) DataNucleusUtil.getDataNucleusSession().createQuery("select dnif from DataNucleusIndexFiles dnif where md5 = :md5").setParameter("md5", md5).getSingleResult();
	}

	public static List<DataNucleusIndexFiles> getAll() throws Exception {
	    	List<DataNucleusIndexFiles> dnifs = null;
	    	Query query = DataNucleusUtil.currentSession().getPm().newQuery("select from " + DataNucleusIndexFiles.class.getName());
	    	dnifs = (List<DataNucleusIndexFiles>) query.execute();
	    	return dnifs;
	    //return DataNucleusUtil.convert(DataNucleusUtil.currentSession().createQuery("select dnif from DataNucleusIndexFiles dnif").getResultList(), DataNucleusIndexFiles.class);
	}

	@Column(name = "timeoutreason")
	@Persistent	
	private String timeoutreason;

        public String getTimeoutreason() {
	    return timeoutreason;
	}

	public void setTimeoutreason(String timeoutreason) {
	    this.timeoutreason = timeoutreason;
	}

	@Column(name = "noindexreason")
	@Persistent	
	private String noindexreason;

        public String getNoindexreason() {
	    return noindexreason;
	}

	public void setNoindexreason(String noindexreason) {
	    this.noindexreason = noindexreason;
	}

	@Column(name = "failedreason")
	@Persistent	
	private String failedreason;

        public String getFailedreason() {
	    return failedreason;
	}

	public void setFailedreason(String failedreason) {
	    this.failedreason = failedreason;
	}
	
	@Column(name = "filename")
	@Persistent	
	private Set<String> filelocations;

        public Set<String> getFilelocations() {
	    return filelocations;
	}

	public void setFilelocations(Set<FileLocation> filelocations) {
	    this.filelocations = FileLocation.getFilelocationsToString(filelocations);
	}

	public static DataNucleusIndexFiles getByFilename(FileLocation fl) {
	    try {
		String md5 = getMd5ByFilename(fl);
		if (md5 == null) {
		    return null;
		}
		return getByMd5(md5);
	    } catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	    }
	    return null;
	}

	public static String getMd5ByFilename(FileLocation fl) {
	    try {
	    	List<DataNucleusIndexFiles> dnifs = null;
	    	Query query = DataNucleusUtil.currentSession().getPm().newQuery("select from " + DataNucleusIndexFiles.class.getName() + " where filelocations.contains(fl)");
	    	// query.setFilter?
	    	query.declareParameters(FileLocation.class.getName() + " fl");
	    	//Object o = query.execute(filename);
		//log.info("result " + o);
	    	dnifs = (List<DataNucleusIndexFiles>) query.execute(fl);
	    	if (dnifs == null || dnifs.size() == 0) {
	    		return null;
	    	}
	    	return dnifs.get(0).getMd5();
	    } catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	    }
	    return null;
	}

	public static Set<FileLocation> getFilelocationsByMd5(String md5) {
	    try {
	    	DataNucleusIndexFiles dnif = getByMd5(md5);
	    	return FileLocation.getFilelocations(dnif.getFilelocations());
	    } catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	    }
	    return null;
	}

	public static void flush() throws Exception {
                 roart.database.DataNucleusUtil.currentSession().flush();
	}

	public static void commit() throws Exception {
                roart.database.DataNucleusUtil.commit();
	}

	public static Set<String> getAllMd5() throws Exception {
    	Set<String> md5s = null;
    	Query query = DataNucleusUtil.currentSession().getPm().newQuery("select md5 from " + DataNucleusIndexFiles.class.getName());
    	md5s = (Set<String>) query.execute();
    	return md5s;
	}

	public static Set<String> getLanguages() throws Exception {
    	Set<String> languages = null;
    	Query query = DataNucleusUtil.currentSession().getPm().newQuery("select distinct language from " + DataNucleusIndexFiles.class.getName());
    	languages = (Set<String>) query.execute();
    	return languages;
	}

    }
