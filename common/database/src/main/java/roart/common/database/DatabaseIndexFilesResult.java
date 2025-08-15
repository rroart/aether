package roart.common.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import roart.common.mapper.Mapper;
import roart.common.model.FilesDTO;
import roart.common.model.IndexFiles;
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

    public Map<String, IndexFilesDTO> getIndexFilesDTOMap() {
        return indexFilesMap;
   }

     public Map<String, IndexFiles> getIndexFilesMap() {
         Map<String, IndexFiles> simpleMap = new HashMap<>();
         for (Entry<String, IndexFilesDTO> entry : indexFilesMap.entrySet()) {
             String key = entry.getKey();
             simpleMap.put(key, Mapper.map(entry.getValue()));
         }
         return simpleMap;
    }

    public void setIndexFilesMap(Map<String, IndexFilesDTO> indexFilesMap) {
        this.indexFilesMap = indexFilesMap;
    }
}
