package roart.jpa;

import java.util.List;
import java.util.Set;

import roart.model.IndexFiles;
import roart.model.FileLocation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class IndexFilesJpa {

    private Log log = LogFactory.getLog(this.getClass());

    public abstract IndexFiles getByFilelocation(FileLocation fl) throws Exception;

    public abstract String getMd5ByFilelocation(FileLocation fl) throws Exception;

    public abstract IndexFiles getByMd5(String md5) throws Exception;

    public abstract Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception;

    public abstract List<IndexFiles> getAll() throws Exception;

    public abstract void save(IndexFiles i);

    public abstract void flush();

    public abstract void close();

    //public abstract IndexFiles ensureExistence(String md5) throws Exception;

}

