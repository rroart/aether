package roart.config;

import roart.service.ControlService;

public class ConfigConstants {

    public static final String FS = "fs";
    public static final String LOCAL = "local";
    public static final String HADOOP = "hadoop";
    public static final String INDEX = "index";
    public static final String SOLR = "solr";
    public static final String LUCENE = "lucene";
    public static final String DB = "db";
    public static final String LOCALHOST = "localhost";
    public static final String HBASE = "hbase";
    public static final String HIBERNATE = "hibernate";
    public static final String CLASSIFY = "classify";
    public static final String MAHOUT = "mahout";
    public static final String MAHOUTSPARK = "mahoutspark";
    public static final String MAHOUTSPARKMASTER = "mahoutsparkmaster";
    public static final String OPENNLP = "opennlp";
    public static final String DOWNLOADER = "downloader";
    public static final String AUTHENTICATE = "authenticate";
    public static final String FAILEDLIMIT = "failedlimit";
    public static final String MAHOUTMODELPATH = "mahaoutmodelpath";
    public static final String MAHOUTLABELINDEXFILEPATH = "mahoutlabelindexfilepath";
    public static final String MAHOUTDICTIONARYPATH = "mahoutdictionarypath";
    public static final String MAHOUTDOCUMENTFREQUENCYPATH = "mahoutdocumentfrequencypath";
    public static final String MAHOUTALGORITHM = "mahoutalgorithm";
    public static final String MAHOUTCONFFS = "mahoutconffs";
    public static final String BAYES = "bayes";
    public static final String CBAYES = "cbayes";
    public static final String OPENNLPMODELPATH = "opennlpmodelpath";
    public static final String SOLRURL = "solrurl";
    public static final String HDFSCONFFS = "hdfsconffs";
    public static final String SWIFTCONFURL = "swiftconfurl";
    public static final String SWIFTCONFUSER = "swiftconfuser";
    public static final String SWIFTCONFKEY = "swiftconfkey";
    public static final String SWIFTCONFCONTAINER = "swiftconfcontainer";
    public static final String HBASEQUORUM = "hbasequorum";
    public static final String HBASEPORT = "hbaseport";
    public static final String HBASEMASTER = "hbasemaster";
    public static final String NODENAME = "nodename";
    public static final String DIRLIST = "dirlist";
    public static final String DIRLISTNOT = "dirlistnot";
    public static final String REINDEXLIMIT = "reindexlimit";
    public static final String INDEXLIMIT = "indexlimit";
    public static final String TIKATIMEOUT = "tikatimeout";
    public static final String OTHERTIMEOUT = "othertimeout";
    public static final String DATANUCLEUS = "datanucleus";
    public static final String ZOOKEEPER = "zookeeper";
	public static final String HIGHLIGHTMLT = "highlightmlt";
	public static final String LANGUAGES = "languages";
	public static final String MAHOUTBASEPATH = "mahoutbasepath";
	public static final String MLTCOUNT = "mltcount";
    public static final String MLTMINDF = "mltmindf";
    public static final String MLTMINTF = "mltmintf";	
    public static final String DISTRIBUTEDLOCKMODE = "distributedlockmode";
    public static final String SMALL = "small";
    public static final String BIG = "big";
    public static final String DISTRIBUTEDPROCESS = "distributedprocess";
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
    public static final String LUCENEPATH = "lucenepath";
}
