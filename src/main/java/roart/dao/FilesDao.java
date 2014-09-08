package roart.dao;

import java.util.List;

import roart.model.Files;

import roart.jpa.FilesJpa;
import roart.jpa.HibernateFilesJpa;
import roart.jpa.HbaseFilesJpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FilesDao {

    private Log log = LogFactory.getLog(this.getClass());

    private static FilesJpa filesJpa = null;

    public static void instance(String type) {
	if (filesJpa == null) {
	    if (type.equals("hibernate")) {
		filesJpa = new HibernateFilesJpa();
	    }
	    if (type.equals("hbase")) {
		filesJpa = new HbaseFilesJpa();
	    }
	}
    }

    public static Files getByFilename(String filename) throws Exception {
	return filesJpa.getByFilename(filename);
    }

    public static List<Files> getByMd5(String md5) throws Exception {
	return filesJpa.getByMd5(md5);
    }

    public static List<Files> getAll() throws Exception {
	return filesJpa.getAll();
    }

}
