package roart.model;

import java.util.List;
import java.util.Set;

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
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

import roart.model.IndexFiles;
import roart.model.FileLocation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HbaseIndexFiles {

    private static Log log = LogFactory.getLog("HbaseIndexFiles");

    // column families
    private static byte[] indexcf = Bytes.toBytes("if");
    private static byte[] flcf = Bytes.toBytes("fl");
    private static byte[] filescf = Bytes.toBytes("fi");

    // column qualifiers
    private static byte[] md5q = Bytes.toBytes("md5");
    private static byte[] indexedq = Bytes.toBytes("indexed");
    private static byte[] timestampq = Bytes.toBytes("timestamp");
    private static byte[] convertswq = Bytes.toBytes("convertsw");
    private static byte[] failedq = Bytes.toBytes("failed");
    private static byte[] failedreasonq = Bytes.toBytes("failedreason");
    private static byte[] timeoutreasonq = Bytes.toBytes("timeoutreason");
    private static byte[] nodeq = Bytes.toBytes("node");
    private static byte[] filenameq = Bytes.toBytes("filename");
    private static byte[] filelocationq = Bytes.toBytes("filelocation");

    public HbaseIndexFiles() {
	try {
	Configuration conf = HBaseConfiguration.create();
	conf.set("hbase.zookeeper.quorum","localhost");
	conf.set("hbase.zookeeper.property.clientPort","2181");
	conf.set("hbase.master","localhost:2181");

	HBaseAdmin admin = new HBaseAdmin(conf);
	HTableDescriptor tableDesc = new HTableDescriptor("index");
	if (admin.tableExists(tableDesc.getName())) {
	    //admin.disableTable(table.getName());
	    //admin.deleteTable(table.getName());
	} else {
	    admin.createTable(tableDesc);
	}
	HTableDescriptor filesTableDesc = new HTableDescriptor("files");
	if (admin.tableExists(filesTableDesc.getName())) {
	    //admin.disableTable(table.getName());
	    //admin.deleteTable(table.getName());
	} else {
	    admin.createTable(filesTableDesc);
	}
	//HTable table = new HTable(conf, "index");
	} catch (IOException e) {
	    log.error("Exception", e);
	}
    }

    public static void put(IndexFiles ifile) {
	try {
	    HTablePool pool = new HTablePool();
	    HTableInterface indexTable = pool.getTable("index");
	    //HTable /*Interface*/ filesTable = new HTable(conf, "index");
	    Put put = new Put(Bytes.toBytes(ifile.getMd5()));
	    put.add(indexcf, md5q, Bytes.toBytes(ifile.getMd5()));
	    put.add(indexcf, indexedq, Bytes.toBytes(ifile.getIndexed()));
	    put.add(indexcf, timestampq, Bytes.toBytes(ifile.getTimestamp()));
	    put.add(indexcf, convertswq, Bytes.toBytes(ifile.getConvertsw()));
	    put.add(indexcf, failedq, Bytes.toBytes(ifile.getFailed()));
	    put.add(indexcf, failedreasonq, Bytes.toBytes(ifile.getFailedreason()));
	    put.add(indexcf, timeoutreasonq, Bytes.toBytes(ifile.getTimeoutreason()));
	    int i = -1;
	    for (FileLocation file : ifile.getFilelocations()) {
		i++;
		String filename = getFile(file);
		put.add(flcf, Bytes.toBytes("q" + i), Bytes.toBytes(filename));
	    }
	    put(ifile.getMd5(), ifile.getFilelocations());
	    indexTable.put(put);
	} catch (IOException e) {
	    log.error("Exception", e);
	}
    }

    public static void put(String md5, Set<FileLocation> files) {
	try {
	    HTablePool pool = new HTablePool();
	    HTableInterface filesTable = pool.getTable("files");
	    //HTable /*Interface*/ filesTable = new HTable(conf, "index");
	    for (FileLocation file : files) {
		String filename = getFile(file);
		Put put = new Put(Bytes.toBytes(filename));
		put.add(filescf, md5q, Bytes.toBytes(md5));
		filesTable.put(put);
	    }
	} catch (IOException e) {
	    log.error("Exception", e);
	}
    }

    public static IndexFiles get(Result index) {
	IndexFiles ifile = new IndexFiles();
	ifile.setMd5(new String(index.getValue(indexcf, md5q)));
	ifile.setIndexed(new Boolean(new String(index.getValue(indexcf, indexedq))));
	ifile.setTimestamp(new String(index.getValue(indexcf, timestampq)));
	ifile.setConvertsw(new String(index.getValue(indexcf, convertswq)));
	ifile.setFailed(new Integer(new String(index.getValue(indexcf, failedq))));
	ifile.setFailedreason(new String(index.getValue(indexcf, failedreasonq)));
	ifile.setTimeoutreason(new String(index.getValue(indexcf, timeoutreasonq)));
	List<KeyValue> list = index.list();
	for (KeyValue kv : list) {
	    byte[] family = kv.getFamily();
	    String fam = new String(family);
	    if (fam.equals("flcf")) {
		String loc = Bytes.toString(kv.getValue());
		FileLocation fl = getFileLocation(loc);
		ifile.addFile(fl);
	    }
	}
	return ifile;
    }

    public static IndexFiles get(String md5) {
	try {
	    HTablePool pool = new HTablePool();
	    HTableInterface indexTable = pool.getTable("index");
	    //HTable /*Interface*/ filesTable = new HTable(conf, "index");
	    Get get = new Get(Bytes.toBytes(md5));
	    get.addFamily(indexcf);
	    get.addFamily(flcf);
	    Result index = indexTable.get(get);
	    IndexFiles ifile = get(index);
	    return ifile;
	} catch (IOException e) {
	    log.error("Exception", e);
	}
	return null;
    }

    public static IndexFiles getIndexByFilelocation(FileLocation fl) {
	String md5 = getMd5ByFilelocation(fl);
	return get(md5);
    }

    public static String getMd5ByFilelocation(FileLocation fl) {
	String name = getFile(fl);
	try {
	    HTablePool pool = new HTablePool();
	    HTableInterface indexTable = pool.getTable("files");
	    //HTable /*Interface*/ filesTable = new HTable(conf, "index");
	    Get get = new Get(Bytes.toBytes(name));
	    get.addColumn(filescf, md5q);
	    Result result = indexTable.get(get);
	    return new String(result.getValue(indexcf, md5q));
	} catch (IOException e) {
	    log.error("Exception", e);
	}
	return null;
    }

    public static IndexFiles ensureExistence(String md5) throws Exception {
	try {
	    HTablePool pool = new HTablePool();
	    HTableInterface indexTable = pool.getTable("index");
	    //HTable /*Interface*/ filesTable = new HTable(conf, "index");
	    Put put = new Put(Bytes.toBytes(md5));
	    indexTable.put(put);
	} catch (IOException e) {
	    log.error("Exception", e);
	}
	IndexFiles i = new IndexFiles();
	i.setMd5(md5);
	return i;
    }

    public static String getFile(FileLocation fl) {
	String node = fl.getNode();
	String file = fl.getFilename();
	if (node != null && node.length() > 0) {
	    file = "file://" + file + "/";
	}
	return file;
    }

    public static FileLocation getFileLocation(String fl) {
	String node = null;
	String file = fl;
	if (fl.startsWith("file://")) {
	    file = file.substring(7);
	    int split = file.indexOf("/");
	    node = file.substring(0, split);
	    file = file.substring(split + 1);
	}
        return new FileLocation(node, file);
    }

}

