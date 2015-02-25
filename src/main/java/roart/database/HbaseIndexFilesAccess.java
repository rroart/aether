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

import roart.model.FileLocation;
import roart.model.IndexFiles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseIndexFilesAccess extends IndexFilesAccess {

    private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
    public IndexFiles getByMd5(String md5) throws Exception {
	return HbaseIndexFiles.get(md5);
    }

	@Override
    public Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
	return HbaseIndexFiles.getFilelocationsByMd5(md5);
    }

	@Override
    public IndexFiles getByFilelocation(FileLocation fl) throws Exception {
	return HbaseIndexFiles.getIndexByFilelocation(fl);
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
    public String getMd5ByFilelocation(FileLocation fl) throws Exception {
	return HbaseIndexFiles.getMd5ByFilelocation(fl);
    }

	@Override
    public List<IndexFiles> getAll() throws Exception {
	return HbaseIndexFiles.getAll();
    }

	@Override
    public void save(IndexFiles i) throws Exception {
	HbaseIndexFiles.put(i);
    }

	@Override
    public void flush() throws Exception {
	HbaseIndexFiles.flush();
    }

	@Override
    public void close() throws Exception {
	HbaseIndexFiles.close();
    }

	@Override
	public Set<String> getAllMd5() throws Exception {
		return HbaseIndexFiles.getAllMd5();
	}

	@Override
	public Set<String> getLanguages() throws Exception {
		return HbaseIndexFiles.getLanguages();
	}

}

