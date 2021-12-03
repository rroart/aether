package roart.common.database;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import roart.common.model.IndexFiles;

public class DatabaseIndexFilesParam extends DatabaseParam {
    private Set<IndexFiles> indexFiles;

    public DatabaseIndexFilesParam() {
        super();
    }

    public Set<IndexFiles> getIndexFiles() {
        return indexFiles;
    }

    public void setIndexFiles(Set<IndexFiles> indexFiles) {
        this.indexFiles = indexFiles;
    }
}
