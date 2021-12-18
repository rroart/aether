package roart.database.hbase;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.database.DatabaseConstructorParam;
import roart.common.database.DatabaseConstructorResult;
import roart.common.model.FileLocation;
import roart.common.model.IndexFiles;
import roart.common.util.FsUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseIndexFiles {

    private Logger log = LoggerFactory.getLogger(HbaseIndexFiles.class);

    private HbaseConfig config;

    // column families
    private final byte[] indexcf = Bytes.toBytes("if");
    private final byte[] flcf = Bytes.toBytes("fl");
    private final byte[] filescf = Bytes.toBytes("fi");

    // column qualifiers
    private final byte[] md5q = Bytes.toBytes("md5");
    private final byte[] indexedq = Bytes.toBytes("indexed");
    private final byte[] timestampq = Bytes.toBytes("timestamp");
    private final byte[] timeindexq = Bytes.toBytes("timeindex");
    private final byte[] timeclassq = Bytes.toBytes("timeclass");
    private final byte[] classificationq = Bytes.toBytes("classification");
    private final byte[] convertswq = Bytes.toBytes("convertsw");
    private final byte[] converttimeq = Bytes.toBytes("converttime");
    private final byte[] failedq = Bytes.toBytes("failed");
    private final byte[] failedreasonq = Bytes.toBytes("failedreason");
    private final byte[] timeoutreasonq = Bytes.toBytes("timeoutreason");
    private final byte[] noindexreasonq = Bytes.toBytes("noindexreason");
    private final byte[] languageq = Bytes.toBytes("language");
    private final byte[] isbnq = Bytes.toBytes("isbn");
    private final byte[] nodeq = Bytes.toBytes("node");
    private final byte[] filenameq = Bytes.toBytes("filename");
    private final byte[] filelocationq = Bytes.toBytes("filelocation");

    private Table filesTable = null;
    private Table indexTable = null;

    private final String INDEX = "index";
    private final String FILES = "files";
    
    public HbaseIndexFiles(String nodename, NodeConfig nodeConf) {
        config = new HbaseConfig();
        try {
            Configuration conf = HBaseConfiguration.create();
            String quorum = nodeConf.getHbasequorum(); 
            String port = nodeConf.getHbaseport();
            String master = nodeConf.getHbasemaster();
            conf.set("hbase.zookeeper.quorum", quorum);
            conf.set("hbase.zookeeper.property.clientPort", port);
            conf.set("hbase.master", master);

            Connection hconn = ConnectionFactory.createConnection(conf);
            config.setConnection(hconn);
            config.setNodename(nodename);
            config.setTableprefix(nodeConf.getHbaseTableprefix());

            Admin admin = hconn.getAdmin();
            //HTableDescriptor indexTableDesc = new HTableDescriptor(TableName.valueOf(getIndex()));
            if (admin.tableExists(TableName.valueOf(getIndex()))) {
                //admin.disableTable(table.getName());
                //admin.deleteTable(table.getName());
            } else {
                TableDescriptor indexTableDesc = TableDescriptorBuilder
                        .newBuilder(TableName.valueOf(getIndex()))
                        .setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(indexcf).build())
                        .setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(flcf).build())
                        .build();
                admin.createTable(indexTableDesc);
            }

            indexTable = hconn.getTable(TableName.valueOf(getIndex()));
            if (admin.isTableEnabled(TableName.valueOf(getIndex()))) {
                admin.disableTable(TableName.valueOf(getIndex()));
            }
            if (!indexTable.getDescriptor().hasColumnFamily(indexcf)) {
                admin.addColumnFamily(TableName.valueOf(getIndex()), ColumnFamilyDescriptorBuilder.newBuilder(indexcf).build());
                //tableDesc.addFamily(new HColumnDescriptor("if"));
            }
            if (!indexTable.getDescriptor().hasColumnFamily(flcf)) {
                admin.addColumnFamily(TableName.valueOf(getIndex()), ColumnFamilyDescriptorBuilder.newBuilder(flcf).build());
                //tableDesc.addFamily(new HColumnDescriptor("fl"));
            }
            admin.enableTable(TableName.valueOf(getIndex()));

            if (admin.tableExists(TableName.valueOf(getIndex()))) {
                //admin.disableTable(table.getName());
                //admin.deleteTable(table.getName());
            } else {
                TableDescriptor filesTableDesc = TableDescriptorBuilder
                        .newBuilder(TableName.valueOf(getFiles()))
                        .setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(filescf).build())
                        .build();
                admin.createTable(filesTableDesc);
            }
            filesTable = hconn.getTable(TableName.valueOf(getFiles()));
            if (admin.isTableEnabled(TableName.valueOf(getFiles()))) {
                admin.disableTable(TableName.valueOf(getFiles()));
            }
            if (!filesTable.getDescriptor().hasColumnFamily(filescf)) {
                admin.addColumnFamily(TableName.valueOf(getFiles()), ColumnFamilyDescriptorBuilder.newBuilder(filescf).build());
                //filesTableDesc.addFamily(new HColumnDescriptor("fi"));
            }
            admin.enableTable(TableName.valueOf(getFiles()));
            //HTable table = new HTable(conf, getIndex());
        } catch (IOException e) {
            log.error(Constants.EXCEPTION, e);
        }
    }

    public void put(IndexFiles ifile) throws Exception {
        //HTable /*Interface*/ filesTable = new HTable(conf, getIndex());
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
        if (ifile.getIsbn() != null) {
            put.addColumn(indexcf, isbnq, Bytes.toBytes(ifile.getIsbn()));
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
            String name = fl.toString();
            log.info("Hbase delete " + name);
            Delete d = new Delete(Bytes.toBytes(name));
            filesTable.delete(d);
        }

    }

    // is this handling other nodes
    // plus get set of existing, remove new from that, delete the rest.

    public void put(String md5, Set<FileLocation> files) throws Exception {
        //HTable /*Interface*/ filesTable = new HTable(conf, getIndex());
        for (FileLocation file : files) {
            String filename = getFile(file);
            Put put = new Put(Bytes.toBytes(filename));
            put.addColumn(filescf, md5q, Bytes.toBytes(md5));
            filesTable.put(put);
        }
    }

    public IndexFiles get(Result index) {
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
        ifile.setIsbn(bytesToString(index.getValue(indexcf, isbnq)));
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

    public FileLocation getfl(Result files, String md5) {
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

    public Map<String, IndexFiles> get(Set<String> md5s) {
        Map<String, IndexFiles> indexFilesMap = new HashMap<>();
        for (String md5 : md5s) {
            IndexFiles indexFile = get(md5);
            if (indexFile != null) {
                indexFilesMap.put(md5, indexFile);
            }
        }
        return indexFilesMap;
    }

    public IndexFiles get(String md5) {
        try {
            Get get = new Get(Bytes.toBytes(md5));
            get.addFamily(indexcf);
            get.addFamily(flcf);
            Result index = indexTable.get(get);
            if (index.isEmpty()) {
                return null;
            }
            return get(index);
        } catch (IOException e) {
            log.error(Constants.EXCEPTION, e);
        }
        return null;
    }

    public IndexFiles getIndexByFilelocation(FileLocation fl) {
        String md5 = getMd5ByFilelocation(fl);
        if (md5.length() == 0) {
            return null;
        }
        return get(md5);
    }

    public String getMd5ByFilelocation(FileLocation fl) {
        String name = getFile(fl);
        try {
            //HTable /*Interface*/ filesTable = new HTable(conf, getIndex());
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
                    //fn = fn. substring(5);
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

    public Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
        Set<FileLocation> flset = new HashSet<FileLocation>();
        Scan scan=new Scan();
        SingleColumnValueFilter filter=new SingleColumnValueFilter(filescf ,md5q ,CompareOperator.EQUAL,new SubstringComparator(md5));
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

    public List<IndexFiles> getAll() throws Exception {
        List<IndexFiles> retlist = new ArrayList<IndexFiles>();
        /*
	Configuration conf = HBaseConfiguration.create();
	HTablePool pool = new HTablePool();
	HTableInterface indexTable = pool.getTable(getIndex());
         */
        ResultScanner scanner = indexTable.getScanner(new Scan());
        for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
            retlist.add(get(rr));
        }
        return retlist;
    }

    public IndexFiles ensureExistenceNot(String md5) throws Exception {
        try {
            //HTable /*Interface*/ filesTable = new HTable(conf, getIndex());
            Put put = new Put(Bytes.toBytes(md5));
            indexTable.put(put);
        } catch (IOException e) {
            log.error(Constants.EXCEPTION, e);
        }
        IndexFiles i = new IndexFiles(md5);
        //i.setMd5(md5);
        return i;
    }

    public String getFile(FileLocation fl) {
        if (fl == null) {
            return null;
        }
        return fl.toString();
    }

    public FileLocation getFileLocation(String fl) {
        if (fl == null) {
            return null;
        }
        return FsUtil.getFileLocation(fl);
    }

    private String convertNullNot(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    private String convert0(String s) {
        if (s == null) {
            return "0";
        }
        return s;
    }

    private String bytesToString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes);
    }

    public void flush() throws Exception {
        //   ((HTable) filesTable).flushCommits();
        //   ((HTable) indexTable).flushCommits();
    }

    public void commit() throws Exception {        
    }

    public void close() throws Exception {
        log.info("closing db");
        if (filesTable != null) {
            filesTable.close();
            filesTable = config.getConnection().getTable(TableName.valueOf(getFiles()));
        }
        if (indexTable != null) {
            indexTable.close();
            indexTable = config.getConnection().getTable(TableName.valueOf(getIndex()));
        }
    }

    public Set<String> getAllMd5() throws Exception {
        Set<String> md5s = new HashSet<String>();
        ResultScanner scanner = indexTable.getScanner(new Scan());
        for (Result index = scanner.next(); index != null; index = scanner.next()) {
            String md5 = bytesToString(index.getValue(indexcf, md5q));
            md5s.add(md5);
        }
        return md5s;
    }

    public Set<String> getLanguages() throws Exception {
        Set<String> languages = new HashSet<String>();
        ResultScanner scanner = indexTable.getScanner(new Scan());
        for (Result index = scanner.next(); index != null; index = scanner.next()) {
            String language = bytesToString(index.getValue(indexcf, languageq));
            languages.add(language);
        }
        return languages;
    }

    public void delete(Set<IndexFiles> indexes) throws Exception {
        for (IndexFiles index : indexes) {
            delete(index);
        }
    }
    
    public void delete(IndexFiles index) throws Exception {
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

    public void destroy() throws Exception {
        config.getConnection().close();
    }

    public String getIndex() {
        return config.getTableprefix() + "_" + getIndex();
    }
public String getFiles() {
    return config.getTableprefix() + "_" + getFiles();
}

public DatabaseConstructorResult clear(DatabaseConstructorParam param) {
    clear(indexTable);
    clear(filesTable);
    return new DatabaseConstructorResult();
}

private void clear(Table table) {
    try {
        List<Delete> deleteList = new ArrayList<Delete>();
        Scan scan = new Scan();
        ResultScanner scanner = table.getScanner(scan);
        for (Result rr : scanner) {
            Delete d = new Delete(rr.getRow());
            deleteList.add(d);
        }
        table.delete(deleteList);
        scanner.close();    
    } 
    catch (IOException e) {
        log.error(Constants.EXCEPTION, e);
    }
}

public DatabaseConstructorResult drop(DatabaseConstructorParam param) {
    try {
    Admin admin = config.getConnection().getAdmin();

    admin.disableTable(TableName.valueOf(getIndex()));
    admin.disableTable(TableName.valueOf(getFiles()));
    admin.deleteTable(TableName.valueOf(getIndex()));
    admin.deleteTable(TableName.valueOf(getFiles()));
    } catch (IOException e) {
        log.error(Constants.EXCEPTION, e);
    }
    return new DatabaseConstructorResult();
}
}

