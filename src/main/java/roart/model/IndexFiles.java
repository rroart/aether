package roart.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Date;

import roart.dao.IndexFilesDao;
import roart.dir.Traverse;
import roart.service.ControlService;
import roart.util.Prop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IndexFiles {

	private static Log log = LogFactory.getLog("IndexFiles");
	private String md5;
	private Boolean indexed;
    private String timeclass;
	private String timeindex;
	private String timestamp;
	private String convertsw;
	private String converttime;
    private String classification;
	private Integer failed;
    private String failedreason;
    private String noindexreason;
    private String timeoutreason;
	private Set<FileLocation> filelocations;
    private int maxfilelocations; // keep max count, for hbase deletions

    private boolean changed = false;
    private boolean indb = false;

	private IndexFiles() {
	    filelocations = new HashSet<FileLocation>();
	    maxfilelocations = 0;
	}

	public IndexFiles(String md5) {
	    filelocations = new HashSet<FileLocation>();
	    maxfilelocations = 0;
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

    public int getMaxfilelocations() {
	return maxfilelocations;
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

        public Date getTimestampDate() {
	    if (timestamp == null) {
		return new Date(0);
	    }
	    try { 
		Long date = new Long(timestamp);
		return new Date(date.longValue());
	    } catch (Exception e) {
		log.error("Exception from " + timestamp);
		log.error("Exception", e);
		return new Date(0);
	    }
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
	    int fl = timeindex.indexOf(".");
	    if (fl >= 0) {
		timeindex = timeindex.substring(0, fl-1) + "000"; // temp
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
	    int fl = converttime.indexOf(".");
	    if (fl >= 0) {
		converttime = converttime.substring(0, fl-1) + "000"; // temp
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

	public String getClassification() {
	    return classification;
	}

	public void setClassification(String classification) {
	    changed |= true;
	    this.classification = classification;
	}

	public void setTimeclass(String timeclass) {
	    changed |= true;
	    this.timeclass = timeclass;
	}

	public void setTimeclass(long millis) {
	    changed |= true;
	    this.timeclass = "" + millis;
	}

        public String getTimeclass() {
	    return timeclass;
	}

        public String getTimeclass(String format) {
	    if (timeclass == null) {
		return null;
	    }
	    int fl = timeclass.indexOf(".");
	    if (fl >= 0) {
		timeclass = timeclass.substring(0, fl-1) + "000"; // temp
	    }
	    return String.format(format, (float) new Long(timeclass).longValue()/1000);
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
	    if (filelocations.size() > maxfilelocations) {
		maxfilelocations = filelocations.size();
	    }
	    //IndexFilesDao.ensureExistence(filelocation);
	}

	public void addFile(String filename) {
	    String nodename = ControlService.nodename;
	    FileLocation fl = new FileLocation(nodename, filename);
	    addFile(fl);
	}

	public boolean removeFile(String filename) {
	    String nodename = ControlService.nodename;
	    FileLocation fl = new FileLocation(nodename, filename);
	    return removeFilelocation(fl);
	}

	public boolean removeFilelocation(FileLocation fl) {
	    boolean removed = filelocations.remove(fl);
	    changed |= removed;
	    return removed;
	}

        public String getTimeoutreason() {
	    return timeoutreason;
	}

	public void setTimeoutreason(String timeoutreason) {
	    changed |= true;
	    this.timeoutreason = timeoutreason;
	}

        public String getNoindexreason() {
	    return noindexreason;
	}

	public void setNoindexreason(String noindexreason) {
	    changed |= true;
	    this.noindexreason = noindexreason;
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

	public static ResultItem getHeader() {
	String myclassify = roart.util.Prop.getProp().getProperty("classify");
	boolean doclassify = myclassify != null && myclassify.length() > 0;
	
	ResultItem ri = new ResultItem();
	ri.add("Indexed");
	ri.add("Md5/Id");
	ri.add("Node");
	ri.add("Filename");
	ri.add("Lang");
	if (doclassify) {
	ri.add("Classification");
	}
	ri.add("Timestamp");
	ri.add("Convertsw");
	ri.add("Converttime");
	ri.add("Indextime");
	if (doclassify) {
	ri.add("Classificationtime");
	}
	ri.add("Failed");
	ri.add("Failed reason");
	ri.add("Timeout reason");
	ri.add("No indexing reason");
	ri.add("Filenames");
	return ri;
	}

	public static ResultItem getHeaderSearch(SearchDisplay display) {
	boolean doclassify = display.classify;
	boolean admin = display.admindisplay;
	
	ResultItem ri = new ResultItem();
	ri.add("Score");
	ri.add("Md5/Id");
	ri.add("Node");
	ri.add("Filename");
	ri.add("Lang");
	if (doclassify) {
	ri.add("Classification");
	}
	ri.add("Timestamp");
	if (admin) {
	ri.add("Convertsw");
	ri.add("Converttime");
	ri.add("Indextime");
	if (doclassify) {
	ri.add("Classificationtime");
	}
	ri.add("Failed");
	ri.add("Failed reason");
	ri.add("Timeout reason");
	ri.add("No indexing reason");
	ri.add("Filenames");
	}
	return ri;
	}

	public static ResultItem getSearchResultItem(IndexFiles index, String lang, float score, SearchDisplay display) {
	boolean doclassify = display.classify;
	boolean admin = display.admindisplay;
	
	ResultItem ri = new ResultItem();
	ri.add("" + score);
	ri.add(index.getMd5());
	FileLocation fl = Traverse.getExistingLocalFilelocationMaybe(index);
	String nodename = null;
	String filename = null;
	if (fl != null) {
	    nodename = fl.getNodeNoLocalhost();
	    filename = fl.getFilename();
	}
	ri.add(nodename);
	ri.add(filename);
	ri.add(lang);
	if (doclassify) {
	    ri.add(index.getClassification());
	}
	ri.add(index.getTimestampDate().toString());
	if (admin) {
	ri.add(index.getConvertsw());
	ri.add(index.getConverttime("%.2f"));
	ri.add(index.getTimeindex("%.2f"));
	if (doclassify) {
	    ri.add(index.getTimeclass("%.2f"));
	}
	ri.add("" + index.getFailed());
	ri.add(index.getFailedreason());
	ri.add(index.getTimeoutreason());
	ri.add(index.getNoindexreason());
	ri.add("" + index.getFilelocations().size());
	}
	return ri;
	}

	public static ResultItem getResultItem(IndexFiles index, String lang) {
	String myclassify = roart.util.Prop.getProp().getProperty("classify");
	boolean doclassify = myclassify != null && myclassify.length() > 0;
	
	ResultItem ri = new ResultItem();
	ri.add("" + index.getIndexed());
	ri.add(index.getMd5());
	FileLocation fl = Traverse.getExistingLocalFilelocationMaybe(index);
	String nodename = null;
	String filename = null;
	if (fl != null) {
	    nodename = fl.getNodeNoLocalhost();
	    filename = fl.getFilename();
	}
	ri.add(nodename);
	ri.add(filename);
	ri.add(lang);
	if (doclassify) {
	    ri.add(index.getClassification());
	}
	ri.add(index.getTimestampDate().toString());
	ri.add(index.getConvertsw());
	ri.add(index.getConverttime("%.2f"));
	ri.add(index.getTimeindex("%.2f"));
	if (doclassify) {
	    ri.add(index.getTimeclass("%.2f"));
	}
	ri.add("" + index.getFailed());
	ri.add(index.getFailedreason());
	ri.add(index.getTimeoutreason());
	ri.add(index.getNoindexreason());
	ri.add("" + index.getFilelocations().size());
	return ri;
	}

    }
