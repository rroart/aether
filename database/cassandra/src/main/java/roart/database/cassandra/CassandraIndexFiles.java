package roart.database.cassandra;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.IOException;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.database.DatabaseConstructorParam;
import roart.common.model.FileLocation;
import roart.common.model.IndexFiles;
import roart.common.util.FsUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.CodecRegistry;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TypeCodec;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;

public class CassandraIndexFiles {

    private Logger log = LoggerFactory.getLogger(CassandraIndexFiles.class);

    private CassandraConfig config;

    private static final String TABLE_FILES_NAME = "files";
    private static final String TABLE_INDEXFILES_NAME = "indexfiles";
    private Session session;

    public Session getSession() {
        return session;
    }

    /*
    public void setSession(Session session) {
        this.session = session;
    }
     */

    public void createKeyspace(
            String keyspaceName, String replicationStrategy, int replicationFactor) {
        StringBuilder sb = 
                new StringBuilder("CREATE KEYSPACE IF NOT EXISTS ")
                .append(keyspaceName).append(" WITH replication = {")
                .append("'class':'").append(replicationStrategy)
                .append("','replication_factor':").append(replicationFactor)
                .append("};");

        String query = sb.toString();
        session.execute(query);
        session.execute("use " + keyspaceName + ";");
    }

