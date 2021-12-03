package roart.database.cassandra;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.NodeConfig;
import roart.common.database.DatabaseConstructorParam;
import roart.common.database.DatabaseConstructorResult;
import roart.common.database.DatabaseFileLocationParam;
import roart.common.database.DatabaseFileLocationResult;
import roart.common.database.DatabaseIndexFilesParam;
import roart.common.database.DatabaseIndexFilesResult;
import roart.common.database.DatabaseLanguagesResult;
import roart.common.database.DatabaseMd5Param;
import roart.common.database.DatabaseMd5Result;
import roart.common.database.DatabaseParam;
import roart.common.database.DatabaseResult;
import roart.common.model.FileLocation;
import roart.common.model.IndexFiles;
import roart.database.DatabaseOperations;

public class CassandraIndexFilesWrapper extends DatabaseOperations {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private CassandraIndexFiles cassandraIndexFiles;

    public CassandraIndexFilesWrapper(String nodename, NodeConfig nodeConf) {
        cassandraIndexFiles = new CassandraIndexFiles(null, nodename, nodeConf);
    }

    @Override
    public DatabaseIndexFilesResult getByMd5(DatabaseMd5Param param) throws Exception {
        Set<String> md5s = param.getMd5s();
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        Map<String, IndexFiles> indexFilesMap = cassandraIndexFiles.get(md5s);
        result.setIndexFilesMap(indexFilesMap);
        return result;
    }

    @Override
    public DatabaseFileLocationResult getFilelocationsByMd5(DatabaseMd5Param param) throws Exception {
        String md5 = param.getMd5();
        Set<FileLocation> fileLocationSet = cassandraIndexFiles.getFilelocationsByMd5(md5);
        DatabaseFileLocationResult result = new DatabaseFileLocationResult();
        FileLocation[] fileLocations = new FileLocation[1];
        result.fileLocation = fileLocationSet.stream().toArray(FileLocation[]::new);
        return result;
    }

    @Override
    public DatabaseIndexFilesResult getByFilelocation(DatabaseFileLocationParam param) throws Exception {
        FileLocation fl = param.getFileLocation();
        IndexFiles indexFilesGot = cassandraIndexFiles.getIndexByFilelocation(fl);
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        IndexFiles[] indexFiles = new IndexFiles[1];
        indexFiles[0] = indexFilesGot;
        result.setIndexFiles(indexFiles);
        return result;
        /*
	IndexFiles ifile = new IndexFiles();
	IndexFiles files = IndexFilesDao.getByFilename(filename);
	ResultScanner scanner = table.getScanner(new Scan());
	for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
	    byte[] key = rr.getRow();
	}
	return files;
         */
    }

    @Override
    public DatabaseMd5Result getMd5ByFilelocation(DatabaseFileLocationParam param) throws Exception {
        Map<String, String> md5Map = new HashMap<>();
        for (FileLocation fl : param.getFileLocations()) {
            String filename = fl.getFilename();
            String md5 = cassandraIndexFiles.getMd5ByFilelocation(fl);
            md5Map.put(filename, md5);
        }
        DatabaseMd5Result result = new DatabaseMd5Result();
        result.setMd5Map(md5Map);
        return result;
    }

    @Override
    public DatabaseIndexFilesResult getAll(DatabaseParam param) throws Exception {
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        result.setIndexFiles(cassandraIndexFiles.getAll().stream().toArray(IndexFiles[]::new));
        return result;
    }

    @Override
    public DatabaseResult save(DatabaseIndexFilesParam param) throws Exception {
        Set<IndexFiles> is = param.getIndexFiles();
        for (IndexFiles i : is) {
            cassandraIndexFiles.put(i);
        }
        return null;
    }

    @Override
    public DatabaseResult flush(DatabaseParam param) throws Exception {
        cassandraIndexFiles.flush();
        return null;
    }

    @Override
    public DatabaseResult commit(DatabaseParam param) throws Exception {
        cassandraIndexFiles.commit();
        return null;
    }

    @Override
    public DatabaseResult close(DatabaseParam param) throws Exception {
        cassandraIndexFiles.close();
        return null;
    }

    @Override
    public DatabaseMd5Result getAllMd5(DatabaseParam param) throws Exception {
        DatabaseMd5Result result = new DatabaseMd5Result();
        result.setMd5(cassandraIndexFiles.getAllMd5().stream().toArray(String[]::new));
        return result;
    }

    @Override
    public DatabaseLanguagesResult getLanguages(DatabaseParam param) throws Exception {
        DatabaseLanguagesResult result = new DatabaseLanguagesResult();
        result.languages = cassandraIndexFiles.getLanguages().stream().toArray(String[]::new);
        return result;
    }

    @Override
    public DatabaseResult delete(DatabaseIndexFilesParam param) throws Exception {
        Set<IndexFiles> indexes = param.getIndexFiles();
        for (IndexFiles index : indexes) {
            cassandraIndexFiles.delete(index);
        }
        return null;
    }

    @Override
    public DatabaseConstructorResult destroy() throws Exception {
        cassandraIndexFiles.destroy();
        return null;
    }

    @Override
    public DatabaseConstructorResult clear(DatabaseConstructorParam param) throws Exception {
        cassandraIndexFiles.clear(param);
        return new DatabaseConstructorResult();
    }

    @Override
    public DatabaseConstructorResult drop(DatabaseConstructorParam param) throws Exception {
        cassandraIndexFiles.drop(param);
        return new DatabaseConstructorResult();        
    }
}

