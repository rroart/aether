package roart.common.database;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import roart.common.model.IndexFiles;
import roart.common.model.Files;

public class DatabaseIndexFilesParam extends DatabaseParam {
    private Set<IndexFiles> indexFiles = new HashSet<>();

    private Set<Files> files = new HashSet<>();

    public DatabaseIndexFilesParam() {
        super();
    }

    public Set<IndexFiles> getIndexFiles() {
        return indexFiles;
    }

    public void setIndexFiles(Set<IndexFiles> indexFiles) {
        this.indexFiles = indexFiles;
    }

    public Set<Files> getFiles() {
        return files;
    }

    public void setFiles(Set<Files> files) {
        this.files = files;
    }
}
