package roart.config;

import java.util.HashMap;
import java.util.Map;

import roart.common.config.ConfigConstants;
import roart.common.config.XMLType;
import roart.common.constants.Constants;

public class ConfigConstantMaps {
    public static final String MYCONVERSION = "[{\"name\":\"tika\",\"timeout\":600,\"mimetypes\":[],\"suffixes\":[]}]";
    public static Map<String, Class> map = new HashMap();

    public static void makeTypeMap() {
        if (!map.isEmpty()) {
            return;
        }

        map.put(ConfigConstants.FS, String.class);
        map.put(ConfigConstants.LOCAL, String.class);
        map.put(ConfigConstants.HADOOP, String.class);
        map.put(ConfigConstants.SWIFT, String.class);
        map.put(ConfigConstants.INDEX, String.class);
        map.put(ConfigConstants.SEARCHENGINESOLR, Boolean.class);
        map.put(ConfigConstants.SEARCHENGINESOLRINDEX, String.class);
        map.put(ConfigConstants.SEARCHENGINELUCENE, Boolean.class);
        map.put(ConfigConstants.SEARCHENGINELUCENEINDEX, String.class);
        map.put(ConfigConstants.SEARCHENGINEELASTIC, Boolean.class);
        map.put(ConfigConstants.SEARCHENGINEELASTICINDEX, String.class);
        map.put(ConfigConstants.DB, String.class);
        map.put(ConfigConstants.LOCALHOST, String.class);
        map.put(ConfigConstants.DATABASEHBASE, Boolean.class);
        map.put(ConfigConstants.DATABASECASSANDRA, Boolean.class);
        map.put(ConfigConstants.DATABASEDYNAMODB, Boolean.class);
        map.put(ConfigConstants.DATABASEHIBERNATE, Boolean.class);
        map.put(ConfigConstants.DATABASEHIBERNATEH2DIR, String.class);
        map.put(ConfigConstants.NODECLASSIFY, Boolean.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUT, Boolean.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARK, Boolean.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTSPARKMASTER, String.class);
        map.put(ConfigConstants.MACHINELEARNINGOPENNLP, Boolean.class);
        map.put(ConfigConstants.GUIDOWNLOADER, Boolean.class);
        map.put(ConfigConstants.GUIAUTHENTICATE, Boolean.class);
        map.put(ConfigConstants.INDEXFAILEDLIMIT, Integer.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTMODELPATH, String.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTLABELINDEXFILEPATH, String.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTDICTIONARYPATH, String.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTDOCUMENTFREQUENCYPATH, String.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTBASEPATH, String.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTALGORITHM, String.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTCONFFS, String.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTMODELPATH, String.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTLABELINDEXFILEPATH, String.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTDICTIONARYPATH, String.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTDOCUMENTFREQUENCYPATH, String.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTBASEPATH, String.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTALGORITHM, String.class);
        map.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTCONFFS, String.class);
        map.put(ConfigConstants.BAYES, String.class);
        map.put(ConfigConstants.CBAYES, String.class);
        map.put(ConfigConstants.MACHINELEARNINGSPARKML, Boolean.class);
        map.put(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLBASEPATH, String.class);
        map.put(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLMODELPATH, String.class);
        map.put(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLLABELINDEXPATH, String.class);
        map.put(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMASTER, String.class);
        map.put(ConfigConstants.MACHINELEARNINGOPENNLPOPENNLPMODELPATH, String.class);
        map.put(ConfigConstants.SEARCHENGINESOLRSOLRURL, String.class);
        map.put(ConfigConstants.SEARCHENGINEELASTICELASTICHOST, String.class);
        map.put(ConfigConstants.SEARCHENGINEELASTICELASTICPORT, String.class);
        map.put(ConfigConstants.FILESYSTEMHDFS, Boolean.class);
        map.put(ConfigConstants.FILESYSTEMHDFSHDFSCONFFS, String.class);
        map.put(ConfigConstants.FILESYSTEMSWIFT, Boolean.class);
        map.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFURL, String.class);
        map.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFUSER, String.class);
        map.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFKEY, String.class);
        map.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFCONTAINER, String.class);
        map.put(ConfigConstants.DATABASEHBASEHBASEQUORUM, String.class);
        map.put(ConfigConstants.DATABASEHBASEHBASEPORT, String.class);
        map.put(ConfigConstants.DATABASEHBASEHBASEMASTER, String.class);
        map.put(ConfigConstants.DATABASECASSANDRAHOST, String.class);
        map.put(ConfigConstants.DATABASECASSANDRATLS, Boolean.class);
        map.put(ConfigConstants.DATABASECASSANDRAPORT, String.class);
        map.put(ConfigConstants.DATABASECASSANDRATHRIFTPORT, String.class);
        map.put(ConfigConstants.DATABASECASSANDRATLSPORT, String.class);
        map.put(ConfigConstants.DATABASEDYNAMODBHOST, String.class);
        map.put(ConfigConstants.DATABASEDYNAMODBPORT, String.class);
        map.put(ConfigConstants.NODE, String.class);
        map.put(ConfigConstants.NODENODENAME, String.class);
        map.put(ConfigConstants.FSDATADIR, String.class);
        map.put(ConfigConstants.FSHOMEDIR, String.class);
        map.put(ConfigConstants.FSLOGDIR, String.class);
        map.put(ConfigConstants.FSDIRLIST, String.class);
        map.put(ConfigConstants.FSDIRLISTNOT, String.class);
        map.put(ConfigConstants.INDEXREINDEXLIMIT, Integer.class);
        map.put(ConfigConstants.INDEXINDEXLIMIT, Integer.class);
        map.put(ConfigConstants.CONVERSIONTIKATIMEOUT, Integer.class);
        map.put(ConfigConstants.CONVERSIONOTHERTIMEOUT, Integer.class);
        map.put(ConfigConstants.CONVERSION, String.class);
        map.put(ConfigConstants.DATABASEDATANUCLEUS, Boolean.class);
        map.put(ConfigConstants.SYNCHRONIZATIONZOOKEEPER, String.class);
        map.put(ConfigConstants.GUIHIGHLIGHTMLT, Boolean.class);
        map.put(ConfigConstants.NODELANGUAGES, String.class);
        map.put(ConfigConstants.SEARCHENGINEMLTMLTCOUNT, Integer.class);
        map.put(ConfigConstants.SEARCHENGINEMLTMLTMINDF, Integer.class);
        map.put(ConfigConstants.SEARCHENGINEMLTMLTMINTF, Integer.class);
        map.put(ConfigConstants.SYNCHRONIZATIONDISTRIBUTEDLOCKMODEBIG, Boolean.class);
        map.put(ConfigConstants.SMALL, Boolean.class);
        map.put(ConfigConstants.BIG, Boolean.class);
        map.put(ConfigConstants.SYNCHRONIZATIONDISTRIBUTEDPROCESS, Boolean.class);
        map.put(ConfigConstants.CONFIG, String.class);
        map.put(ConfigConstants.SEARCHENGINELUCENELUCENEPATH, String.class);
        map.put(ConfigConstants.INMEMORYSERVER, String.class);
        map.put(ConfigConstants.INMEMORYHAZELCAST, String.class);
        map.put(ConfigConstants.INMEMORYREDIS, String.class);
 }

    public static Map<String, Object> deflt = new HashMap();
    public static void makeDefaultMap() {
        if (!deflt.isEmpty()) {
            return;
        }

        deflt.put(ConfigConstants.FS, null);
        deflt.put(ConfigConstants.LOCAL, null);
        deflt.put(ConfigConstants.HADOOP, null);
        deflt.put(ConfigConstants.SWIFT, null);
        deflt.put(ConfigConstants.INDEX, null);
        deflt.put(ConfigConstants.SEARCHENGINESOLR, Boolean.FALSE);
        deflt.put(ConfigConstants.SEARCHENGINESOLRINDEX, "myindex");
        deflt.put(ConfigConstants.SEARCHENGINELUCENE, Boolean.TRUE);
        deflt.put(ConfigConstants.SEARCHENGINELUCENEINDEX, "myindex");
        deflt.put(ConfigConstants.SEARCHENGINEELASTIC, Boolean.FALSE);
        deflt.put(ConfigConstants.SEARCHENGINEELASTICINDEX, "myindex");
        deflt.put(ConfigConstants.DB, null);
        deflt.put(ConfigConstants.LOCALHOST, null);
        deflt.put(ConfigConstants.DATABASEHBASE, Boolean.FALSE);
        deflt.put(ConfigConstants.DATABASECASSANDRA, Boolean.FALSE);
        deflt.put(ConfigConstants.DATABASEDYNAMODB, Boolean.FALSE);
        deflt.put(ConfigConstants.DATABASEHIBERNATE, Boolean.TRUE);
        deflt.put(ConfigConstants.DATABASEHIBERNATEH2DIR, "");
        deflt.put(ConfigConstants.NODECLASSIFY, Boolean.FALSE);
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUT, Boolean.FALSE);
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARK, Boolean.FALSE);
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTSPARKMASTER, "spark://127.0.0.1:7077"); 
        deflt.put(ConfigConstants.MACHINELEARNINGOPENNLP, Boolean.FALSE);
        deflt.put(ConfigConstants.GUIDOWNLOADER, Boolean.TRUE);
        deflt.put(ConfigConstants.GUIAUTHENTICATE, Boolean.FALSE);
        deflt.put(ConfigConstants.INDEXFAILEDLIMIT, 0);
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTBASEPATH, null);
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTMODELPATH, "");
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTLABELINDEXFILEPATH, "");
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTDICTIONARYPATH, "");
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTDOCUMENTFREQUENCYPATH, "");
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTALGORITHM, "bayes");
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTCONFFS, "");
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTBASEPATH, null);
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTMODELPATH, "");
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTLABELINDEXFILEPATH, "");
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTDICTIONARYPATH, "");
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTDOCUMENTFREQUENCYPATH, "");
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTALGORITHM, "bayes");
        deflt.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTCONFFS, "");
        deflt.put(ConfigConstants.BAYES, "");
        deflt.put(ConfigConstants.CBAYES, "");
        deflt.put(ConfigConstants.MACHINELEARNINGSPARKML, Boolean.FALSE);
        deflt.put(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLBASEPATH, "");
        deflt.put(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLMODELPATH, "");
        deflt.put(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLLABELINDEXPATH, ""); 
        deflt.put(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMASTER, "");
        deflt.put(ConfigConstants.MACHINELEARNINGOPENNLPOPENNLPMODELPATH, ""); 
        deflt.put(ConfigConstants.SEARCHENGINESOLRSOLRURL, "");
        deflt.put(ConfigConstants.SEARCHENGINEELASTICELASTICHOST, "localhost");
        deflt.put(ConfigConstants.SEARCHENGINEELASTICELASTICPORT, "9300");
        deflt.put(ConfigConstants.FILESYSTEMHDFS, Boolean.FALSE);
        deflt.put(ConfigConstants.FILESYSTEMHDFSHDFSCONFFS, "");
        deflt.put(ConfigConstants.FILESYSTEMSWIFT, Boolean.FALSE);
        deflt.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFURL, "");
        deflt.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFUSER, "");
        deflt.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFKEY, "");
        deflt.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFCONTAINER, "");
        deflt.put(ConfigConstants.DATABASEHBASEHBASEQUORUM, "localhost");
        deflt.put(ConfigConstants.DATABASEHBASEHBASEPORT, "2181");
        deflt.put(ConfigConstants.DATABASEHBASEHBASEMASTER, "localhost:2181");
        deflt.put(ConfigConstants.DATABASECASSANDRAHOST, "");
        deflt.put(ConfigConstants.DATABASECASSANDRATLS, Boolean.FALSE);
        deflt.put(ConfigConstants.DATABASECASSANDRAPORT, "9042");
        deflt.put(ConfigConstants.DATABASECASSANDRATHRIFTPORT, "9160");
        deflt.put(ConfigConstants.DATABASECASSANDRATLSPORT, "9142");
        deflt.put(ConfigConstants.DATABASEDYNAMODBHOST, "");
        deflt.put(ConfigConstants.DATABASEDYNAMODBPORT, "8000");
        deflt.put(ConfigConstants.NODE, null);
        deflt.put(ConfigConstants.NODENODENAME, "localhost");
        deflt.put(ConfigConstants.FSDATADIR, "");
        deflt.put(ConfigConstants.FSHOMEDIR, "");
        deflt.put(ConfigConstants.FSLOGDIR, "");
        deflt.put(ConfigConstants.FSDIRLIST, "");
        deflt.put(ConfigConstants.FSDIRLISTNOT, "");
        deflt.put(ConfigConstants.INDEXREINDEXLIMIT, 0);
        deflt.put(ConfigConstants.INDEXINDEXLIMIT, 0);
        deflt.put(ConfigConstants.CONVERSIONTIKATIMEOUT, 600);
        deflt.put(ConfigConstants.CONVERSIONOTHERTIMEOUT, 600);
        deflt.put(ConfigConstants.CONVERSION, MYCONVERSION);
        deflt.put(ConfigConstants.DATABASEDATANUCLEUS, Boolean.FALSE);
        deflt.put(ConfigConstants.SYNCHRONIZATIONZOOKEEPER, "localhost:2181");
        deflt.put(ConfigConstants.GUIHIGHLIGHTMLT, Boolean.TRUE);
        deflt.put(ConfigConstants.NODELANGUAGES, "en");
        deflt.put(ConfigConstants.SEARCHENGINEMLTMLTCOUNT, 10);
        deflt.put(ConfigConstants.SEARCHENGINEMLTMLTMINDF, 5);
        deflt.put(ConfigConstants.SEARCHENGINEMLTMLTMINTF, 2);
        deflt.put(ConfigConstants.SYNCHRONIZATIONDISTRIBUTEDLOCKMODEBIG, Boolean.TRUE);
        deflt.put(ConfigConstants.SMALL, Boolean.FALSE);
        deflt.put(ConfigConstants.BIG, Boolean.FALSE);
        deflt.put(ConfigConstants.SYNCHRONIZATIONDISTRIBUTEDPROCESS, Boolean.FALSE);
        deflt.put(ConfigConstants.CONFIG, "");
        deflt.put(ConfigConstants.SEARCHENGINELUCENELUCENEPATH, "/tmp");
        deflt.put(ConfigConstants.INMEMORYSERVER, Constants.HAZELCAST);
        deflt.put(ConfigConstants.INMEMORYHAZELCAST, null);
        deflt.put(ConfigConstants.INMEMORYREDIS, "localhost");
}

    public static Map<String, String> text = new HashMap();

    public static void makeTextMap() {
        if (!text.isEmpty()) {
            return;
        }

        text.put(ConfigConstants.FS, "Filesystem");
        text.put(ConfigConstants.LOCAL, "");
        text.put(ConfigConstants.HADOOP, "");
        text.put(ConfigConstants.SWIFT, "");
        text.put(ConfigConstants.INDEX, "");
        text.put(ConfigConstants.SEARCHENGINESOLR, "Use Solr");
        text.put(ConfigConstants.SEARCHENGINESOLRINDEX, "Solr index");
        text.put(ConfigConstants.SEARCHENGINELUCENE, "Use Lucene");
        text.put(ConfigConstants.SEARCHENGINELUCENEINDEX, "Lucene index");
        text.put(ConfigConstants.SEARCHENGINEELASTIC, "Use Elastic");
        text.put(ConfigConstants.SEARCHENGINEELASTICINDEX, "Elastic index");
        text.put(ConfigConstants.DB, "Database");
        text.put(ConfigConstants.LOCALHOST, "");
        text.put(ConfigConstants.DATABASEHBASE, "");
        text.put(ConfigConstants.DATABASECASSANDRA, "");
        text.put(ConfigConstants.DATABASEDYNAMODB, "");
        text.put(ConfigConstants.DATABASEHIBERNATE, "");
        text.put(ConfigConstants.DATABASEHIBERNATE, "Hibernate H2 directory");
        text.put(ConfigConstants.NODECLASSIFY, "Use classifier ML");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUT, "");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARK, "");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTSPARKMASTER, "Mahout Spark master");
        text.put(ConfigConstants.MACHINELEARNINGOPENNLP, "Machine learning OpenNLP");
        text.put(ConfigConstants.GUIDOWNLOADER, "Use downloader");
        text.put(ConfigConstants.GUIAUTHENTICATE, "Use login");
        text.put(ConfigConstants.INDEXFAILEDLIMIT, "Limit for failed indexing per unit");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTBASEPATH, "Mahout base path");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTMODELPATH, "Mahout model path");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTLABELINDEXFILEPATH, "Mahout labelindex filepath");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTDICTIONARYPATH, "Mahout dictionary path");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTDOCUMENTFREQUENCYPATH, "Mahout document frequency path");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTALGORITHM, "Mahout algorithm");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTCONFFS, "Mahout conf fs");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTBASEPATH, "Mahout spark base path");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTMODELPATH, "Mahout spark model path");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTLABELINDEXFILEPATH, "Mahout spark labelindex filepath");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTDICTIONARYPATH, "Mahout spark dictionary path");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTDOCUMENTFREQUENCYPATH, "Mahout spark document frequency path");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTALGORITHM, "Mahout spark algorithm");
        text.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTCONFFS, "Mahout spark conf fs");
        text.put(ConfigConstants.BAYES, "");
        text.put(ConfigConstants.CBAYES, "");
        text.put(ConfigConstants.MACHINELEARNINGSPARKML, "Spark ML");
        text.put(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLBASEPATH, "Spark ML base path");
        text.put(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLMODELPATH, "Spark ML model path");
        text.put(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLLABELINDEXPATH, "Spark ML labelindex path");
        text.put(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMASTER, "Spark ML master");
        text.put(ConfigConstants.MACHINELEARNINGOPENNLPOPENNLPMODELPATH, "OpenNLP model path");
        text.put(ConfigConstants.SEARCHENGINESOLRSOLRURL, "Solr URL");
        text.put(ConfigConstants.SEARCHENGINEELASTICELASTICHOST, "Elastic host");
        text.put(ConfigConstants.SEARCHENGINEELASTICELASTICPORT, "Elastic port");
        text.put(ConfigConstants.FILESYSTEMHDFS, "Use HDFS");
        text.put(ConfigConstants.FILESYSTEMHDFSHDFSCONFFS, "HDFS fs path");
        text.put(ConfigConstants.FILESYSTEMSWIFT, "Use Swift");
        text.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFURL, "Swift url");
        text.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFUSER, "Swift user");
        text.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFKEY, "Swift key");
        text.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFCONTAINER, "Swift containter");
        text.put(ConfigConstants.DATABASEHBASEHBASEQUORUM, "HBase quorum");
        text.put(ConfigConstants.DATABASEHBASEHBASEPORT, "HBase port");
        text.put(ConfigConstants.DATABASEHBASEHBASEMASTER, "HBase master");
        text.put(ConfigConstants.DATABASECASSANDRAHOST, "Cassandra host");
        text.put(ConfigConstants.DATABASECASSANDRATLS, "Cassandra TLS enable");
        text.put(ConfigConstants.DATABASECASSANDRAPORT, "Cassandra port");
        text.put(ConfigConstants.DATABASECASSANDRATHRIFTPORT, "Cassandra Thrift port");
        text.put(ConfigConstants.DATABASECASSANDRATLSPORT, "Cassandra TLS port");
        text.put(ConfigConstants.DATABASEDYNAMODBHOST, "Dynamodb host");
        text.put(ConfigConstants.DATABASEDYNAMODBPORT, "Dynamodb port");
        text.put(ConfigConstants.NODE, "Node");
        text.put(ConfigConstants.NODENODENAME, "Nodename");
        text.put(ConfigConstants.FSDATADIR, "Filesystem data dir");
        text.put(ConfigConstants.FSHOMEDIR, "Filesystem home dir");
        text.put(ConfigConstants.FSLOGDIR, "Filesystem log dir");
        text.put(ConfigConstants.FSDIRLIST, "Filesystem dirs");
        text.put(ConfigConstants.FSDIRLISTNOT, "Filesystem exclude dirs");
        text.put(ConfigConstants.INDEXREINDEXLIMIT, "Reindex limit");
        text.put(ConfigConstants.INDEXINDEXLIMIT, "Index limit");
        text.put(ConfigConstants.CONVERSIONTIKATIMEOUT, "Tika timeout");
        text.put(ConfigConstants.CONVERSIONOTHERTIMEOUT, "Other conversion timeout");
        text.put(ConfigConstants.CONVERSION, "Conversion");
        text.put(ConfigConstants.DATABASEDATANUCLEUS, "Use datanucleus");
        text.put(ConfigConstants.SYNCHRONIZATIONZOOKEEPER, "Zookeeper connection");
        text.put(ConfigConstants.GUIHIGHLIGHTMLT, "Highlight MLT");
        text.put(ConfigConstants.NODELANGUAGES, "languages");
        text.put(ConfigConstants.SEARCHENGINEMLTMLTCOUNT, "MLT count");
        text.put(ConfigConstants.SEARCHENGINEMLTMLTMINDF, "Min DF");
        text.put(ConfigConstants.SEARCHENGINEMLTMLTMINTF, "Min TF");
        text.put(ConfigConstants.SYNCHRONIZATIONDISTRIBUTEDLOCKMODEBIG, "Distributed lock mode");
        text.put(ConfigConstants.SMALL, "Small lock mode");
        text.put(ConfigConstants.BIG, "Big lock mode");
        text.put(ConfigConstants.SYNCHRONIZATIONDISTRIBUTEDPROCESS, "Distributed processing");
        text.put(ConfigConstants.CONFIG, "");
        text.put(ConfigConstants.SEARCHENGINELUCENELUCENEPATH, "Lucene path");
        text.put(ConfigConstants.INMEMORYSERVER, "In memory server");
        text.put(ConfigConstants.INMEMORYHAZELCAST, "In memory Hazelcast connection");
        text.put(ConfigConstants.INMEMORYREDIS, "In memory Redis connection");
}
    public static Map<String, XMLType> mymap = new HashMap<>();
    public static void makeMap() {
        mymap.put(ConfigConstants.FS, new XMLType( String.class, null, "Filesystem"));
        mymap.put(ConfigConstants.LOCAL, new XMLType( String.class, null, ""));
        mymap.put(ConfigConstants.HADOOP, new XMLType( String.class, null, ""));
        mymap.put(ConfigConstants.SWIFT, new XMLType( String.class, null, ""));
        mymap.put(ConfigConstants.INDEX, new XMLType( String.class, null, ""));
        mymap.put(ConfigConstants.SEARCHENGINESOLR, new XMLType( Boolean.class, Boolean.FALSE, "Use Solr"));
        mymap.put(ConfigConstants.SEARCHENGINESOLRINDEX, new XMLType( String.class, "myindex", "Solr index"));
        mymap.put(ConfigConstants.SEARCHENGINELUCENE, new XMLType( Boolean.class, Boolean.TRUE, "Use Lucene"));
        mymap.put(ConfigConstants.SEARCHENGINELUCENEINDEX, new XMLType( String.class, "myindex", "Lucene index"));
        mymap.put(ConfigConstants.SEARCHENGINEELASTIC, new XMLType( Boolean.class, Boolean.FALSE, "Use Elastic"));
        mymap.put(ConfigConstants.SEARCHENGINEELASTICINDEX, new XMLType( String.class, "myindex", "Elastic index"));
        mymap.put(ConfigConstants.DB, new XMLType( String.class, null, "Database"));
        mymap.put(ConfigConstants.LOCALHOST, new XMLType( String.class, null, ""));
        mymap.put(ConfigConstants.DATABASEHBASE, new XMLType( Boolean.class, Boolean.FALSE, ""));
        mymap.put(ConfigConstants.DATABASECASSANDRA, new XMLType( Boolean.class, Boolean.FALSE, ""));
        mymap.put(ConfigConstants.DATABASEDYNAMODB, new XMLType( Boolean.class, Boolean.FALSE, ""));
        mymap.put(ConfigConstants.DATABASEHIBERNATE, new XMLType( Boolean.class, Boolean.TRUE, "Hibernate H2 directory"));
        mymap.put(ConfigConstants.DATABASEHIBERNATEH2DIR, new XMLType( String.class, "", null));
        mymap.put(ConfigConstants.NODECLASSIFY, new XMLType( Boolean.class, Boolean.FALSE, "Use classifier ML"));
        mymap.put(ConfigConstants.MACHINELEARNINGOPENNLP, new XMLType( Boolean.class, Boolean.FALSE, "Machine learning OpenNLP"));
        mymap.put(ConfigConstants.GUIDOWNLOADER, new XMLType( Boolean.class, Boolean.TRUE, "Use downloader"));
        mymap.put(ConfigConstants.GUIAUTHENTICATE, new XMLType( Boolean.class, Boolean.FALSE, "Use login"));
        mymap.put(ConfigConstants.INDEXFAILEDLIMIT, new XMLType( Integer.class, 0, "Limit for failed indexing per unit"));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUT, new XMLType( Boolean.class, Boolean.FALSE, ""));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTBASEPATH, new XMLType( String.class, null, "Mahout base path"));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTMODELPATH, new XMLType( String.class, "", "Mahout model path"));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTLABELINDEXFILEPATH, new XMLType( String.class, "", "Mahout labelindex filepath"));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTDICTIONARYPATH, new XMLType( String.class, "", "Mahout dictionary path"));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTDOCUMENTFREQUENCYPATH, new XMLType( String.class, "", "Mahout document frequency path"));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTALGORITHM, new XMLType( String.class, "bayes", "Mahout algorithm"));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTCONFFS, new XMLType( String.class, "", "Mahout conf fs"));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARK, new XMLType( Boolean.class, Boolean.FALSE, ""));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTSPARKMASTER, new XMLType( String.class, "spark://127.0.0.1:7077", "Mahout Spark master"));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTBASEPATH, new XMLType( String.class, null, "Mahout spark base path"));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTMODELPATH, new XMLType( String.class, "", "Mahout spark model path"));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTLABELINDEXFILEPATH, new XMLType( String.class, "", "Mahout spark labelindex filepath"));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTDICTIONARYPATH, new XMLType( String.class, "", "Mahout spark dictionary path"));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTDOCUMENTFREQUENCYPATH, new XMLType( String.class, "", "Mahout spark document frequency path"));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTCONFFS, new XMLType( String.class, "", "Mahout spark conf fs"));
        mymap.put(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTALGORITHM, new XMLType( String.class, "bayes", "Mahout spark algorithm"));
        mymap.put(ConfigConstants.BAYES, new XMLType( String.class, "", ""));
        mymap.put(ConfigConstants.CBAYES, new XMLType( String.class, "", ""));
        mymap.put(ConfigConstants.MACHINELEARNINGSPARKML, new XMLType( Boolean.class, Boolean.FALSE, "Spark ML"));
        mymap.put(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLBASEPATH, new XMLType( String.class, "", "Spark ML base path"));
        mymap.put(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLMODELPATH, new XMLType( String.class, "", "Spark ML model path"));
        mymap.put(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLLABELINDEXPATH, new XMLType( String.class, "", "Spark ML labelindex path"));
        mymap.put(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMASTER, new XMLType( String.class, "", "Spark ML master"));
        mymap.put(ConfigConstants.MACHINELEARNINGOPENNLPOPENNLPMODELPATH, new XMLType( String.class, "", "OpenNLP model path"));
        mymap.put(ConfigConstants.SEARCHENGINESOLRSOLRURL, new XMLType( String.class, "", "Solr URL"));
        mymap.put(ConfigConstants.SEARCHENGINEELASTICELASTICHOST, new XMLType( String.class, "localhost", "Elastic host"));
        mymap.put(ConfigConstants.SEARCHENGINEELASTICELASTICPORT, new XMLType( String.class, "9300", "Elastic port"));
        mymap.put(ConfigConstants.FILESYSTEMHDFS, new XMLType( Boolean.class, Boolean.FALSE, "Use HDFS"));
        mymap.put(ConfigConstants.FILESYSTEMHDFSHDFSCONFFS, new XMLType( String.class, "", "HDFS fs path"));
        mymap.put(ConfigConstants.FILESYSTEMSWIFT, new XMLType( Boolean.class, Boolean.FALSE, "Use Swift"));
        mymap.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFURL, new XMLType( String.class, "", "Swift url"));
        mymap.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFUSER, new XMLType( String.class, "", "Swift user"));
        mymap.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFKEY, new XMLType( String.class, "", "Swift key"));
        mymap.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFCONTAINER, new XMLType( String.class, "", "Swift containter"));
        mymap.put(ConfigConstants.DATABASEHBASEHBASEQUORUM, new XMLType( String.class, "localhost", "HBase quorum"));
        mymap.put(ConfigConstants.DATABASECASSANDRAHOST, new XMLType( String.class, "", "Cassandra host"));
        mymap.put(ConfigConstants.DATABASEHBASEHBASEPORT, new XMLType( String.class, "2181", "HBase port"));
        mymap.put(ConfigConstants.DATABASEHBASEHBASEMASTER, new XMLType( String.class, "localhost:2181", "HBase master"));
        mymap.put(ConfigConstants.DATABASECASSANDRATLS, new XMLType( Boolean.class, Boolean.FALSE, "Cassandra TLS enable"));
        mymap.put(ConfigConstants.DATABASECASSANDRAPORT, new XMLType( String.class, "9042", "Cassandra port"));
        mymap.put(ConfigConstants.DATABASECASSANDRATHRIFTPORT, new XMLType( String.class, "9160", "Cassandra Thrift port"));
        mymap.put(ConfigConstants.DATABASECASSANDRATLSPORT, new XMLType( String.class, "9142", "Cassandra TLS port"));
        mymap.put(ConfigConstants.DATABASEDYNAMODBHOST, new XMLType( String.class, "", "Dynamodb host"));
        mymap.put(ConfigConstants.DATABASEDYNAMODBPORT, new XMLType( String.class, "8000", "Dynamodb port"));
        mymap.put(ConfigConstants.NODE, new XMLType( String.class, null, "Node"));
        mymap.put(ConfigConstants.NODENODENAME, new XMLType( String.class, "localhost", "Nodename"));
        mymap.put(ConfigConstants.FSDATADIR, new XMLType( String.class, "", "Filesystem data dir"));
        mymap.put(ConfigConstants.FSHOMEDIR, new XMLType( String.class, "", "Filesystem home dir"));
        mymap.put(ConfigConstants.FSLOGDIR, new XMLType( String.class, "", "Filesystem log dir"));
        mymap.put(ConfigConstants.FSDIRLIST, new XMLType( String.class, "", "Filesystem dirs"));
        mymap.put(ConfigConstants.FSDIRLISTNOT, new XMLType( String.class, "", "Filesystem exclude dirs"));
        mymap.put(ConfigConstants.INDEXREINDEXLIMIT, new XMLType( Integer.class, 0, "Reindex limit"));
        mymap.put(ConfigConstants.INDEXINDEXLIMIT, new XMLType( Integer.class, 0, "Index limit"));
        mymap.put(ConfigConstants.CONVERSIONTIKATIMEOUT, new XMLType( Integer.class, 600, "Tika timeout"));
        mymap.put(ConfigConstants.CONVERSIONOTHERTIMEOUT, new XMLType( Integer.class, 600, "Other conversion timeout"));
        mymap.put(ConfigConstants.CONVERSION, new XMLType( String.class, MYCONVERSION, "Conversion"));
        mymap.put(ConfigConstants.DATABASEDATANUCLEUS, new XMLType( Boolean.class, Boolean.FALSE, "Use datanucleus"));
        mymap.put(ConfigConstants.SYNCHRONIZATIONZOOKEEPER, new XMLType( String.class, null, "Use zookeeper"));
        mymap.put(ConfigConstants.GUIHIGHLIGHTMLT, new XMLType( Boolean.class, Boolean.TRUE, "Highlight MLT"));
        mymap.put(ConfigConstants.NODELANGUAGES, new XMLType( String.class, "en", "languages"));
        mymap.put(ConfigConstants.SEARCHENGINEMLTMLTCOUNT, new XMLType( Integer.class, 10, "MLT count"));
        mymap.put(ConfigConstants.SEARCHENGINEMLTMLTMINDF, new XMLType( Integer.class, 5, "Min DF"));
        mymap.put(ConfigConstants.SEARCHENGINEMLTMLTMINTF, new XMLType( Integer.class, 2, "Min TF"));
        mymap.put(ConfigConstants.SYNCHRONIZATIONZOOKEEPER, new XMLType( String.class, "localhost:2181", "Zookeeper connection"));
        mymap.put(ConfigConstants.SYNCHRONIZATIONDISTRIBUTEDLOCKMODEBIG, new XMLType( Boolean.class, Boolean.TRUE, "Distributed lock mode"));
        mymap.put(ConfigConstants.SMALL, new XMLType( Boolean.class, Boolean.FALSE, "Small lock mode"));
        mymap.put(ConfigConstants.BIG, new XMLType( Boolean.class, Boolean.FALSE, "Big lock mode"));
        mymap.put(ConfigConstants.SYNCHRONIZATIONDISTRIBUTEDPROCESS, new XMLType( Boolean.class, Boolean.FALSE, "Distributed processing"));
        mymap.put(ConfigConstants.CONFIG, new XMLType( String.class, "", ""));
        mymap.put(ConfigConstants.CONFIGFILE, new XMLType(null, null, null));
        mymap.put(ConfigConstants.SEARCHENGINELUCENELUCENEPATH, new XMLType( String.class, "/tmp", "Lucene path"));
        mymap.put(ConfigConstants.INMEMORYSERVER, new XMLType( String.class, Constants.HAZELCAST, "In memory server"));
        mymap.put(ConfigConstants.INMEMORYHAZELCAST, new XMLType( String.class, null, "In memory Hazelcast connection"));
        mymap.put(ConfigConstants.INMEMORYREDIS, new XMLType( String.class, "localhost", "In memory Redis connection"));
}
}

