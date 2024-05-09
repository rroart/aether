package roart.database.cassandra;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.io.IOException;
import java.net.InetSocketAddress;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.database.DatabaseConstructorParam;
import roart.common.model.FileLocation;
import roart.common.model.Files;
import roart.common.model.IndexFiles;
import roart.common.util.FsUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
//import com.datastax.oss.driver.api.core.cql.Cluster;
//import com.datastax.driver.core.Cluster.Builder;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.core.type.codec.registry.MutableCodecRegistry;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.session.Session;
import com.datastax.oss.driver.api.core.cql.Statement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import com.datastax.oss.driver.api.querybuilder.select.SelectFrom;
import com.datastax.oss.driver.api.querybuilder.truncate.Truncate;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.update.Update;
import com.datastax.oss.driver.api.querybuilder.update.UpdateWithAssignments;
import com.datastax.oss.driver.internal.core.type.codec.ParseUtils;
import com.datastax.oss.driver.api.querybuilder.update.UpdateStart;
import com.datastax.oss.driver.api.querybuilder.delete.Delete;
import com.datastax.oss.driver.api.querybuilder.insert.InsertInto;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspaceStart;
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable;
import com.datastax.oss.driver.api.querybuilder.schema.CreateTableStart;
import com.datastax.oss.driver.api.querybuilder.schema.CreateType;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.*;

public class CassandraIndexFiles {

    private Logger log = LoggerFactory.getLogger(CassandraIndexFiles.class);

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
    private final String mimetypeq = "mimetype";
    private final String sizeq = "size";
    private final String convertsizeq = "convertsize";
    private final String convertswq = "convertsw";
    private final String converttimeq = "converttime";
    private final String failedq = "failed";
    private final String failedreasonq = "failedreason";
    private final String timeoutreasonq = "timeoutreason";
    private final String noindexreasonq = "noindexreason";
    private final String languageq = "language";
    private final String isbnq = "isbn";
    private final String createdq = "created";
    private final String checkedq = "checked";
    private final String nodeq = "node";
    private final String filenameq = "filename";
    private final String filelocationq = "filelocation";
    private final String filelocationsq = "filelocations";

    //private static final CqlIdentifier KEYSPACE = CqlIdentifier.fromInternal("aether");
    private static final CqlIdentifier TABLE_FILES_NAME = CqlIdentifier.fromInternal("files");
    private static final CqlIdentifier TABLE_INDEXFILES_NAME = CqlIdentifier.fromInternal("indexfiles");

    private CassandraConfig config;

    private CqlSession session;

    private FilesDao filesDao;
    private IndexDao indexDao;
    private CqlIdentifier keyspace;
    private TypeCodec<UdtValue> filelocationTypeCodec;
    private MutableCodecRegistry codecRegistry;
    
    public CassandraIndexFiles(CqlSession session, String configname, NodeConfig nodeConf) {
        if (!nodeConf.wantCassandra()) {
            return;
        }
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
            int aPort = 9042;
            if (port != null) {
                aPort = Integer.valueOf(port);
            }
            session = CqlSession
                    .builder()
                    .addContactPoint(new InetSocketAddress(host, aPort))
                    .withLocalDatacenter("datacenter1")
                    .build();
            this.session = session;
        }
        config.setSession(this.session);
        config.setConfigname(configname);
        this.keyspace = CqlIdentifier.fromInternal(nodeConf.getCassandraKeyspace());
        try {
            createAKeyspace(keyspace, 1);
            DataType filelocation = createAType();
            createTables(filelocation);
            IndexMapper indexMapper = new IndexMapperBuilder(session).build();
            FilesMapper filesMapper = new FilesMapperBuilder(session).build();
            filesDao = filesMapper.filesDao(keyspace);
            indexDao = indexMapper.indexDao(keyspace);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
    }

    public CqlSession getSession() {
        return session;
    }

    public FilesDao getFilesDao() {
        return filesDao;
    }

    public void setFilesDao(FilesDao filesDao) {
        this.filesDao = filesDao;
    }

    public IndexDao getIndexDao() {
        return indexDao;
    }

    public void setIndexDao(IndexDao indexDao) {
        this.indexDao = indexDao;
    }

