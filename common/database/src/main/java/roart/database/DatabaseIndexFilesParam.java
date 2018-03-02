package roart.database;

import roart.model.IndexFiles;

public class DatabaseIndexFilesParam extends DatabaseParam {
    private IndexFiles indexFiles;

    public DatabaseIndexFilesParam() {
        super();
    }

    public IndexFiles getIndexFiles() {
        return indexFiles;
    }

    public void setIndexFiles(IndexFiles indexFiles) {
        this.indexFiles = indexFiles;
    }
        
}
