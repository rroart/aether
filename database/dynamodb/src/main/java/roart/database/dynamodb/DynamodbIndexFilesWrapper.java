package roart.database.dynamodb;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import java.io.IOException;

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
import roart.common.model.Files;
import roart.common.model.IndexFiles;
import roart.database.DatabaseOperations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamodbIndexFilesWrapper extends DatabaseOperations {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private DynamodbIndexFiles dynamodbIndexFiles;

    public DynamodbIndexFilesWrapper(String nodename, NodeConfig nodeConf) {
        dynamodbIndexFiles = new DynamodbIndexFiles(null, nodename, nodeConf);
    }

    @Override
    public DatabaseIndexFilesResult getByMd5(DatabaseMd5Param param) throws Exception {
        Set<String> md5s = param.getMd5s();
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        Map<String, IndexFiles> indexFilesMap = dynamodbIndexFiles.get(md5s);
        result.setIndexFilesMap(indexFilesMap);
        return result;
    }

    @Override
    public DatabaseFileLocationResult getFilelocationsByMd5(DatabaseMd5Param param) throws Exception {
        String md5 = param.getMd5();
        Set<FileLocation> fileLocationSet = dynamodbIndexFiles.getFilelocationsByMd5(md5);
        DatabaseFileLocationResult result = new DatabaseFileLocationResult();
        FileLocation[] fileLocations = new FileLocation[1];
        result.fileLocation = fileLocationSet.stream().toArray(FileLocation[]::new);
        return result;
    }

    @Override
    public DatabaseIndexFilesResult getByFilelocation(DatabaseFileLocationParam param) throws Exception {
        FileLocation fl = param.getFileLocation();
        IndexFiles indexFilesGot = dynamodbIndexFiles.getIndexByFilelocation(fl);
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
            String md5 = dynamodbIndexFiles.getMd5ByFilelocation(fl);
            md5Map.put(filename, md5);
        }
        DatabaseMd5Result result = new DatabaseMd5Result();
        result.setMd5Map(md5Map);
        return result;
    }

    @Override
    public DatabaseIndexFilesResult getAll(DatabaseParam param) throws Exception {
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        result.setIndexFiles(dynamodbIndexFiles.getAll().stream().toArray(IndexFiles[]::new));
        return result;
    }

    @Override
    public DatabaseIndexFilesResult getAllFiles(DatabaseParam param) throws Exception {
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        result.setFiles(dynamodbIndexFiles.getAllFiles().stream().toArray(Files[]::new));
        return result;
    }

    @Override
    public DatabaseResult save(DatabaseIndexFilesParam param) throws Exception {
        Set<IndexFiles> is = param.getIndexFiles();
        for (IndexFiles i : is) {
            dynamodbIndexFiles.put(i);
        }
        return null;
    }

    @Override
    public DatabaseResult flush(DatabaseParam param) throws Exception {
        dynamodbIndexFiles.flush();
        return null;
    }

    @Override
    public DatabaseResult commit(DatabaseParam param) throws Exception {
        dynamodbIndexFiles.commit();
        return null;
    }

    @Override
    public DatabaseResult close(DatabaseParam param) throws Exception {
        dynamodbIndexFiles.close();
        return null;
    }

    @Override
    public DatabaseMd5Result getAllMd5(DatabaseParam param) throws Exception {
        DatabaseMd5Result result = new DatabaseMd5Result();
        result.setMd5(dynamodbIndexFiles.getAllMd5().stream().toArray(String[]::new));
        return result;
    }

    @Override
    public DatabaseLanguagesResult getLanguages(DatabaseParam param) throws Exception {
        DatabaseLanguagesResult result = new DatabaseLanguagesResult();
        result.languages = dynamodbIndexFiles.getLanguages().stream().toArray(String[]::new);
        return result;
    }

    @Override
    public DatabaseResult delete(DatabaseIndexFilesParam param) throws Exception {
        Set<IndexFiles> indexes = param.getIndexFiles();
        for (IndexFiles index : indexes) {
            dynamodbIndexFiles.delete(index);
        }
        Set<Files> files = param.getFiles();
        for (Files index : files) {
            dynamodbIndexFiles.delete(index);
        }
        return null;
    }

    @Override
    public DatabaseConstructorResult destroy() throws Exception {
        dynamodbIndexFiles.destroy();
        return null;
    }

    @Override
    public DatabaseConstructorResult clear(DatabaseConstructorParam param) throws Exception {
        dynamodbIndexFiles.clear(param);
        return new DatabaseConstructorResult();
    }

    @Override
    public DatabaseConstructorResult drop(DatabaseConstructorParam param) throws Exception {
        dynamodbIndexFiles.drop(param);
        return new DatabaseConstructorResult();        
    }
}

