package roart.model;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.TableName;
//import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
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
import roart.util.ConfigConstants;
import roart.util.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseIndexFiles {

    private static Logger log = LoggerFactory.getLogger("HbaseIndexFiles");

    // column families
    private static byte[] indexcf = Bytes.toBytes("if");
    private static byte[] flcf = Bytes.toBytes("fl");
    private static byte[] filescf = Bytes.toBytes("fi");

    // column qualifiers
    private static byte[] md5q = Bytes.toBytes("md5");
    private static byte[] indexedq = Bytes.toBytes("indexed");
    private static byte[] timestampq = Bytes.toBytes("timestamp");
    private static byte[] timeindexq = Bytes.toBytes("timeindex");
    private static byte[] timeclassq = Bytes.toBytes("timeclass");
    private static byte[] classificationq = Bytes.toBytes("classification");
    private static byte[] convertswq = Bytes.toBytes("convertsw");
    private static byte[] converttimeq = Bytes.toBytes("converttime");
    private static byte[] failedq = Bytes.toBytes("failed");
    private static byte[] failedreasonq = Bytes.toBytes("failedreason");
    private static byte[] timeoutreasonq = Bytes.toBytes("timeoutreason");
    private static byte[] noindexreasonq = Bytes.toBytes("noindexreason");
    private static byte[] nodeq = Bytes.toBytes("node");
    private static byte[] filenameq = Bytes.toBytes("filename");
    private static byte[] filelocationq = Bytes.toBytes("filelocation");

    private static HTableInterface filesTable = null;
    private static HTableInterface indexTable = null;
    private static HConnection hconn = null;
    
    public HbaseIndexFiles() {
	try {
	Configuration conf = HBaseConfiguration.create();
	String quorum = roart.util.Prop.getProp().getProperty(ConfigConstants.HBASEQUORUM);
	String port = roart.util.Prop.getProp().getProperty(ConfigConstants.HBASEPORT);
	String master = roart.util.Prop.getProp().getProperty(ConfigConstants.HBASEMASTER);
	conf.set("hbase.zookeeper.quorum", quorum);
	conf.set("hbase.zookeeper.property.clientPort", port);
	conf.set("hbase.master", master);
	
	hconn = HConnectionManager.createConnection(conf);
	
	HBaseAdmin admin = new HBaseAdmin(conf);
	HTableDescriptor indexTableDesc = new HTableDescriptor(TableName.valueOf("index"));
	if (admin.tableExists(indexTableDesc.getName())) {
	    //admin.disableTable(table.getName());
	    //admin.deleteTable(table.getName());
	} else {
	    admin.createTable(indexTableDesc);
	}
	
	indexTable = hconn.getTable("index");
	if (admin.isTableEnabled("index")) {
	    admin.disableTable("index");
	}
	if (!indexTable.getTableDescriptor().hasFamily(indexcf)) {
	    admin.addColumn("index", new HColumnDescriptor("if"));
	    //tableDesc.addFamily(new HColumnDescriptor("if"));
	}
	if (!indexTable.getTableDescriptor().hasFamily(flcf)) {
	    admin.addColumn("index", new HColumnDescriptor("fl"));
	    //tableDesc.addFamily(new HColumnDescriptor("fl"));
	}
	admin.enableTable("index");

	HTableDescriptor filesTableDesc = new HTableDescriptor(TableName.valueOf("files"));
	if (admin.tableExists(filesTableDesc.getName())) {
	    //admin.disableTable(table.getName());
	    //admin.deleteTable(table.getName());
	} else {
	    admin.createTable(filesTableDesc);
	}
	filesTable = hconn.getTable("files");
	if (admin.isTableEnabled("files")) {
	    admin.disableTable("files");
	}
	if (!filesTable.getTableDescriptor().hasFamily(filescf)) {
	    admin.addColumn("files", new HColumnDescriptor("fi"));
	    //filesTableDesc.addFamily(new HColumnDescriptor("fi"));
	}
	admin.enableTable("files");
	//HTable table = new HTable(conf, "index");
	} catch (IOException e) {
	    log.error(Constants.EXCEPTION, e);
	}
    }

    public static void put(IndexFiles ifile) {
	try {
	    //HTable /*Interface*/ filesTable = new HTable(conf, "index");
	    Put put = new Put(Bytes.toBytes(ifile.getMd5()));
	    put.add(indexcf, md5q, Bytes.toBytes(ifile.getMd5()));
	    if (ifile.getIndexed() != null) {
		put.add(indexcf, indexedq, Bytes.toBytes("" + ifile.getIndexed()));
	    }
	    if (ifile.getTimestamp() != null) {
		put.add(indexcf, timestampq, Bytes.toBytes(ifile.getTimestamp()));
	    }
	    if (ifile.getTimeindex() != null) {
		put.add(indexcf, timeindexq, Bytes.toBytes(ifile.getTimeindex()));
	    }
	    if (ifile.getTimeclass() != null) {
		put.add(indexcf, timeclassq, Bytes.toBytes(ifile.getTimeclass()));
	    }
	    if (ifile.getClassification() != null) {
		put.add(indexcf, classificationq, Bytes.toBytes(ifile.getClassification()));
	    }
	    if (ifile.getConvertsw() != null) {
		put.add(indexcf, convertswq, Bytes.toBytes(ifile.getConvertsw()));
	    }
	    if (ifile.getConverttime() != null) {
		put.add(indexcf, converttimeq, Bytes.toBytes(ifile.getConverttime()));
	    }
	    if (ifile.getFailed() != null) {
		put.add(indexcf, failedq, Bytes.toBytes("" + ifile.getFailed()));
	    }
	    if (ifile.getFailedreason() != null) {
		put.add(indexcf, failedreasonq, Bytes.toBytes(ifile.getFailedreason()));
	    }
	    if (ifile.getTimeoutreason() != null) {
		put.add(indexcf, timeoutreasonq, Bytes.toBytes(ifile.getTimeoutreason()));
	    }
	    if (ifile.getNoindexreason() != null) {
		put.add(indexcf, noindexreasonq, Bytes.toBytes(ifile.getNoindexreason()));
	    }
	    //log.info("hbase " + ifile.getMd5());
	    int i = -1;
	    for (FileLocation file : ifile.getFilelocations()) {
		i++;
		String filename = getFile(file);
		//log.info("hbase " + filename);
		put.add(flcf, Bytes.toBytes("q" + i), Bytes.toBytes(filename));
	    }
	    i++;
	    // now, delete the rest (or we would get some old historic content)
	    for (; i < ifile.getMaxfilelocations(); i++) {
	    	Delete d = new Delete(Bytes.toBytes(ifile.getMd5()));
		d.deleteColumns(flcf, Bytes.toBytes("q" + i));
	    	//log.info("Hbase delete q" + i);
	    	indexTable.delete(d);
	    }
	    
	    put(ifile.getMd5(), ifile.getFilelocations());	    
	    indexTable.put(put);

	    Set<FileLocation> curfls = getFilelocationsByMd5(ifile.getMd5());
	    curfls.removeAll(ifile.getFilelocations());

	    // delete the files no longer associated to the md5
	    for (FileLocation fl : curfls) {
	    	if (!fl.isLocal()) {
		    continue;
		}
	    	String name = fl.toString();
	    	log.info("Hbase delete " + name);
	    	Delete d = new Delete(Bytes.toBytes(name));
	    	filesTable.delete(d);
	    }
	    
	} catch (IOException e) {
	    log.error(Constants.EXCEPTION, e);
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
    }

    // is this handling other nodes
    // plus get set of existing, remove new from that, delete the rest.

    public static void put(String md5, Set<FileLocation> files) {
	try {
	    //HTable /*Interface*/ filesTable = new HTable(conf, "index");
	    for (FileLocation file : files) {
		String filename = getFile(file);
		Put put = new Put(Bytes.toBytes(filename));
		put.add(filescf, md5q, Bytes.toBytes(md5));
		filesTable.put(put);
	    }
	} catch (IOException e) {
	    log.error(Constants.EXCEPTION, e);
	}
    }

    public static IndexFiles get(Result index) {
	String md5 = bytesToString(index.getValue(indexcf, md5q));
	IndexFiles ifile = new IndexFiles(md5);
	//ifile.setMd5(bytesToString(index.getValue(indexcf, md5q)));
	ifile.setIndexed(new Boolean(bytesToString(index.getValue(indexcf, indexedq))));
	ifile.setTimeindex(bytesToString(index.getValue(indexcf, timeindexq)));
	ifile.setTimestamp(bytesToString(index.getValue(indexcf, timestampq)));
	ifile.setTimeclass(bytesToString(index.getValue(indexcf, timeclassq)));
	ifile.setClassification(bytesToString(index.getValue(indexcf, classificationq)));
	ifile.setConvertsw(bytesToString(index.getValue(indexcf, convertswq)));
	ifile.setConverttime(bytesToString(index.getValue(indexcf, converttimeq)));
	ifile.setFailed(new Integer(convert0(bytesToString(index.getValue(indexcf, failedq)))));
	ifile.setFailedreason(bytesToString(index.getValue(indexcf, failedreasonq)));
	ifile.setTimeoutreason(bytesToString(index.getValue(indexcf, timeoutreasonq)));
	ifile.setNoindexreason(bytesToString(index.getValue(indexcf, noindexreasonq)));
	List<Cell> list = index.listCells();
	if (list != null) {
	for (Cell kv : list) {
	    byte[] family = CellUtil.cloneFamily(kv);
	    String fam = new String(family);
	    if (fam.equals("fl")) {
		byte[] qual = CellUtil.cloneValue(kv);
		String loc = Bytes.toString(qual);
		FileLocation fl = getFileLocation(loc);
		ifile.addFile(fl);
	    }
	}
	}
	ifile.setUnchanged();
	return ifile;
    }

    public static FileLocation getfl(Result files, String md5) {
	FileLocation fl = null;
	List<Cell> list = files.listCells();
	if (list != null) {
	for (Cell kv : list) {
	    byte[] family = CellUtil.cloneFamily(kv);
	    String fam = new String(family);
	    if (fam.equals("fi")) {
		byte[] qual = CellUtil.cloneValue(kv);
		String md5tmp = new String(qual);
		if (md5.equals(md5tmp)) {
		    byte [] key = CellUtil.cloneRow(kv);
		    String loc = new String(key);
		    FileLocation fltmp = getFileLocation(loc);
		    fl = fltmp;
		}
	    }
	}
	}
	return fl;
    }

    public static IndexFiles get(String md5) {
	try {
	    //HTable /*Interface*/ filesTable = new HTable(conf, "index");
	    Get get = new Get(Bytes.toBytes(md5));
	    get.addFamily(indexcf);
	    get.addFamily(flcf);
	    Result index = indexTable.get(get);
	    if (index.isEmpty()) {
		return null;
	    }
	    IndexFiles ifile = get(index);
	    return ifile;
	} catch (IOException e) {
	    log.error(Constants.EXCEPTION, e);
	}
	return null;
    }

    public static IndexFiles getIndexByFilelocation(FileLocation fl) {
	String md5 = getMd5ByFilelocation(fl);
	if (md5.length() == 0) {
	    return null;
	}
	return get(md5);
    }

    public static String getMd5ByFilelocation(FileLocation fl) {
	String name = getFile(fl);
	try {
	    //HTable /*Interface*/ filesTable = new HTable(conf, "index");
	    Get get = new Get(Bytes.toBytes(name));
	    //get.addColumn(filescf, md5q);
	    get.addFamily(filescf);
	    Result result = filesTable.get(get);
	    //log.info("res " + new String(result.getValue(filescf, md5q)));
	    // add a little workaround for temp backward compatibility
	    // TODO remove later, old compat
	    if (result.isEmpty()) {
		String fn = fl.getFilename();
		if (fn != null) {
		    fn = fn.substring(5);
		    get = new Get(Bytes.toBytes(fn));
		    get.addFamily(filescf);
		    result = filesTable.get(get);
		}
	    }
	    if (result.isEmpty()) {
		return null;
	    }
	    return bytesToString(result.getValue(filescf, md5q));
	} catch (IOException e) {
	    log.error(Constants.EXCEPTION, e);
	}
	return null;
    }

    public static Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
	Set<FileLocation> flset = new HashSet<FileLocation>();
	ResultScanner scanner = filesTable.getScanner(new Scan());
	for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
	    FileLocation fl = getfl(rr, md5);
	    if (fl != null) {
		flset.add(fl);
	    }
	}
	return flset;
    }

    public static List<IndexFiles> getAll() throws Exception {
	List<IndexFiles> retlist = new ArrayList<IndexFiles>();
	/*
	Configuration conf = HBaseConfiguration.create();
	HTablePool pool = new HTablePool();
	HTableInterface indexTable = pool.getTable("index");
	*/
	ResultScanner scanner = indexTable.getScanner(new Scan());
	for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
	    retlist.add(get(rr));
	}
	return retlist;
    }

    public static IndexFiles ensureExistenceNot(String md5) throws Exception {
	try {
	    //HTable /*Interface*/ filesTable = new HTable(conf, "index");
	    Put put = new Put(Bytes.toBytes(md5));
	    indexTable.put(put);
	} catch (IOException e) {
	    log.error(Constants.EXCEPTION, e);
	}
	IndexFiles i = new IndexFiles(md5);
	//i.setMd5(md5);
	return i;
    }

    public static String getFile(FileLocation fl) {
    	if (fl == null) {
    		return null;
    	}
    	return fl.toString();
    }

    public static FileLocation getFileLocation(String fl) {
    	if (fl == null) {
    		return null;
    	}
    	return new FileLocation(fl);
    }

    private static String convertNullNot(String s) {
	if (s == null) {
	    return "";
	}
	return s;
    }

    private static String convert0(String s) {
	if (s == null) {
	    return "0";
	}
	return s;
    }

    private static String bytesToString(byte[] bytes) {
	if (bytes == null) {
	    return null;
	}
	return new String(bytes);
    }

    public static void flush() {
	try {
	    filesTable.flushCommits();
	    indexTable.flushCommits();
	} catch (IOException e) {
	    log.error(Constants.EXCEPTION, e);
	}
    }

    public static void close() {
	try {
	    log.info("closing db");
	    filesTable.close();
	    indexTable.close();
	    filesTable = hconn.getTable("files");
	    indexTable = hconn.getTable("index");
	} catch (IOException e) {
	    log.error(Constants.EXCEPTION, e);
	}
    }

}

