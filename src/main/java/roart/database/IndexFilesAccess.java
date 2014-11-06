package roart.database;

import java.util.List;
import java.util.Set;

import roart.model.IndexFiles;
import roart.model.FileLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class IndexFilesAccess {

    private Logger log = LoggerFactory.getLogger(this.getClass());

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