    public void createAKeyspace(CqlIdentifier keyspaceName, int replicationFactor) {
        CreateKeyspace create = createKeyspace(keyspaceName)
                .ifNotExists()
                .withSimpleStrategy(replicationFactor);

        session.execute(create.build());
        session.execute("use " + keyspaceName + ";");
    }

    public DataType createAType() {
        CreateType create = createType(filelocationq)
                .ifNotExists()
                .withField(nodeq, DataTypes.TEXT)
                .withField(filenameq, DataTypes.TEXT);
        session.execute(create.build());
        //if (true) return null;
        codecRegistry = (MutableCodecRegistry) session.getContext().getCodecRegistry();             
        Optional<UserDefinedType> filelocationType = session.getMetadata().getKeyspace(keyspace).get().getUserDefinedType(filelocationq);
        this.filelocationTypeCodec = codecRegistry.codecFor(filelocationType.get());
        FilelocationCodec myJsonCodec = new FilelocationCodec(filelocationTypeCodec, FileLocation.class);

        codecRegistry.register(myJsonCodec);
        /*
              JsonCodec<FileLocation> myJsonCodec = new JsonCodec<>(FileLocation.class);
              CodecRegistry myCodecRegistry = session.getCluster().getConfiguration().getCodecRegistry();
              myCodecRegistry.register(myJsonCodec);
         */

        return null;
    }

