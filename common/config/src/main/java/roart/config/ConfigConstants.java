package roart.config;

public class ConfigConstants {

    @Deprecated
    public static final String FS = "fs";
    @Deprecated
    public static final String LOCAL = "local";
    @Deprecated
    public static final String HADOOP = "hadoop";
    @Deprecated
   public static final String SWIFT = "swift";
    @Deprecated
    public static final String INDEX = "index";
    public static final String SEARCHENGINESOLR = "searchengine.solr[@enable]";
    public static final String SEARCHENGINELUCENE = "searchengine.lucene[@enable]";
    public static final String SEARCHENGINEELASTIC = "searchengine.elastic[@enable]";
    @Deprecated
    public static final String DB = "db";
    public static final String LOCALHOST = "localhost";
    public static final String DATABASEHBASE = "database.hbase[@enable]";
    public static final String DATABASEHIBERNATE = "database.hibernate[@enable]";
    public static final String DATABASEHIBERNATEH2DIR = "database.hibernate.h2dir";
    public static final String NODECLASSIFY = "node.classify[@enable]";
    public static final String MACHINELEARNINGOPENNLP = "machinelearning.opennlp[@enable]";
    public static final String GUIDOWNLOADER = "gui.downloader[@enable]";
    public static final String GUIAUTHENTICATE = "gui.authenticate[@enable]";
    public static final String INDEXFAILEDLIMIT = "index.failedlimit";
    public static final String MACHINELEARNINGMAHOUT = "machinelearning.mahout[@enable]";
    public static final String MACHINELEARNINGMAHOUTMAHOUTBASEPATH = "machinelearning.mahout.mahoutbasepath";
    public static final String MACHINELEARNINGMAHOUTMAHOUTMODELPATH = "machinelearning.mahout.mahoutmodelpath";
    public static final String MACHINELEARNINGMAHOUTMAHOUTLABELINDEXFILEPATH = "machinelearning.mahout.mahoutlabelindexfilepath";
    public static final String MACHINELEARNINGMAHOUTMAHOUTDICTIONARYPATH = "machinelearning.mahout.mahoutdictionarypath";
    public static final String MACHINELEARNINGMAHOUTMAHOUTDOCUMENTFREQUENCYPATH = "machinelearning.mahout.mahoutdocumentfrequencypath";
    public static final String MACHINELEARNINGMAHOUTMAHOUTALGORITHM = "machinelearning.mahout.mahoutalgorithm";
    public static final String MACHINELEARNINGMAHOUTMAHOUTCONFFS = "machinelearning.mahout.mahoutconffs";
    public static final String MACHINELEARNINGMAHOUTSPARK = "machinelearning.mahoutspark[@enable]";
    public static final String MACHINELEARNINGMAHOUTSPARKMAHOUTSPARKMASTER = "machinelearning.mahoutspark.mahoutsparkmaster";
    public static final String MACHINELEARNINGMAHOUTSPARKMAHOUTBASEPATH = "machinelearning.mahoutspark.mahoutbasepath";
    public static final String MACHINELEARNINGMAHOUTSPARKMAHOUTMODELPATH = "machinelearning.mahoutspark.mahoutmodelpath";
    public static final String MACHINELEARNINGMAHOUTSPARKMAHOUTLABELINDEXFILEPATH = "machinelearning.mahoutspark.mahoutlabelindexfilepath";
    public static final String MACHINELEARNINGMAHOUTSPARKMAHOUTDICTIONARYPATH = "machinelearning.mahoutspark.mahoutdictionarypath";
    public static final String MACHINELEARNINGMAHOUTSPARKMAHOUTDOCUMENTFREQUENCYPATH = "machinelearning.mahoutspark.mahoutdocumentfrequencypath";
    public static final String MACHINELEARNINGMAHOUTSPARKMAHOUTCONFFS = "machinelearning.mahoutspark.mahoutconffs";
    public static final String MACHINELEARNINGMAHOUTSPARKMAHOUTALGORITHM = "machinelearning.mahoutspark.mahoutalgorithm";
    public static final String BAYES = "bayes";
    public static final String CBAYES = "cbayes";
    public static final String MACHINELEARNINGSPARKML = "machinelearning.sparkml[@enable]";
    public static final String MACHINELEARNINGSPARKMLSPARKMLBASEPATH = "machinelearning.sparkml.sparkmlbasepath";
    public static final String MACHINELEARNINGSPARKMLSPARKMLMODELPATH = "machinelearning.sparkml.sparkmlmodelpath";
    public static final String MACHINELEARNINGSPARKMLSPARKMLLABELINDEXPATH = "machinelearning.sparkml.sparkmllabelindexpath";
    public static final String MACHINELEARNINGSPARKMLSPARKMASTER = "machinelearning.sparkml.sparkmaster";
    public static final String MACHINELEARNINGOPENNLPOPENNLPMODELPATH = "machinelearning.opennlp.opennlpmodelpath";
    public static final String SEARCHENGINESOLRSOLRURL = "searchengine.solr.solrurl";
    public static final String SEARCHENGINEELASTICELASTICHOST = "searchengine.elastic.elastichost";
    public static final String SEARCHENGINEELASTICELASTICPORT = "searchengine.elastic.elasticport";
    public static final String FILESYSTEMHDFS = "filesystem.hdfs[@enable]";
    public static final String FILESYSTEMHDFSHDFSCONFFS = "filesystem.hdfs.hdfsconffs";
    public static final String FILESYSTEMSWIFT = "filesystem.swift[@enable]";
    public static final String FILESYSTEMSWIFTSWIFTCONFURL = "filesystem.swift.swiftconfurl";
    public static final String FILESYSTEMSWIFTSWIFTCONFUSER = "filesystem.swift.swiftconfuser";
    public static final String FILESYSTEMSWIFTSWIFTCONFKEY = "filesystem.swift.swiftconfkey";
    public static final String FILESYSTEMSWIFTSWIFTCONFCONTAINER = "filesystem.swift.swiftconfcontainer";
    public static final String DATABASEHBASEHBASEQUORUM = "database.hbase.hbasequorum";
    public static final String DATABASEHBASEHBASEPORT = "database.hbase.hbaseport";
    public static final String DATABASEHBASEHBASEMASTER = "database.hbase.hbasemaster";
    public static final String NODE = "node";
    public static final String NODENODENAME = "node.nodename";
    public static final String FSDATADIR = "fs.datadir";
    public static final String FSHOMEDIR = "fs.homedir";
    public static final String FSLOGDIR = "fs.logdir";
    public static final String FSDIRLIST = "fs.dirlist";
    public static final String FSDIRLISTNOT = "fs.dirlistnot";
    public static final String INDEXREINDEXLIMIT = "index.reindexlimit";
    public static final String INDEXINDEXLIMIT = "index.indexlimit";
    public static final String CONVERSIONTIKATIMEOUT = "conversion.tikatimeout";
    public static final String CONVERSTIONOTHERTIMEOUT = "conversion.othertimeout";
    public static final String DATABASEDATANUCLEUS = "database.datanucleus[@enable]";
    public static final String SYNCHRONIZATIONZOOKEEPER = "synchronization.zookeeper";
	public static final String GUIHIGHLIGHTMLT = "gui.highlightmlt[@enable]";
	public static final String NODELANGUAGES = "node.languages";
	public static final String SEARCHENGINEMLTMLTCOUNT = "searchengine.mlt.mltcount";
    public static final String SEARCHENGINEMLTMLTMINDF = "searchengine.mlt.mltmindf";
    public static final String SEARCHENGINEMLTMLTMINTF = "searchengine.mlt.mltmintf";	
    public static final String SYNCHRONIZATIONDISTRIBUTEDLOCKMODEBIG = "synchronization.distributedlockmodebig[@enable]";
    @Deprecated
    public static final String SMALL = "small";
    @Deprecated
    public static final String BIG = "big";
    public static final String SYNCHRONIZATIONDISTRIBUTEDPROCESS = "synchronization.distributedprocess[@enable]";
    public static final String CONFIG = "config";
    
