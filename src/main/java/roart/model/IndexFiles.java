package roart.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import roart.dao.IndexFilesDao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IndexFiles {

	private static Log log = LogFactory.getLog("IndexFiles");
	private String md5;
	private Boolean indexed;
	private String timeindex;
	private String timestamp;
	private String convertsw;
	private String converttime;
	private Integer failed;
    private String failedreason;
    private String timeoutreason;
	private Set<FileLocation> filelocations;

    private boolean changed = false;
    private boolean indb = false;

	private IndexFiles() {
	    filelocations = new HashSet<FileLocation>();
	}

	public IndexFiles(String md5) {
	    filelocations = new HashSet<FileLocation>();
	    this.setMd5(md5);
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
	    changed |= true;
	    this.indexed = indexed;
	}

        public String getTimestamp() {
	    return timestamp;
	}

	public void setTimestamp(String timestamp) {
	    changed |= true;
	    this.timestamp = timestamp;
	}

        public String getTimeindex() {
	    return timeindex;
	}

        public String getTimeindex(String format) {
	    if (timeindex == null) {
		return null;
	    }
	    return String.format(format, (float) new Long(timeindex).longValue()/1000);
	}

	public void setTimeindex(String timeindex) {
	    changed |= true;
	    this.timeindex = timeindex;
	}

	public void setTimeindex(long millis) {
	    changed |= true;
	    this.timeindex = "" + millis;
	}

        public String getConvertsw() {
	    return convertsw;
	}

	public void setConvertsw(String convertsw) {
	    changed |= true;
	    this.convertsw = convertsw;
	}

        public String getConverttime() {
	    return converttime;
	}

        public String getConverttime(String format) {
	    if (converttime == null) {
		return null;
	    }
	    return String.format(format, (float) new Long(converttime).longValue()/1000);
	}

	public void setConverttime(String converttime) {
	    changed |= true;
	    this.converttime = converttime;
	}

	public void setConverttime(long millis) {
	    changed |= true;
	    this.converttime = "" + millis;
	}

        public Integer getFailed() {
	    if (failed == null) {
		failed = new Integer(0);
	    }
	    return failed;
	}

	public void setFailed(Integer failed) {
	    changed |= true;
	    this.failed = failed;
	}

        public void incrFailed() {
	    changed |= true;
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
	    changed |= true;
	    this.filelocations = filelocations;
	}

	public void addFile(FileLocation filelocation) {
	    changed |= true;
	    filelocations.add(filelocation);
	    //IndexFilesDao.ensureExistence(filelocation);
	}

	public void addFile(String filename) {
	    String nodename = roart.util.Prop.getProp().getProperty("nodename");
	    FileLocation fl = new FileLocation(nodename, filename);
	    addFile(fl);
	}

	public boolean removeFile(String filename) {
	    String nodename = roart.util.Prop.getProp().getProperty("nodename");
	    FileLocation fl = new FileLocation(nodename, filename);
	    return filelocations.remove(fl);
	}

	public boolean removeFilelocation(FileLocation fl) {
	    return filelocations.remove(fl);
	}

        public String getTimeoutreason() {
	    return timeoutreason;
	}

	public void setTimeoutreason(String timeoutreason) {
	    changed |= true;
	    this.timeoutreason = timeoutreason;
	}

        public String getFailedreason() {
	    return failedreason;
	}

	public void setFailedreason(String failedreason) {
	    changed |= true;
	    this.failedreason = failedreason;
	}

    /*
    public void save() {
	all.remove(this);
	IndexFilesDao.save(this);
    }
    */

    public boolean hasChanged() {
	return changed;
    }

    public void setUnchanged() {
	changed = false;
    }

    public void setDbNot() {
	indb = true;
    }

    public boolean inDbNot() {
	return indb;
    }

    }
