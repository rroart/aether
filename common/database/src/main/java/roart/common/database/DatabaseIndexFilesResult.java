package roart.common.database;

import java.util.Map;

import roart.common.model.FilesDTO;
import roart.common.model.IndexFilesDTO;

public class DatabaseIndexFilesResult extends DatabaseResult {
    private FilesDTO[] files;

    private IndexFilesDTO[] indexFiles;

    private Map<String, IndexFilesDTO> indexFilesMap;
    
    public DatabaseIndexFilesResult() {
        super();
    }

    public IndexFilesDTO[] getIndexFiles() {
        return indexFiles;
    }

    public void setIndexFiles(IndexFilesDTO[] indexFiles) {
        this.indexFiles = indexFiles;
    }

    public FilesDTO[] getFiles() {
        return files;
    }

    public void setFiles(FilesDTO[] files) {
        this.files = files;
    }

     public Map<String, IndexFilesDTO> getIndexFilesMap() {
        return indexFilesMap;
    }

    public void setIndexFilesMap(Map<String, IndexFilesDTO> indexFilesMap) {
        this.indexFilesMap = indexFilesMap;
    }
}
