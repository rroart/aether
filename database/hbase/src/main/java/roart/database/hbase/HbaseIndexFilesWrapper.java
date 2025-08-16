package roart.database.hbase;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

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
import roart.common.model.FilesDTO;
import roart.common.model.IndexFilesDTO;
import roart.database.DatabaseOperations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseIndexFilesWrapper extends DatabaseOperations {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private HbaseIndexFiles hbaseIndexFiles;

    public HbaseIndexFilesWrapper(String configname, String configid, NodeConfig nodeConf) {
        hbaseIndexFiles = new HbaseIndexFiles(configname, nodeConf);
    }

    @Override
    public DatabaseIndexFilesResult getByMd5(DatabaseMd5Param param) throws Exception {
        Set<String> md5s = param.getMd5s();
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        Map<String, IndexFilesDTO> indexFilesMap = hbaseIndexFiles.get(md5s);
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
        IndexFilesDTO indexFilesGot = hbaseIndexFiles.getIndexByFilelocation(fl);
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        IndexFilesDTO[] indexFiles = new IndexFilesDTO[1];
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
        DatabaseMd5Result result = new DatabaseMd5Result();
        Map<FileLocation, String> indexFiles = hbaseIndexFiles.getMd5ByFilelocation(param.getFileLocations());
        indexFiles.entrySet().forEach(e -> md5Map.put(e.getKey().toString(), e.getValue())); 
        result.setMd5Map(md5Map);
        return result;
    }

    @Override
    public DatabaseIndexFilesResult getAll(DatabaseParam param) throws Exception {
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        result.setIndexFiles(hbaseIndexFiles.getAll().stream().toArray(IndexFilesDTO[]::new));
        return result;
    }

    @Override
    public DatabaseIndexFilesResult getAllFiles(DatabaseParam param) throws Exception {
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        result.setFiles(hbaseIndexFiles.getAllFiles().stream().toArray(FilesDTO[]::new));
        return result;
    }

    @Override
    public DatabaseResult save(DatabaseIndexFilesParam param) throws Exception {
        Set<IndexFilesDTO> is = param.getIndexFiles();
        hbaseIndexFiles.save(is);
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
        Set<IndexFilesDTO> indexes = param.getIndexFiles();
        for (IndexFilesDTO index : indexes) {
            hbaseIndexFiles.delete(index);
        }
        Set<FilesDTO> files = param.getFiles();
        for (FilesDTO index : files) {
            hbaseIndexFiles.delete(index);
        }
        return null;
    }

    @Override
    public DatabaseConstructorResult destroy() throws Exception {
        hbaseIndexFiles.destroy();
        return null;
    }

    @Override
    public DatabaseConstructorResult clear(DatabaseConstructorParam param) throws Exception {
        hbaseIndexFiles.clear(param);
        return new DatabaseConstructorResult();
    }

    @Override
    public DatabaseConstructorResult drop(DatabaseConstructorParam param) throws Exception {
        hbaseIndexFiles.drop(param);
        return new DatabaseConstructorResult();        
    }

}

