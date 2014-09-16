package roart.jpa;

import java.util.List;

import roart.model.Files;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HibernateFilesJpa extends FilesJpa {

    private Log log = LogFactory.getLog(this.getClass());

    public Files getByFilename(String filename) throws Exception {
	return null; //Files.getByFilename(filename);
    }

    public List<Files> getByMd5(String md5) throws Exception {
	return null; // Files.getByMd5(md5);
    }

    public List<Files> getAll() throws Exception {
	return null; // Files.getAll();
    }

}