    public void createType() {
        try {
            StringBuilder sb = 
                    new StringBuilder("CREATE TYPE filelocation ( ")
                    .append("node text,")
                    .append("filename text);");

            String query = sb.toString();
            session.execute(query);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        CodecRegistry codecRegistry = session.getCluster().getConfiguration().getCodecRegistry();             
        UserType addressType = session.getCluster().getMetadata().getKeyspace("library").getUserType("filelocation");
        TypeCodec<UDTValue> addressTypeCodec = codecRegistry.codecFor(addressType);
        FilelocationCodec myJsonCodec = new FilelocationCodec(addressTypeCodec, FileLocation.class);
        codecRegistry.register(myJsonCodec);
        /*
              JsonCodec<FileLocation> myJsonCodec = new JsonCodec<>(FileLocation.class);
              CodecRegistry myCodecRegistry = session.getCluster().getConfiguration().getCodecRegistry();
              myCodecRegistry.register(myJsonCodec);
         */


    }

    public void createTable() {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(TABLE_FILES_NAME).append("(")
                .append("filename text PRIMARY KEY, ")
                .append("md5 text);");

        String query = sb.toString();
        System.out.println("q1");
        try {
            session.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("q2");
        StringBuilder sb2 = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(TABLE_INDEXFILES_NAME).append("(")
                .append("md5 text PRIMARY KEY, ")
                .append("indexed text, ")
                .append("timestamp text, ")
                .append("timeindex text, ")
                .append("timeclass text, ")
                .append("classification text, ")
                .append("convertsw text, ")
                .append("converttime text, ")
                .append("failed int, ")
                .append("failedreason text, ")
                .append("timeoutreason text, ")
                .append("noindexreason text, ")
                .append("language text, ")
                .append("isbn text, ")
                .append("node text, ")
                .append("filename text, ")
                .append("filelocation set<frozen<filelocation>>);");

        String query2 = sb2.toString();
        System.out.println("q3");
        try {
            session.execute(query2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("q4");
    }
    // column families
    private final String indexcf = "if";
    private final String flcf = "fl";
    private final String filescf = "fi";

    // column qualifiers
    private final String md5q = "md5";
    private final String indexedq = "indexed";
    private final String timestampq = "timestamp";
    private final String timeindexq = "timeindex";
    private final String timeclassq = "timeclass";
    private final String classificationq = "classification";
    private final String convertswq = "convertsw";
    private final String converttimeq = "converttime";
    private final String failedq = "failed";
    private final String failedreasonq = "failedreason";
    private final String timeoutreasonq = "timeoutreason";
    private final String noindexreasonq = "noindexreason";
    private final String languageq = "language";
    private final String isbnq = "isbn";
    private final String nodeq = "node";
    private final String filenameq = "filename";
    private final String filelocationq = "filelocation";

    public CassandraIndexFiles(Session session, String nodename, NodeConfig nodeConf) {
        String port = "9042";
        String host = "localhost";
        if (session == null) {
            port = nodeConf.getCassandraPort();
            host = nodeConf.getCassandraHost();
        }
        config = new CassandraConfig();
        if (session != null) {
            this.session = session;
        } else {
            Cluster cluster;


            Builder b = Cluster.builder().addContactPoint(host);
            if (port != null) {
                b.withPort(Integer.valueOf(port));
            }
            log.info("Cluster build");
            cluster = b.build();
            log.info("Cluster built");

            this.session = cluster.connect();
            log.info("Cluster connected");
            log.info("Session {}", this.session.getCluster().getClusterName());
        }
        config.setSession(this.session);
        config.setNodename(nodename);
        System.out.println("ct1");
        try {
            createKeyspace(nodeConf.getCassandraKeyspace(), "SimpleStrategy", 1);
            createType();
            createTable();
        } catch (Exception e) {
            e.printStackTrace();
            // check if exists
        }
        System.out.println("ct2");
    }

    public void put(IndexFiles ifile) throws Exception {
        StringBuilder sb = new StringBuilder("BEGIN BATCH ")
                .append("UPDATE ").append(TABLE_INDEXFILES_NAME).append(" set ");
        if (ifile.getIndexed() != null) {
            sb.append(indexedq + " = '" + ifile.getIndexed() + "', ");
        }
        if (ifile.getTimestamp() != null) {
            sb.append(timestampq + " = '" + ifile.getTimestamp() + "', ");
        }
        if (ifile.getTimeindex() != null) {
            sb.append(timeindexq + " = '" + ifile.getTimeindex() + "', ");
        }
        if (ifile.getTimeclass() != null) {
            sb.append(timeclassq + " = '" + ifile.getTimeclass() + "', ");
        }
        if (ifile.getClassification() != null) {
            sb.append(classificationq + " = '" + ifile.getClassification() + "', ");
        }
        if (ifile.getConvertsw() != null) {
            sb.append(convertswq + " = '" + ifile.getConvertsw() + "', ");
        }
        if (ifile.getConverttime() != null) {
            sb.append(converttimeq + " = '" + ifile.getConverttime() + "', ");
        }
        if (ifile.getFailed() != null) {
            sb.append(failedq + " = " +"" + ifile.getFailed() + ", ");
        }
        if (ifile.getFailedreason() != null) {
            sb.append(failedreasonq + " = '" +ifile.getFailedreason() + "', ");
        }
        if (ifile.getTimeoutreason() != null) {
            sb.append(timeoutreasonq + " = '" +ifile.getTimeoutreason() + "', ");
        }
        if (ifile.getNoindexreason() != null) {
            sb.append(noindexreasonq + " = '" +ifile.getNoindexreason() + "', ");
        }
        if (ifile.getLanguage() != null) {
            sb.append(languageq + " = '" +ifile.getLanguage() + "', ");
        }
        if (ifile.getIsbn() != null) {
            sb.append(isbnq + " = '" +ifile.getIsbn() + "', ");
        }
        if (ifile.getFilelocations() != null) {
            sb.append(filelocationq + " = ? ");	        
        }
        sb.append("where " + md5q + " = '" + ifile.getMd5() + "'; apply batch;");
        //log.info("hbase " + ifile.getMd5());
        int i = -1;
        /*
            for (FileLocation file : ifile.getFilelocations()) {
	        StringBuilder sb2 = new StringBuilder("BEGIN BATCH ")
	                .append("UPDATE ").append(TABLE_FILES_NAME).append(" set ");
	        sb2.append(md5q + " = " + ifile.getMd5() + ", ");
	            if (file.getFilename() != null) {
	                sb2.append(md5q + " = " + file.getFilename() + ", ");
	            }
	            session.execute(sb2.toString());
		i++;
            }
         */
        String str = sb.toString();
        System.out.println("put1" + sb.toString());
        PreparedStatement prepared = session.prepare(str);
        BoundStatement bound = prepared.bind(ifile.getFilelocations());
        session.execute(bound);
        System.out.println("put2");
        /*
	    // now, delete the rest (or we would get some old historic content)
	    for (; i < ifile.getMaxfilelocations(); i++) {
	    	StringBuilder sb3 = new StringBuilder("BEGIN BATCH ")
	    	        .append("DELETE FROM ").append(TABLE_INDEXFILES_NAME).append(" where ")
	    	        .append(md5q + " = " + ifile.getMd5());
	    	session.execute(sb3.toString());
	    }
	           System.out.println("put3");
         */
        put(ifile.getMd5(), ifile.getFilelocations());

        // or if still to slow, simply get current (old) indexfiles
        Set<FileLocation> curfls = getFilelocationsByMd5(ifile.getMd5());
        curfls.removeAll(ifile.getFilelocations());

        // delete the files no longer associated to the md5
        for (FileLocation fl : curfls) {
            if (!fl.isLocal(config.getNodename())) {
                continue;
            }
            String name = fl.toString();
            log.info("Cassandra delete {}", name);
            StringBuilder sb3 = new StringBuilder("BEGIN BATCH ")
                    .append("DELETE FROM ").append(TABLE_FILES_NAME).append(" where ")
                    .append(filenameq + " = '" + name + "';apply batch;");
            System.out.println("del " + sb3.toString());
            session.execute(sb3.toString());
        }
        System.out.println("put4");

    }

    public void put(String md5, Set<FileLocation> files) throws Exception {
        //HTable /*Interface*/ filesTable = new HTable(conf, "index");
        for (FileLocation file : files) {
            System.out.println("files put " + md5 + " " + file);
            String filename = getFile(file);
            StringBuilder sb = new StringBuilder("BEGIN BATCH ")
                    .append("UPDATE ").append(TABLE_FILES_NAME).append(" set ");
            sb.append(md5q + " = '" + md5 + "' ");
            sb.append("where " + filenameq + " = '" + filename + "'; apply batch;");
            String str = sb.toString();
            System.out.println("put1" + sb.toString());
            session.execute(str);
        }
    }

    public IndexFiles get(Row row) {
        String md5 = row.getString(md5q);
        IndexFiles ifile = new IndexFiles(md5);
        //ifile.setMd5(bytesToString(index.getValue(indexcf, md5q)));
        ifile.setIndexed(new Boolean(row.getString(indexedq)));
        ifile.setTimeindex(row.getString(timeindexq));
        ifile.setTimestamp(row.getString(timestampq));
        ifile.setTimeclass(row.getString(timeclassq));
        ifile.setClassification(row.getString(classificationq));
        ifile.setConvertsw(row.getString(convertswq));
        ifile.setConverttime(row.getString(converttimeq));
        ifile.setFailed(row.getInt(failedq));
        ifile.setFailedreason(row.getString(failedreasonq));
        ifile.setTimeoutreason(row.getString(timeoutreasonq));
        ifile.setNoindexreason(row.getString(noindexreasonq));
        ifile.setLanguage(row.getString(languageq));
        ifile.setIsbn(row.getString(isbnq));
        ifile.setFilelocations(row.getSet(filelocationq, FileLocation.class));
        ifile.setUnchanged();
        return ifile;
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
        StringBuilder sb3 = new StringBuilder("SELECT * FROM ").append(TABLE_INDEXFILES_NAME).append(" where ")
                .append(md5q + " = '" + md5).append("';");
        System.out.println(sb3.toString());
        ResultSet resultSet = session.execute(sb3.toString());
        for (Row row : resultSet) {
            return get(row);
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
        Set<FileLocation> flset = new HashSet<>();
        StringBuilder sb3 = new StringBuilder("SELECT * FROM ").append(TABLE_FILES_NAME).append(" where ")
                .append(filenameq + " = '" + name + "';");
        ResultSet resultSet = session.execute(sb3.toString());
        for (Row row : resultSet) {
            return row.getString(md5q);
        }
        return null;
    }

    public Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
        Set<FileLocation> flset = new HashSet<>();
        StringBuilder sb3 = new StringBuilder("")
                .append("SELECT * FROM ").append(TABLE_FILES_NAME).append(" where ")
                .append(md5q + " = '" + md5 + "' ALLOW FILTERING;");
        System.out.println("sb3 " + sb3.toString());
        ResultSet resultSet = session.execute(sb3.toString());
        //System.out.println("h0 " + resultSet.all().size());
        for (Row row : resultSet) {
            System.out.println("h1 " + row);
            FileLocation fl = getFileLocation(row.getString(filenameq));
            if (fl != null) {
                flset.add(fl);
            }
        }
        return flset;
    }

    public List<IndexFiles> getAll() throws Exception {
        StringBuilder sb = new StringBuilder("SELECT * FROM ")
                .append(TABLE_INDEXFILES_NAME).append(";");
        String query = sb.toString();
        ResultSet resultSet = session.execute(query);	
        List<IndexFiles> retlist = new ArrayList<>();
        for (Row row : resultSet) {
            retlist.add(get(row));
        }
        return retlist;
    }

    public IndexFiles ensureExistenceNot(String md5) throws Exception {
        try {
            //HTable /*Interface*/ filesTable = new HTable(conf, "index");
            //Put put = new Put(md5));
            //indexTable.put(put);
        } catch (Exception e) {
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

    public void flush() throws Exception {
        //
    }

    public void commit() throws Exception {        
    }

    public void close() throws Exception {
        log.info("closing db");
        session.close();
    }

    public Set<String> getAllMd5() throws Exception {
        Set<String> md5s = new HashSet<String>();
        StringBuilder sb = new StringBuilder("SELECT * FROM ").append(TABLE_INDEXFILES_NAME);
        ResultSet resultSet = session.execute(sb.toString());
        for (Row row : resultSet.all()) {
            String md5 = row.getString(md5q);
            md5s.add(md5);
        }
        return md5s;
    }

    public Set<String> getLanguages() throws Exception {
        StringBuilder sb = new StringBuilder("SELECT * FROM ").append(TABLE_INDEXFILES_NAME);
        ResultSet resultSet = session.execute(sb.toString());
        Set<String> languages = new HashSet<>();
        for (Row row : resultSet.all()) {
            String lang = row.getString("language");
            languages.add(lang);
        }
        return languages;
    }

    public void delete(IndexFiles index) throws Exception {
        StringBuilder sb = new StringBuilder("BEGIN BATCH ")
                .append("DELETE FROM ").append(TABLE_INDEXFILES_NAME).append(" where ")
                .append(md5q + " = '" + index.getMd5()).append("'; apply batch;");
        session.execute(sb.toString());

        Set<FileLocation> curfls = getFilelocationsByMd5(index.getMd5());
        System.out.println("curfls " + curfls.size());
        //curfls.removeAll(index.getFilelocations());
        //System.out.println("curfls " + curfls.size());

        // delete the files no longer associated to the md5
        for (FileLocation fl : curfls) {
            String name = fl.toString();
            log.info("Cassandra delete {}", name);
            StringBuilder sb3 = new StringBuilder("BEGIN BATCH ")
                    .append("DELETE FROM ").append(TABLE_FILES_NAME).append(" where ")
                    .append(filenameq + " = '" + name + "'; apply batch;");
            session.execute(sb3.toString());
        }
    }

    public void destroy() throws Exception {
        config.getSession().close();
    }

    public void clear(String tableName) {
    StringBuilder sb = 
            new StringBuilder("TRUNCATE ");
    sb.append(tableName);
    sb.append(";");
    String query = sb.toString();
    session.execute(query);

    }

    public void drop(String keyspaceName) {
    StringBuilder sb = 
            new StringBuilder("DROP KEYSPACE IF EXISTS ");
    sb.append(keyspaceName);
    sb.append(";");
    String query = sb.toString();
    session.execute(query);

    }

    public void clear(DatabaseConstructorParam param) {
        clear(TABLE_INDEXFILES_NAME);
        clear(TABLE_FILES_NAME);
    }

    public void drop(DatabaseConstructorParam param) {
        drop(param.getConf().getCassandraKeyspace());
    }
}

