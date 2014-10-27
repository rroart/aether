package roart.jpa;

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
import roart.model.HbaseIndexFiles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseIndexFilesJpa extends IndexFilesJpa {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public IndexFiles getByMd5(String md5) throws Exception {
	return HbaseIndexFiles.get(md5);
    }

    public Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
	return HbaseIndexFiles.getFilelocationsByMd5(md5);
    }

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

    public String getMd5ByFilelocation(FileLocation fl) throws Exception {
	return HbaseIndexFiles.getMd5ByFilelocation(fl);
    }

    public List<IndexFiles> getAll() throws Exception {
	return HbaseIndexFiles.getAll();
    }

    public void save(IndexFiles i) {
	HbaseIndexFiles.put(i);
    }

    public void flush() {
	HbaseIndexFiles.flush();
    }

    public void close() {
	HbaseIndexFiles.close();
    }

}

