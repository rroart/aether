package roart.common.database;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import roart.common.model.IndexFilesDTO;
import roart.common.model.FilesDTO;

public class DatabaseIndexFilesParam extends DatabaseParam {
    private Set<IndexFilesDTO> indexFiles = new HashSet<>();

    private Set<FilesDTO> files = new HashSet<>();

    public DatabaseIndexFilesParam() {
        super();
    }

    public Set<IndexFilesDTO> getIndexFiles() {
        return indexFiles;
    }

    public void setIndexFiles(Set<IndexFilesDTO> indexFiles) {
        this.indexFiles = indexFiles;
    }

    public Set<FilesDTO> getFiles() {
        return files;
    }

    public void setFiles(Set<FilesDTO> files) {
        this.files = files;
    }
}
