package roart.database.dynamodb;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import java.io.IOException;

import roart.common.config.NodeConfig;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamodbIndexFilesWrapper extends DatabaseOperations {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private DynamodbIndexFiles hbaseIndexFiles;

    public DynamodbIndexFilesWrapper(String nodename, NodeConfig nodeConf) {
        hbaseIndexFiles = new DynamodbIndexFiles(null, nodename, nodeConf);
    }

    @Override
    public DatabaseIndexFilesResult getByMd5(DatabaseMd5Param param) throws Exception {
        Set<String> md5s = param.getMd5s();
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        Map<String, IndexFiles> indexFilesMap = hbaseIndexFiles.get(md5s);
        result.setIndexFilesMap(indexFilesMap);
        return result;
    }

    @Override
    public DatabaseFileLocationResult getFilelocationsByMd5(DatabaseMd5Param param) throws Exception {
        String md5 = param.getMd5();
        Set<FileLocation> fileLocationSet = hbaseIndexFiles.getFilelocationsByMd5(md5);
        DatabaseFileLocationResult result = new DatabaseFileLocationResult();
        FileLocation[] fileLocations = new FileLocation[1];
        result.fileLocation = fileLocationSet.stream().toArray(FileLocation[]::new);
        return result;
    }

    @Override
    public DatabaseIndexFilesResult getByFilelocation(DatabaseFileLocationParam param) throws Exception {
        FileLocation fl = param.getFileLocation();
        IndexFiles indexFilesGot = hbaseIndexFiles.getIndexByFilelocation(fl);
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
        FileLocation fl = param.getFileLocation();
        DatabaseMd5Result result = new DatabaseMd5Result();
        String[] md5 = new String[1];
        md5[0] = hbaseIndexFiles.getMd5ByFilelocation(fl);
        result.setMd5(md5);
        return result;
    }

    @Override
    public DatabaseIndexFilesResult getAll(DatabaseParam param) throws Exception {
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        result.setIndexFiles(hbaseIndexFiles.getAll().stream().toArray(IndexFiles[]::new));
        return result;
    }

    @Override
    public DatabaseResult save(DatabaseIndexFilesParam param) throws Exception {
        IndexFiles i = param.getIndexFiles();
        hbaseIndexFiles.put(i);
        return null;
    }

    @Override
    public DatabaseResult flush(DatabaseParam param) throws Exception {
        hbaseIndexFiles.flush();
        return null;
    }

    @Override
    public DatabaseResult commit(DatabaseParam param) throws Exception {
        hbaseIndexFiles.commit();
        return null;
    }

    @Override
    public DatabaseResult close(DatabaseParam param) throws Exception {
        hbaseIndexFiles.close();
        return null;
    }

    @Override
    public DatabaseMd5Result getAllMd5(DatabaseParam param) throws Exception {
        DatabaseMd5Result result = new DatabaseMd5Result();
        result.setMd5(hbaseIndexFiles.getAllMd5().stream().toArray(String[]::new));
        return result;
    }

    @Override
    public DatabaseLanguagesResult getLanguages(DatabaseParam param) throws Exception {
        DatabaseLanguagesResult result = new DatabaseLanguagesResult();
        result.languages = hbaseIndexFiles.getLanguages().stream().toArray(String[]::new);
        return result;
    }

    @Override
    public DatabaseResult delete(DatabaseIndexFilesParam param) throws Exception {
        IndexFiles index = param.getIndexFiles();
        hbaseIndexFiles.delete(index);
        return null;
    }

    @Override
    public DatabaseConstructorResult destroy() throws Exception {
        hbaseIndexFiles.destroy();
        return null;
    }

}