    public static final int DEFAULT_CONFIG_FAILEDLIMIT = 0;
    public static final int DEFAULT_CONFIG_TIKATIMEOUT = 600;
    public static final int DEFAULT_CONFIG_OTHERTIMEOUT = 600;
    public static final int DEFAULT_CONFIG_INDEXLIMIT = 0;
    public static final int DEFAULT_CONFIG_REINDEXLIMIT = 0;
    public static final int DEFAULT_CONFIG_MLTCOUNT = 10;
    public static final int DEFAULT_CONFIG_MLTMINDF = 5;
    public static final int DEFAULT_CONFIG_MLTMINTF = 2;
    
    public static final String PROPFILE = "aether.prop";
    public static final String CONFIGFILE = "aether.xml";
    public static final String SEARCHENGINELUCENELUCENEPATH = "searchengine.lucene.lucenepath";
    public static String[] indexvalues = { SEARCHENGINELUCENE, SEARCHENGINESOLR, SEARCHENGINEELASTIC };
    public static String[] dbvalues = { DATABASEHIBERNATE, DATABASEDATANUCLEUS, DATABASEHBASE };
    public static String[] classifyvalues = { MACHINELEARNINGMAHOUT, MACHINELEARNINGMAHOUTSPARK, MACHINELEARNINGSPARKML, MACHINELEARNINGOPENNLP };
}
