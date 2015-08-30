package roart.database;

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
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;

import roart.model.IndexFiles;
import roart.model.FileLocation;
import roart.util.ConfigConstants;
import roart.util.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseIndexFiles {

    private static Logger log = LoggerFactory.getLogger(HbaseIndexFiles.class);

    // column families
    private static final byte[] indexcf = Bytes.toBytes("if");
    private static final byte[] flcf = Bytes.toBytes("fl");
    private static final byte[] filescf = Bytes.toBytes("fi");

    // column qualifiers
    private static final byte[] md5q = Bytes.toBytes("md5");
    private static final byte[] indexedq = Bytes.toBytes("indexed");
    private static final byte[] timestampq = Bytes.toBytes("timestamp");
    private static final byte[] timeindexq = Bytes.toBytes("timeindex");
    private static final byte[] timeclassq = Bytes.toBytes("timeclass");
    private static final byte[] classificationq = Bytes.toBytes("classification");
    private static final byte[] convertswq = Bytes.toBytes("convertsw");
    private static final byte[] converttimeq = Bytes.toBytes("converttime");
    private static final byte[] failedq = Bytes.toBytes("failed");
    private static final byte[] failedreasonq = Bytes.toBytes("failedreason");
    private static final byte[] timeoutreasonq = Bytes.toBytes("timeoutreason");
    private static final byte[] noindexreasonq = Bytes.toBytes("noindexreason");
    private static final byte[] languageq = Bytes.toBytes("language");
    private static final byte[] nodeq = Bytes.toBytes("node");
    private static final byte[] filenameq = Bytes.toBytes("filename");
    private static final byte[] filelocationq = Bytes.toBytes("filelocation");

    private static Table filesTable = null;
    private static Table indexTable = null;
    private static Connection hconn = null;
    
    public HbaseIndexFiles() {
	try {
	Configuration conf = HBaseConfiguration.create();
	String quorum = roart.util.Prop.getProp().getProperty(ConfigConstants.HBASEQUORUM);
	String port = roart.util.Prop.getProp().getProperty(ConfigConstants.HBASEPORT);
	String master = roart.util.Prop.getProp().getProperty(ConfigConstants.HBASEMASTER);
	conf.set("hbase.zookeeper.quorum", quorum);
	conf.set("hbase.zookeeper.property.clientPort", port);
	conf.set("hbase.master", master);
	
	hconn = ConnectionFactory.createConnection(conf);
	
	Admin admin = hconn.getAdmin();
	HTableDescriptor indexTableDesc = new HTableDescriptor(TableName.valueOf("index"));
	if (admin.tableExists(indexTableDesc.getTableName())) {
	    //admin.disableTable(table.getName());
	    //admin.deleteTable(table.getName());
	} else {
		indexTableDesc.addFamily(new HColumnDescriptor("if"));
		indexTableDesc.addFamily(new HColumnDescriptor("fl"));
	    admin.createTable(indexTableDesc);
	}
	
	indexTable = hconn.getTable(TableName.valueOf("index"));
	if (admin.isTableEnabled(TableName.valueOf("index"))) {
	    admin.disableTable(TableName.valueOf("index"));
	}
	if (!indexTable.getTableDescriptor().hasFamily(indexcf)) {
	    admin.addColumn(TableName.valueOf("index"), new HColumnDescriptor("if"));
	    //tableDesc.addFamily(new HColumnDescriptor("if"));
	}
	if (!indexTable.getTableDescriptor().hasFamily(flcf)) {
	    admin.addColumn(TableName.valueOf("index"), new HColumnDescriptor("fl"));
	    //tableDesc.addFamily(new HColumnDescriptor("fl"));
	}
	admin.enableTable(TableName.valueOf("index"));

	HTableDescriptor filesTableDesc = new HTableDescriptor(TableName.valueOf("files"));
	if (admin.tableExists(filesTableDesc.getTableName())) {
	    //admin.disableTable(table.getName());
	    //admin.deleteTable(table.getName());
	} else {
		filesTableDesc.addFamily(new HColumnDescriptor("fi"));
	    admin.createTable(filesTableDesc);
	}
	filesTable = hconn.getTable(TableName.valueOf("files"));
	if (admin.isTableEnabled(TableName.valueOf("files"))) {
	    admin.disableTable(TableName.valueOf("files"));
	}
	if (!filesTable.getTableDescriptor().hasFamily(filescf)) {
	    admin.addColumn(TableName.valueOf("files"), new HColumnDescriptor("fi"));
	    //filesTableDesc.addFamily(new HColumnDescriptor("fi"));
	}
	admin.enableTable(TableName.valueOf("files"));
	//HTable table = new HTable(conf, "index");
	} catch (IOException e) {
	    log.error(Constants.EXCEPTION, e);
	}
    }

    public static void put(IndexFiles ifile) throws Exception {
	    //HTable /*Interface*/ filesTable = new HTable(conf, "index");
	    Put put = new Put(Bytes.toBytes(ifile.getMd5()));
	    put.addColumn(indexcf, md5q, Bytes.toBytes(ifile.getMd5()));
	    if (ifile.getIndexed() != null) {
		put.addColumn(indexcf, indexedq, Bytes.toBytes("" + ifile.getIndexed()));
	    }
	    if (ifile.getTimestamp() != null) {
		put.addColumn(indexcf, timestampq, Bytes.toBytes(ifile.getTimestamp()));
	    }
	    if (ifile.getTimeindex() != null) {
		put.addColumn(indexcf, timeindexq, Bytes.toBytes(ifile.getTimeindex()));
	    }
	    if (ifile.getTimeclass() != null) {
		put.addColumn(indexcf, timeclassq, Bytes.toBytes(ifile.getTimeclass()));
	    }
	    if (ifile.getClassification() != null) {
		put.addColumn(indexcf, classificationq, Bytes.toBytes(ifile.getClassification()));
	    }
	    if (ifile.getConvertsw() != null) {
		put.addColumn(indexcf, convertswq, Bytes.toBytes(ifile.getConvertsw()));
	    }
	    if (ifile.getConverttime() != null) {
		put.addColumn(indexcf, converttimeq, Bytes.toBytes(ifile.getConverttime()));
	    }
	    if (ifile.getFailed() != null) {
		put.addColumn(indexcf, failedq, Bytes.toBytes("" + ifile.getFailed()));
	    }
	    if (ifile.getFailedreason() != null) {
		put.addColumn(indexcf, failedreasonq, Bytes.toBytes(ifile.getFailedreason()));
	    }
	    if (ifile.getTimeoutreason() != null) {
		put.addColumn(indexcf, timeoutreasonq, Bytes.toBytes(ifile.getTimeoutreason()));
	    }
	    if (ifile.getNoindexreason() != null) {
		put.addColumn(indexcf, noindexreasonq, Bytes.toBytes(ifile.getNoindexreason()));
	    }
	    if (ifile.getLanguage() != null) {
		put.addColumn(indexcf, languageq, Bytes.toBytes(ifile.getLanguage()));
	    }
	    //log.info("hbase " + ifile.getMd5());
	    int i = -1;
	    for (FileLocation file : ifile.getFilelocations()) {
		i++;
		String filename = getFile(file);
		//log.info("hbase " + filename);
		put.addColumn(flcf, Bytes.toBytes("q" + i), Bytes.toBytes(filename));
	    }
	    i++;
	    // now, delete the rest (or we would get some old historic content)
	    for (; i < ifile.getMaxfilelocations(); i++) {
	    	Delete d = new Delete(Bytes.toBytes(ifile.getMd5()));
		d.addColumns(flcf, Bytes.toBytes("q" + i)); // yes this deletes, was previously deleteColumns
	    	//log.info("Hbase delete q" + i);
	    	indexTable.delete(d);
	    }
	    
	    put(ifile.getMd5(), ifile.getFilelocations());	    
	    indexTable.put(put);

	// or if still to slow, simply get current (old) indexfiles
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
	    
    }

    // is this handling other nodes
    // plus get set of existing, remove new from that, delete the rest.

    public static void put(String md5, Set<FileLocation> files) throws Exception {
	    //HTable /*Interface*/ filesTable = new HTable(conf, "index");
	    for (FileLocation file : files) {
		String filename = getFile(file);
		Put put = new Put(Bytes.toBytes(filename));
		put.addColumn(filescf, md5q, Bytes.toBytes(md5));
		filesTable.put(put);
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
	ifile.setLanguage(bytesToString(index.getValue(indexcf, languageq)));
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
	Scan scan=new Scan();
	SingleColumnValueFilter filter=new SingleColumnValueFilter(Bytes.toBytes("fi"),Bytes.toBytes("md5"),CompareFilter.CompareOp.EQUAL,new SubstringComparator(md5));
	scan.setFilter(filter);
	ResultScanner scanner = filesTable.getScanner(scan);
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

    public static void flush() throws Exception {
	    ((HTable) filesTable).flushCommits();
	    ((HTable) indexTable).flushCommits();
    }

    public static void close() throws Exception {
	    log.info("closing db");
	    if (filesTable != null) {
	    filesTable.close();
	    filesTable = hconn.getTable(TableName.valueOf("files"));
	    }
	    if (indexTable != null) {
	    indexTable.close();
	    indexTable = hconn.getTable(TableName.valueOf("index"));
	    }
    }

	public static Set<String> getAllMd5() throws Exception {
		Set<String> md5s = new HashSet<String>();
		ResultScanner scanner = indexTable.getScanner(new Scan());
		for (Result index = scanner.next(); index != null; index = scanner.next()) {
			String md5 = bytesToString(index.getValue(indexcf, md5q));
		    md5s.add(md5);
		}
		return md5s;
	}

	public static Set<String> getLanguages() throws Exception {
		Set<String> languages = new HashSet<String>();
		ResultScanner scanner = indexTable.getScanner(new Scan());
		for (Result index = scanner.next(); index != null; index = scanner.next()) {
			String language = bytesToString(index.getValue(indexcf, languageq));
		    languages.add(language);
		}
		return languages;
	}

    public static void delete(IndexFiles index) throws Exception {
        for (int i = -1; i < index.getMaxfilelocations(); i++) {
            Delete d = new Delete(Bytes.toBytes(index.getMd5()));
            d.addColumns(flcf, Bytes.toBytes("q" + i)); // yes this deletes, was previously deleteColumns
            indexTable.delete(d);
        }
        
        Set<FileLocation> curfls = getFilelocationsByMd5(index.getMd5());
        curfls.removeAll(index.getFilelocations());

        // delete the files no longer associated to the md5
        for (FileLocation fl : curfls) {
            String name = fl.toString();
            log.info("Hbase delete " + name);
            Delete d = new Delete(Bytes.toBytes(name));
            filesTable.delete(d);
        }
    }

}

