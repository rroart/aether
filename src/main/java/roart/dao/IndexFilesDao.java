package roart.dao;

import java.util.List;

import roart.model.IndexFiles;
import roart.model.FileLocation;

import roart.jpa.HibernateIndexFilesJpa;
import roart.jpa.HbaseIndexFilesJpa;
import roart.jpa.IndexFilesJpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IndexFilesDao {

    private Log log = LogFactory.getLog(this.getClass());

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
	return indexFilesJpa.getByMd5(md5);
    }

    public static IndexFiles getByFilename(String filename) throws Exception {
	String nodename = roart.util.Prop.getProp().getProperty("nodename");
	FileLocation fl = new FileLocation(nodename, filename);
	return indexFilesJpa.getByFilelocation(fl);
    }

    public static IndexFiles getByFilelocation(FileLocation fl) throws Exception {
	return indexFilesJpa.getByFilelocation(fl);
    }

    public static List<IndexFiles> getAll() throws Exception {
	return indexFilesJpa.getAll();
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
	indexFilesJpa.save(i);
    }

}
