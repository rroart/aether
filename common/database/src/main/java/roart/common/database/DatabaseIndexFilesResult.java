package roart.common.database;

import java.util.Map;

import roart.common.model.Files;
import roart.common.model.IndexFiles;

public class DatabaseIndexFilesResult extends DatabaseResult {
    private Files[] files;

    private IndexFiles[] indexFiles;

    private Map<String, IndexFiles> indexFilesMap;
    
    public DatabaseIndexFilesResult() {
        super();
    }

    public IndexFiles[] getIndexFiles() {
        return indexFiles;
    }

    public void setIndexFiles(IndexFiles[] indexFiles) {
        this.indexFiles = indexFiles;
    }

    public Files[] getFiles() {
        return files;
    }

    public void setFiles(Files[] files) {
        this.files = files;
    }

     public Map<String, IndexFiles> getIndexFilesMap() {
        return indexFilesMap;
    }

    public void setIndexFilesMap(Map<String, IndexFiles> indexFilesMap) {
        this.indexFilesMap = indexFilesMap;
    }
}
