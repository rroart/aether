package roart.database;

import javax.jdo.Query;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.JDOObjectNotFoundException;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.store.query.QueryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.model.FileLocation;
import roart.model.IndexFiles;
import roart.util.Constants;

@PersistenceCapable(table="Files")
    public class DataNucleusFiles /*implements Serializable*/ {
	/**
	 * @author roart
	 *
	 */
	
	private static Logger log = LoggerFactory.getLogger(DataNucleusFiles.class);

	@Column(name = "filelocation")
	@Persistent	
	@PrimaryKey
	private String filelocation;
        public String getFilelocation() {
	    return filelocation;
	}

	public void setFilelocation(String filelocation) {
	    this.filelocation = filelocation;
	}

    @Column(name = "md5")
    @Persistent 
    private String md5;
        public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public static DataNucleusFiles ensureExistence(FileLocation fn) throws Exception {
        DataNucleusFiles fi = getByFilelocation(fn);
        if (fi == null) {
        fi = new DataNucleusFiles();
        fi.setFilelocation(fn.toString());
        DataNucleusUtil.currentSession().save(fi);
        }
        return fi;
    }

    public static DataNucleusFiles getByFilelocation(FileLocation filelocation) throws Exception {
	try {
	return (DataNucleusFiles) DataNucleusUtil.currentSession().getPm2().getObjectById(DataNucleusFiles.class, filelocation.toString());
	    } catch (JDOObjectNotFoundException e) {
		return null;
	    } catch (NucleusObjectNotFoundException e) {
		return null;
	    } catch (Exception e) {
		log.error(Constants.EXCEPTION, e);
	    }
	    return null;
    }

    public static String getMd5ByFilelocation(FileLocation fl) {
        DataNucleusFiles fi = null;
        try {
            fi = getByFilelocation(fl);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        String md5 = null;
        if (fi != null) {
            md5 = fi.getMd5();
        }
        return md5;
    }
    
    public static List<DataNucleusFiles> getByMd5(String md5) throws Exception {
        List<DataNucleusFiles> dnifs = null;
        Query query = DataNucleusUtil.currentSession().getPm2().newQuery(DataNucleusFiles.class);
        query.setFilter("md5 == mymd5");
            query.declareParameters(String.class.getName() + " mymd5");
            dnifs = (List<DataNucleusFiles>) query.execute(md5);
            if (dnifs == null || dnifs.size() == 0) {
                return new ArrayList<DataNucleusFiles>();
            }
            return dnifs;
     }

    public static void delete(IndexFiles index) throws Exception {
        Set<FileLocation> fls = index.getFilelocations();
        for (FileLocation fl : fls) {
            DataNucleusFiles dnif = getByFilelocation(fl);
            DataNucleusUtil.currentSession().getPm2().deletePersistent(dnif);
        }
    }
    
    public void delete() throws Exception {
        DataNucleusUtil.currentSession().getPm2().deletePersistent(this);
    }

    }
