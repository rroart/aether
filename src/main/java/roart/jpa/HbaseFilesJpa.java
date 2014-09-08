package roart.jpa;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import roart.model.Files;

public class HbaseFilesJpa extends FilesJpa {

    private Log log = LogFactory.getLog(this.getClass());

    public List<Files> getAll() throws Exception {
	return null;
    }

    public List<Files> getByMd5(String md5) throws Exception {
	return null;
    }

    public Files getByFilename(String filename) throws Exception {
	return null;
    }

}

