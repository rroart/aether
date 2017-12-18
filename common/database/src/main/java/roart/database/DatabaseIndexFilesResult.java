package roart.database;

import roart.model.IndexFiles;

public class DatabaseIndexFilesResult extends DatabaseResult {
    public IndexFiles[] indexFiles;

    public IndexFiles[] getIndexFiles() {
        return indexFiles;
    }

    public void setIndexFiles(IndexFiles[] indexFiles) {
        this.indexFiles = indexFiles;
    }
}
