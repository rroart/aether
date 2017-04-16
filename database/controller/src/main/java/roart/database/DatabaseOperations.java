package roart.database;

public abstract class DatabaseOperations {
    
    public abstract DatabaseConstructorResult destroy() throws Exception;

    public abstract DatabaseIndexFilesResult getByMd5(DatabaseMd5Param param) throws Exception;
    
    public abstract DatabaseFileLocationResult getFilelocationsByMd5(DatabaseMd5Param param) throws Exception;;
    
    public abstract DatabaseIndexFilesResult getByFilelocation(DatabaseFileLocationParam param) throws Exception;;
    
    public abstract DatabaseMd5Result getMd5ByFilelocation(DatabaseFileLocationParam param) throws Exception;;
    
    public abstract DatabaseIndexFilesResult getAll(DatabaseParam param) throws Exception;
    
    public abstract DatabaseResult save(DatabaseIndexFilesParam param) throws Exception;
    
    public abstract DatabaseResult flush(DatabaseParam param) throws Exception;
    
    public abstract DatabaseResult commit(DatabaseParam param) throws Exception;
    
    public abstract DatabaseResult close(DatabaseParam param) throws Exception;
    
    public abstract DatabaseMd5Result getAllMd5(DatabaseParam param) throws Exception;
    
    public abstract DatabaseLanguagesResult getLanguages(DatabaseParam param) throws Exception;
    
    public abstract DatabaseResult delete(DatabaseIndexFilesParam param) throws Exception;

}
