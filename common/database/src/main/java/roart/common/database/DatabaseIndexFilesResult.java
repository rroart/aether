package roart.common.database;

import java.util.Map;

import roart.common.model.IndexFiles;

public class DatabaseIndexFilesResult extends DatabaseResult {
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

    public Map<String, IndexFiles> getIndexFilesMap() {
        return indexFilesMap;
    }

    public void setIndexFilesMap(Map<String, IndexFiles> indexFilesMap) {
        this.indexFilesMap = indexFilesMap;
    }
}
