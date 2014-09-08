package roart.jpa;

import java.util.List;

import roart.model.Files;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class FilesJpa {

    private Log log = LogFactory.getLog(this.getClass());

    public abstract Files getByFilename(String filename) throws Exception;

    public abstract List<Files> getByMd5(String md5) throws Exception;

    public abstract List<Files> getAll() throws Exception;

}