    public void createTables(DataType filelocation) {
        CreateTable create = createTable(TABLE_FILES_NAME)
                .ifNotExists()
                .withPartitionKey(filenameq, DataTypes.TEXT)
                .withColumn(md5q, DataTypes.TEXT);
        try {
            session.execute(create.build());
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        CreateTable create2 = createTable(TABLE_INDEXFILES_NAME)
                .ifNotExists()
                .withPartitionKey(md5q, DataTypes.TEXT)
                .withColumn(indexedq, DataTypes.BOOLEAN)
                .withColumn(timestampq, DataTypes.TEXT)
                .withColumn(timeindexq, DataTypes.TEXT)
                .withColumn(timeclassq, DataTypes.TEXT)
                .withColumn(classificationq, DataTypes.TEXT)
                .withColumn(convertswq, DataTypes.TEXT)
                .withColumn(converttimeq, DataTypes.TEXT)
	        .withColumn(failedq, DataTypes.INT)
                .withColumn(failedreasonq, DataTypes.TEXT)
                .withColumn(timeoutreasonq, DataTypes.TEXT)
                .withColumn(noindexreasonq, DataTypes.TEXT)
                .withColumn(languageq, DataTypes.TEXT)
                .withColumn(isbnq, DataTypes.TEXT)
                .withColumn(createdq, DataTypes.TEXT)
                .withColumn(checkedq, DataTypes.TEXT)
                .withColumn(nodeq, DataTypes.TEXT)
                .withColumn(filelocationsq, DataTypes.setOf(udt(filelocationq), true));  

        session.execute(create2.build());
    }
    public void put(IndexFiles ifile) throws Exception {
        //InsertInto insert = insertInto(TABLE_INDEXFILES_NAME);
        UpdateStart updateStart = update(TABLE_INDEXFILES_NAME);
        UpdateWithAssignments updatewa = null;
        if (ifile.getFilelocations() != null) {
            //log.info("fls " + ifile.getFilelocations());
            updatewa = updateStart.setColumn(filelocationsq, literal(ifile.getFilelocations(), codecRegistry));
            //u = update.appendSetElement(filelocationq, literal(ifile.getFilelocations(), codecRegistry));
            /*
            for (FileLocation filelocation : ifile.getFilelocations()) {
                u = update.appendSetElement(filelocationq, literal(filelocation, codecRegistry));
            }
            */
        }
        if (ifile.getIndexed() != null) {
            updatewa = updatewa.setColumn(indexedq, literal(ifile.getIndexed()));
        }
        if (ifile.getTimestamp() != null) {
            updatewa = updatewa.setColumn(timestampq, literal(ifile.getTimestamp()));
        }
        if (ifile.getTimeindex() != null) {
            updatewa = updatewa.setColumn(timeindexq, literal(ifile.getTimeindex()));
        }
        if (ifile.getTimeclass() != null) {
            updatewa = updatewa.setColumn(timeclassq, literal(ifile.getTimeclass()));
        }
        if (ifile.getClassification() != null) {
            updatewa = updatewa.setColumn(classificationq, literal(ifile.getClassification()));
        }
        if (ifile.getMimetype() != null) {
            updatewa = updatewa.setColumn(mimetypeq, literal(ifile.getMimetype()));
        }
        if (ifile.getSize() != null) {
            updatewa = updatewa.setColumn(sizeq, literal(ifile.getSize()));
        }
        if (ifile.getConvertsize() != null) {
            updatewa = updatewa.setColumn(convertsizeq, literal(ifile.getConvertsize()));
        }
        if (ifile.getConvertsw() != null) {
            updatewa = updatewa.setColumn(convertswq, literal(ifile.getConvertsw()));
        }
        if (ifile.getConverttime() != null) {
            updatewa = updatewa.setColumn(converttimeq, literal(ifile.getConverttime()));
        }
        if (ifile.getFailed() != null) {
            updatewa = updatewa.setColumn(failedq, literal(ifile.getFailed()));
        }
        if (ifile.getFailedreason() != null) {
            updatewa = updatewa.setColumn(failedreasonq, literal(ifile.getFailedreason()));
        }
        if (ifile.getTimeoutreason() != null) {
            updatewa = updatewa.setColumn(timeoutreasonq, literal(ifile.getTimeoutreason()));
        }
        if (ifile.getNoindexreason() != null) {
            updatewa = updatewa.setColumn(noindexreasonq, literal(ifile.getNoindexreason()));
        }
        if (ifile.getLanguage() != null) {
            updatewa = updatewa.setColumn(languageq, literal(ifile.getLanguage()));
        }
        if (ifile.getIsbn() != null) {
            updatewa = updatewa.setColumn(isbnq, literal(ifile.getIsbn()));
        }
        if (ifile.getCreated() != null) {
            updatewa = updatewa.setColumn(createdq, literal(ifile.getCreated()));
        }
        if (ifile.getChecked() != null) {
            updatewa = updatewa.setColumn(checkedq, literal(ifile.getChecked()));
        }
        Update update = updatewa
                .whereColumn(md5q)
                .isEqualTo(literal(ifile.getMd5()));

        session.execute(update.build());
        put(ifile.getMd5(), ifile.getFilelocations());

    }

    public void put(String md5, Set<FileLocation> files) throws Exception {
        //HTable /*Interface*/ filesTable = new HTable(conf, "index");
        for (FileLocation file : files) {
            String filename = getFile(file);
            Update update = update(TABLE_FILES_NAME)
             .setColumn(md5q, literal(md5))
             .whereColumn(filenameq)
             .isEqualTo(literal(filename));
            session.execute(update.build());
        }
    }

    public IndexFiles get(Row row) {
        String md5 = row.getString(md5q);
        IndexFiles ifile = new IndexFiles(md5);
        //ifile.setMd5(bytesToString(index.getValue(indexcf, md5q)));
        ifile.setIndexed(row.getBoolean(indexedq));
        ifile.setTimeindex(row.getString(timeindexq));
        ifile.setTimestamp(row.getString(timestampq));
        ifile.setTimeclass(row.getString(timeclassq));
        ifile.setClassification(row.getString(classificationq));
        ifile.setMimetype(row.getString(mimetypeq));
        ifile.setSize(row.getInt(sizeq));
        ifile.setConvertsize(row.getInt(convertsizeq));
        ifile.setConvertsw(row.getString(convertswq));
        ifile.setConverttime(row.getString(converttimeq));
        ifile.setFailed(row.getInt(failedq));
        ifile.setFailedreason(row.getString(failedreasonq));
        ifile.setTimeoutreason(row.getString(timeoutreasonq));
        ifile.setNoindexreason(row.getString(noindexreasonq));
        ifile.setLanguage(row.getString(languageq));
        ifile.setIsbn(row.getString(isbnq));
        ifile.setCreated(row.getString(createdq));
        ifile.setChecked(row.getString(checkedq));
        ifile.setFilelocations(row.getSet(filelocationsq, FileLocation.class));
        ifile.setUnchanged();
        return ifile;
    }

    public Files getFiles(Row row) {
        String md5 = row.getString(md5q);
        Files ifile = new Files();
        //ifile.setMd5(bytesToString(index.getValue(indexcf, md5q)));
        ifile.setFilename(row.getString(filenameq));
        ifile.setMd5(row.getString(md5q));
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
        Select select = selectFrom(TABLE_INDEXFILES_NAME)
                .all()
                .whereColumn(md5q)
                .isEqualTo(literal(md5));
        ResultSet resultSet = session.execute(select.build());
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
        Select select = selectFrom(TABLE_FILES_NAME)
                .column(md5q)
                .whereColumn(filenameq)
                .isEqualTo(literal(name));
        ResultSet resultSet = session.execute(select.build());
        for (Row row : resultSet) {
            return row.getString(md5q);
        }
        return null;
    }

    public Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
        Set<FileLocation> flset = new HashSet<>();
        Select select = selectFrom(TABLE_FILES_NAME)
                .column(filenameq)
                .whereColumn(md5q)
                .isEqualTo(literal(md5))
                .allowFiltering();
        ResultSet resultSet = session.execute(select.build());
        for (Row row : resultSet) {
            FileLocation fl = getFileLocation(row.getString(filenameq));
            if (fl != null) {
                flset.add(fl);
            }
        }
        return flset;
    }

