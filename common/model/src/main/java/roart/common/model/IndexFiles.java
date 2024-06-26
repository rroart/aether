package roart.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Date;

import roart.common.config.ConfigConstants;
import roart.common.config.MyConfig;
import roart.common.constants.Constants;
import roart.common.synchronization.MyLock;
import roart.common.synchronization.MyObjectLock;
import roart.common.synchronization.MyObjectLockData;
import roart.common.synchronization.MySemaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class IndexFiles {

    public static final int FILENAMECOLUMN = 3;
    public static final int MIMETYPECOLUMN = 4;

    private static Logger log = LoggerFactory.getLogger(IndexFiles.class);
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
    private String language;
    private String isbn;
    private String created;
    private String checked;
    private Integer size;
    private Integer convertsize;
    private String mimetype;
    private Integer version;

    private boolean changed = false;
    private boolean indb = false;

    private int priority;

    @JsonIgnore
    private MyLock flock;
    @JsonIgnore
    private MyLock lock;
    @JsonIgnore
    private Object lockqueue;

    @JsonIgnore
    private MySemaphore semaphoreflock;
    @JsonIgnore
    private MySemaphore semaphorelock;
    @JsonIgnore
    private Object semaphorelockqueue;

    private MyObjectLockData objectflock;

    private MyObjectLockData objectlock;

    private IndexFiles() {
        filelocations = new HashSet<>();
    }

    public IndexFiles(String md5) {
        filelocations = new HashSet<>();
        this.setMd5(md5);
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
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
        int fl = timeindex.indexOf(".");
        // TODO remove later, old compat
        if (fl >= 0) {
            timeindex = timeindex.substring(0, fl-1) + "000"; // temp
        }
        return String.format(format, (float) new Long(timeindex).longValue()/1000);
    }

    public void setTimeindex(String timeindex) {
        changed |= true;
        this.timeindex = timeindex;
    }

    /*
	public void setTimeindex(long millis) {
	    changed |= true;
	    this.timeindex = "" + millis;
	}
     */

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
        // TODO remove later, old compat
        if (fl >= 0) {
            converttime = converttime.substring(0, fl-1) + "000"; // temp
        }
        return String.format(format, (float) new Long(converttime).longValue()/1000);
    }

    public void setConverttime(String converttime) {
        changed |= true;
        this.converttime = converttime;
    }

    /*
	public void setConverttime(long millis) {
	    changed |= true;
	    this.converttime = "" + millis;
	}
     */

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

    /*
	public void setTimeclass(long millis) {
	    changed |= true;
	    this.timeclass = "" + millis;
	}
     */

    public String getTimeclass() {
        return timeclass;
    }

    public String getTimeclass(String format) {
        if (timeclass == null) {
            return null;
        }
        int fl = timeclass.indexOf(".");
        // TODO remove later, old compat
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

    public void setFilelocations(Set<FileLocation> filelocations) {
        changed |= true;
        this.filelocations = filelocations;
    }

    public boolean addFile(FileLocation filelocation) {
        boolean added = filelocations.add(filelocation);
        if (!added) {
            log.error("Already added {} {}", md5, filelocation);
        } else {
            changed |= true;
        }
        log.debug("fls {}", filelocations);
        if (filelocation.toString().length() > 500) {
            log.error("filename {}", filelocation.toString().length());                
        }
        return !added;
        //IndexFilesDao.ensureExistence(filelocation);
    }

    public boolean addFile(String nodename, String filename) {
        FileLocation fl = new FileLocation(nodename, filename);
        return addFile(fl);
    }

    public boolean removeFile(String nodename, String filename) {
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
        if (timeoutreason != null && timeoutreason.length() > 250) {
            log.error("timeoutreason {}", timeoutreason.length());
            timeoutreason = timeoutreason.substring(0, 250);
        }
    }

    public String getNoindexreason() {
        return noindexreason;
    }

    public void setNoindexreason(String noindexreason) {
        changed |= true;
        this.noindexreason = noindexreason;
        if (noindexreason != null && noindexreason.length() > 250) {
            log.error("noindexreason {}", noindexreason.length());
            noindexreason = noindexreason.substring(0, 250);
        }
    }

    public String getFailedreason() {
        return failedreason;
    }

    public void setFailedreason(String failedreason) {
        changed |= true;
        this.failedreason = failedreason;
        if (failedreason != null && failedreason.length() > 250) {
            log.error("failedreason {}", failedreason.length());
            failedreason = failedreason.substring(0, 250);
        }
    }

    public void setLanguage(String language) {
        changed |= true;
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    /*
    public void save() {
	all.remove(this);
	IndexFilesDao.save(this);
    }
     */

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        changed |= true;
        this.isbn = isbn;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getConvertsize() {
        return convertsize;
    }

    public void setConvertsize(Integer convertsize) {
        this.convertsize = convertsize;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

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

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public void setFlock(MyLock flock) {
        this.flock = flock;
    }

    public MyLock getFlock() {
        return flock;
    }

    public void setLock(MyLock lock) {
        this.lock = lock;
    }

    public MyLock getLock() {
        return lock;
    }

    public Object getLockqueue() {
        return lockqueue;
    }

    public void setLockqueue(Object lockqueue) {
        this.lockqueue = lockqueue;
    }

    public void setSemaphoreflock(MySemaphore semaphoreflock) {
        this.semaphoreflock = semaphoreflock;
    }

    public MySemaphore getSemaphoreflock() {
        return semaphoreflock;
    }

    public void setSemaphorelock(MySemaphore semaphorelock) {
        this.semaphorelock = semaphorelock;
    }

    public MySemaphore getSemaphorelock() {
        return semaphorelock;
    }

    public Object getSemaphorelockqueue() {
        return semaphorelockqueue;
    }

    public void setSemaphorelockqueue(Object semaphorelockqueue) {
        this.semaphorelockqueue = semaphorelockqueue;
    }

    public MyObjectLockData getObjectflock() {
        return objectflock;
    }

    public void setObjectflock(MyObjectLockData objectflock) {
        this.objectflock = objectflock;
    }

    public MyObjectLockData getObjectlock() {
        return objectlock;
    }

    public void setObjectlock(MyObjectLockData objectlock) {
        this.objectlock = objectlock;
    }

    @JsonIgnore
    public Date getTimestampDate() {
        if (timestamp == null) {
            return new Date(0);
        }
        try { 
            Long date = new Long(timestamp);
            return new Date(date.longValue());
        } catch (Exception e) {
            log.error("Exception from " + timestamp);
            log.error(Constants.EXCEPTION, e);
            return new Date(0);
        }
    }

    @JsonIgnore
    public Date getCheckedDate() {
        if (checked == null) {
            return new Date(0);
        }
        try { 
            Long date = new Long(checked);
            return new Date(date.longValue());
        } catch (Exception e) {
            log.error("Exception from " + checked);
            log.error(Constants.EXCEPTION, e);
            return new Date(0);
        }
    }

    // not used
    @JsonIgnore
    public Set<String> getFilenames() {
        Set<String> names = new HashSet<String>();
        for (FileLocation fl : filelocations) {
            names.add(fl.getFilename());
        }
        return names;
    }

    @JsonIgnore
    public String getFilename() {
        if (filelocations.size() == 0) {
            return null;
        }
        return ((FileLocation) (getFilelocations()).iterator().next()).getFilename();
    }

    @JsonIgnore
    public String getFilelocation() {
        if (filelocations.size() == 0) {
            return null;
        }
        return ((FileLocation) (getFilelocations()).iterator().next()).toString();
    }

    @JsonIgnore
    public FileLocation getaFilelocation() {
        if (filelocations.size() == 0) {
            return null;
        }
        return ((FileLocation) (getFilelocations()).iterator().next());
    }

    public static ResultItem getHeader() {
        boolean doclassify = MyConfig.conf.wantClassify();

        ResultItem ri = new ResultItem();
        ri.add("Indexed");
        ri.add("Md5/Id");
        ri.add("Node");
        ri.add("Filename");
        ri.add("Mimetype");
        ri.add("Lang");
        ri.add("ISBN");
        if (doclassify) {
            ri.add("Classification");
        }
        ri.add("Timestamp");
        ri.add("Updated");
        ri.add("Size");
        ri.add("Convertsize");
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

    public static ResultItem getHeaderSearch() {
        boolean doclassify = MyConfig.conf.wantClassify();
        boolean admin = MyConfig.conf.admin;
        boolean dohighlightmlt = MyConfig.conf.getHighlightmlt();

        ResultItem ri = new ResultItem();
        ri.add("Score");
        ri.add("Md5/Id");
        ri.add("Node");
        ri.add("Filename");
        ri.add("Mimetype");
        if (dohighlightmlt) {
            ri.add("Highlight and similar");
        }
        ri.add("Lang");
        ri.add("ISBN");
        if (doclassify) {
            ri.add("Classification");
        }
        ri.add("Timestamp");
        ri.add("Updated");
        if (admin) {
            ri.add("Size");
            ri.add("Convertsize");
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
            ri.add("Created");
            ri.add("Checked");
            ri.add("Metadata");
        }
        return ri;
    }

    public static ResultItem getSearchResultItem(IndexFiles index, String lang, float score, String[] highlights, List<String> metadata, String csnodename, FileLocation maybeFl) {
        boolean doclassify = MyConfig.conf.wantClassify();
        boolean admin = MyConfig.conf.admin;
        boolean dohighlightmlt = MyConfig.conf.getHighlightmlt();

        ResultItem ri = new ResultItem();
        ri.add("" + score);
        ri.add(index.getMd5());
        FileLocation fl = maybeFl;
        String nodename = null;
        String filename = null;
        if (fl != null) {
            nodename = fl.getNodeNoLocalhost(csnodename);
            filename = fl.getFilename();
        }
        ri.add(nodename);
        ri.add(filename);
        ri.add(index.getMimetype());
        if (dohighlightmlt) {
            if (highlights != null && highlights.length > 0) {
                ri.add(highlights[0]);
            } else {
                ri.add(null);
            }
        }
        ri.add(lang);
        ri.add(index.getIsbn());
        if (doclassify) {
            ri.add(index.getClassification());
        }
        ri.add(index.getTimestampDate().toString());
        ri.add(index.getCheckedDate().toString());
        if (admin) {
            ri.add("" + index.getSize());
            ri.add("" + index.getConvertsize());
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
            ri.add(index.getCreated());
            ri.add(index.getChecked());
            String metadatastring = "";
            if (metadata != null) {
                for (String md : metadata) {
                    metadatastring = metadatastring + md + "<br>";
                }  
            }
            ri.add(metadatastring);
        }
        return ri;
    }

    public static ResultItem getResultItem(IndexFiles index, String lang, String csnodename, FileLocation maybeFl) {
        boolean doclassify = MyConfig.conf.wantClassify();

        if (lang == null || lang.length() == 0) {
            lang = "n/a";
        }

        ResultItem ri = new ResultItem();
        ri.add("" + index.getIndexed());
        ri.add(index.getMd5());
        FileLocation fl = maybeFl;
        String nodename = null;
        String filename = null;
        if (fl != null) {
            nodename = fl.getNodeNoLocalhost(csnodename);
            filename = fl.getFilename();
        }
        ri.add(nodename);
        ri.add(filename);
        ri.add(index.getMimetype());
        ri.add(lang);
        ri.add(index.getIsbn());
        if (doclassify) {
            ri.add(index.getClassification());
        }
        ri.add(index.getTimestampDate().toString());
        ri.add(index.getCheckedDate().toString());
        ri.add("" + index.getSize());
        ri.add("" + index.getConvertsize());
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

    @Override
    public String toString() {
        return md5 + " " + filelocations;
    }

    @Override
    public boolean equals(Object object) {
        IndexFiles f = (IndexFiles) object;
        return Objects.equals(md5, f.md5)
                && Objects.equals(indexed, f.indexed)
                && Objects.equals(timeindex, f.timeindex)
                && Objects.equals(timestamp, f.timestamp)
                && Objects.equals(timeclass, f.timeclass)
                && Objects.equals(convertsw, f.convertsw)
                && Objects.equals(classification, f.classification)
                && Objects.equals(failed, f.failed)
                && Objects.equals(failedreason, f.failedreason)
                && Objects.equals(timeoutreason, f.timeoutreason)
                && Objects.equals(noindexreason, f.noindexreason)
                && Objects.equals(filelocations, f.filelocations)
                && Objects.equals(language, f.language)
                && Objects.equals(isbn, f.isbn)
                && Objects.equals(created, f.created)
                && Objects.equals(size, f.size)
                && Objects.equals(convertsize, f.convertsize)
                && Objects.equals(mimetype, f.mimetype)
                && Objects.equals(checked, f.checked);

    }

    @Override
    public int hashCode() {
        return Objects.hash(md5, indexed, timeindex, timestamp, timeclass, convertsw, classification, failed, failedreason, timeoutreason, noindexreason, filelocations, language, isbn, created, size, convertsize, mimetype, checked);
    }

}
