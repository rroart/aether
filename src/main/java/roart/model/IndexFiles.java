package roart.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import roart.dao.IndexFilesDao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IndexFiles {

    private static Set<IndexFiles> all = new HashSet<IndexFiles>();

	private static Log log = LogFactory.getLog("IndexFiles");
	private String md5;
	private Boolean indexed;
	private String timestamp;
	private String convertsw;
	private Integer failed;
    private String failedreason;
    private String timeoutreason;
	private Set<FileLocation> filelocations;

	public IndexFiles() {
	    filelocations = new HashSet<FileLocation>();
	    all.add(this);
	}

        public String getMd5() {
	    return md5;
	}

	public void setMd5(String md5) {
	    this.md5 = md5;
	}

        public String getFilename() {
	    if (filelocations.size() == 0) {
		return null;
	    }
	    return ((FileLocation) (getFilelocations()).iterator().next()).getFilename();
	}

        public String getFilelocation() {
	    if (filelocations.size() == 0) {
		return null;
	    }
	    return ((FileLocation) (getFilelocations()).iterator().next()).toString();
	}

        public Boolean getIndexed() {
	    return indexed;
	}

	public void setIndexed(Boolean indexed) {
	    this.indexed = indexed;
	}

        public String getTimestamp() {
	    return timestamp;
	}

	public void setTimestamp(String timestamp) {
	    this.timestamp = timestamp;
	}

        public String getConvertsw() {
	    return convertsw;
	}

	public void setConvertsw(String convertsw) {
	    this.convertsw = convertsw;
	}

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

        public Set<FileLocation> getFilelocations() {
	    return filelocations;
	}

        public Set<String> getFilenames() {
	    Set<String> names = new HashSet<String>();
	    for (FileLocation fl : filelocations) {
		names.add(fl.getFilename());
	    }
	    return names;
	}

	public void setFilelocations(Set<FileLocation> filelocations) {
	    this.filelocations = filelocations;
	}

	public void addFile(FileLocation filelocation) {
	    filelocations.add(filelocation);
	    //IndexFilesDao.ensureExistence(filelocation);
	}

	public void addFile(String filename) {
	    String nodename = roart.util.Prop.getProp().getProperty("nodename");
	    FileLocation fl = new FileLocation(nodename, filename);
	    addFile(fl);
	}

	public boolean removeFile(String filelocation) {
	    return filelocations.remove(filelocation);
	}

        public String getTimeoutreason() {
	    return timeoutreason;
	}

	public void setTimeoutreason(String timeoutreason) {
	    this.timeoutreason = timeoutreason;
	}

        public String getFailedreason() {
	    return failedreason;
	}

	public void setFailedreason(String failedreason) {
	    this.failedreason = failedreason;
	}

    public void save() {
	all.remove(this);
	IndexFilesDao.save(this);
    }

    public static void commit() {
	String mydb = roart.util.Prop.getProp().getProperty("mydb");
	if (mydb.equals("hibernate")) {
	    try {
		roart.model.HibernateUtil.commit();
	    } catch (Exception e) {
		log.error("Exception", e);
	    }
	}
	if (mydb.equals("hbase")) {
	    for (IndexFiles i : all) {
		log.info("saving " + i.getMd5());
		i.save();
	    }
	    all.clear();
	}
    }

    }