    public List<IndexFiles> getAll() throws Exception {
        Select select = selectFrom(TABLE_INDEXFILES_NAME).all();
        ResultSet resultSet = session.execute(select.build());	
        List<IndexFiles> retlist = new ArrayList<>();
        for (Row row : resultSet) {
            retlist.add(get(row));
        }
        return retlist;
    }

    public List<Files> getAllFiles() throws Exception {
        Select select = selectFrom(TABLE_FILES_NAME).all();
        ResultSet resultSet = session.execute(select.build());   
        List<Files> retlist = new ArrayList<>();
        for (Row row : resultSet) {
            retlist.add(getFiles(row));
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
        Select select = selectFrom(TABLE_INDEXFILES_NAME)
                .column(md5q);
        ResultSet resultSet = session.execute(select.build());
        for (Row row : resultSet.all()) {
            String md5 = row.getString(md5q);
            md5s.add(md5);
        }
        return md5s;
    }

    public Set<String> getLanguages() throws Exception {
        Select select = selectFrom(TABLE_INDEXFILES_NAME)
                .column(languageq);
        ResultSet resultSet = session.execute(select.build());
        Set<String> languages = new HashSet<>();
        for (Row row : resultSet.all()) {
            String lang = row.getString("language");
            languages.add(lang);
        }
        return languages;
    }

    public void delete(IndexFiles index) throws Exception {
        Delete delete = deleteFrom(TABLE_INDEXFILES_NAME)
                .whereColumn(md5q)
                .isEqualTo(literal(index.getMd5()));
         session.execute(delete.build());

        Set<FileLocation> curfls = getFilelocationsByMd5(index.getMd5());
        //curfls.removeAll(index.getFilelocations());
        //System.out.println("curfls " + curfls.size());

        // delete the files no longer associated to the md5
        for (FileLocation fl : curfls) {
            String name = fl.toString();
            log.info("Cassandra delete {}", name);
            Delete delete2 = deleteFrom(TABLE_FILES_NAME)
                    .whereColumn(filenameq)
                    .isEqualTo(literal(name));
            session.execute(delete2.build());
        }
    }

    public void delete(Files filename) throws Exception {
        deleteFile(filename.getFilename());
    }

    public void deleteFile(String filename) throws Exception {
        log.info("Cassandra delete {}", filename);
        Delete delete = deleteFrom(TABLE_FILES_NAME)
                .whereColumn(filenameq)
                .isEqualTo(literal(filename));
        session.execute(delete.build());
    }

    public void destroy() throws Exception {
        config.getSession().close();
    }

    public void clear(CqlIdentifier tableFilesName) {
        Truncate truncate = truncate(keyspace, tableFilesName);
        session.execute(truncate.build());

    }

    public void drop(CqlIdentifier keyspaceName) {
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
        drop(keyspace);
    }
}

