package roart.database;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
//import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

import roart.config.NodeConfig;
import roart.model.FileLocation;
import roart.model.IndexFiles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseIndexFilesWrapper extends DatabaseOperations {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private HbaseIndexFiles hbaseIndexFiles;
    
	public HbaseIndexFilesWrapper(String nodename, NodeConfig nodeConf) {
        hbaseIndexFiles = new HbaseIndexFiles(nodename, nodeConf);
    }

    @Override
    public DatabaseIndexFilesResult getByMd5(DatabaseMd5Param param) throws Exception {
	    String md5 = param.md5;
	    DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
	    IndexFiles[] indexFiles = new IndexFiles[1];
	    indexFiles[0] = hbaseIndexFiles.get(md5);
	    result.indexFiles = indexFiles;
	    return result;
    }

	@Override
    public DatabaseFileLocationResult getFilelocationsByMd5(DatabaseMd5Param param) throws Exception {
        String md5 = param.md5;
        Set<FileLocation> fileLocationSet = hbaseIndexFiles.getFilelocationsByMd5(md5);
        DatabaseFileLocationResult result = new DatabaseFileLocationResult();
        FileLocation[] fileLocations = new FileLocation[1];
        result.fileLocation = (FileLocation[]) fileLocationSet.toArray();
        return result;
    }

	@Override
    public DatabaseIndexFilesResult getByFilelocation(DatabaseFileLocationParam param) throws Exception {
	    FileLocation fl = param.fileLocation;
	IndexFiles indexFilesGot = hbaseIndexFiles.getIndexByFilelocation(fl);
    DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
    IndexFiles[] indexFiles = new IndexFiles[1];
    indexFiles[0] = indexFilesGot;
    result.indexFiles = indexFiles;
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
	    FileLocation fl = param.fileLocation;
	    DatabaseMd5Result result = new DatabaseMd5Result();
        String[] md5 = new String[1];
        md5[0] = hbaseIndexFiles.getMd5ByFilelocation(fl);
        result.md5 = md5;
        return result;
    }

	@Override
    public DatabaseIndexFilesResult getAll(DatabaseParam param) throws Exception {
	       DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
	        result.indexFiles = (IndexFiles[]) hbaseIndexFiles.getAll().toArray();
	        return result;
    }

	@Override
    public DatabaseResult save(DatabaseIndexFilesParam param) throws Exception {
	    IndexFiles i = param.indexFiles;
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
        result.md5 = (String[]) hbaseIndexFiles.getAllMd5().toArray();
        return result;
	}

	@Override
	public DatabaseLanguagesResult getLanguages(DatabaseParam param) throws Exception {
	       DatabaseLanguagesResult result = new DatabaseLanguagesResult();
	       	result.languages = (String[]) hbaseIndexFiles.getLanguages().toArray();
	       	return result;
	}

    @Override
    public DatabaseResult delete(DatabaseIndexFilesParam param) throws Exception {
        IndexFiles index = param.indexFiles;
        hbaseIndexFiles.delete(index);
        return null;
    }

    @Override
    public DatabaseConstructorResult destroy() throws Exception {
        hbaseIndexFiles.destroy();
        return null;
    }

}

