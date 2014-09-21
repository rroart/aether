package roart.dao;

import java.util.List;
import java.util.TreeMap;
import java.util.Map;

import roart.model.IndexFiles;
import roart.model.FileLocation;

import roart.jpa.HibernateIndexFilesJpa;
import roart.jpa.HbaseIndexFilesJpa;
import roart.jpa.IndexFilesJpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IndexFilesDao {

    private static Log log = LogFactory.getLog("IndexFilesDao");

    private static Map<String, IndexFiles> all = new TreeMap<String, IndexFiles>();

    private static IndexFilesJpa indexFilesJpa = null;

    public static void instance(String type) {
	if (indexFilesJpa == null) {
	    if (type.equals("hibernate")) {
		indexFilesJpa = new HibernateIndexFilesJpa();
	    }
	    if (type.equals("hbase")) {
		indexFilesJpa = new HbaseIndexFilesJpa();
	    }
	}
    }

    public static IndexFiles getByMd5(String md5) throws Exception {
	if (all.containsKey(md5)) {
	    return all.get(md5);
	}
	IndexFiles i = indexFilesJpa.getByMd5(md5);
	if (i == null) {
	    i = new IndexFiles(md5);
	}
	all.put(md5, i);
	return i;
    }

    public static IndexFiles getByFilenameNot(String filename) throws Exception {
	String nodename = roart.util.Prop.getProp().getProperty("nodename");
	FileLocation fl = new FileLocation(nodename, filename);
	return indexFilesJpa.getByFilelocation(fl);
    }

    public static IndexFiles getByFilelocationNot(FileLocation fl) throws Exception {
	return indexFilesJpa.getByFilelocation(fl);
    }

    public static String getMd5ByFilename(String filename) throws Exception {
	String nodename = roart.util.Prop.getProp().getProperty("nodename");
	FileLocation fl = new FileLocation(nodename, filename);
	return indexFilesJpa.getMd5ByFilelocation(fl);
    }

    public static List<IndexFiles> getAll() throws Exception {
	all.clear();
	List<IndexFiles> iAll = indexFilesJpa.getAll();
	for (IndexFiles i : iAll) {
	    all.put(i.getMd5(), i);
	}
	return iAll;
    }

    /*
    public static IndexFiles ensureExistence(String md5) throws Exception {
	IndexFiles fi = getByMd5(md5);
	if (fi == null) {
	    indexFilesJpa.ensureExistence(md5);
	}
	return fi;
    }
    */

    public static IndexFiles ensureExistenceNot(FileLocation filename) throws Exception {
	/*
	IndexFiles fi = getByMd5(md5);
	if (fi == null) {
	    indexFilesJpa.ensureExistence(md5);
	}
	*/
	return null;
    }

    public static void save(IndexFiles i) {
	if (i.hasChanged()) {
	    log.info("saving " + i.getMd5());
	    indexFilesJpa.save(i);
	} else {
	    //log.info("not saving " + i.getMd5());
	}
    }

    public static IndexFiles instanceNot(String md5) {
	IndexFiles i = all.get(md5);
	if (i == null) {
	    i = new IndexFiles(md5);
	    all.put(md5, i);
	}
	return i;
    }

    public static void commit() {
	close();
    }

    public static void close() {
	for (String k : all.keySet()) {
	    IndexFiles i = all.get(k);
	    IndexFilesDao.save(i);
	}
	all.clear();
	indexFilesJpa.close();
    }

    public static void flush() {
	indexFilesJpa.flush();
    }

}
