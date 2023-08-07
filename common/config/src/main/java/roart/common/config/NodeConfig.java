package roart.common.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import roart.common.constants.Constants;

public class NodeConfig extends MyConfig {
    public enum Config { REINDEXLIMIT, INDEXLIMIT, FAILEDLIMIT, OTHERTIMEOUT, TIKATIMEOUT, MLTCOUNT, MLTMINTF, MLTMINDF }

    /*
    public ConfigTreeMap configTreeMap;
    
    public Map<String, Object> configValueMap;
    public Map<String, String> text = new HashMap();
    public Map<String, Object> deflt = new HashMap();
    public Map<String, Class> type = new HashMap();
    */
    
    public NodeConfig() {
        
    }
 
    @JsonIgnore
    public double getMPCpu() {
        return (Double) getNotEmptyValueOrDefault(ConfigConstants.MPCPU);       
    }
    
    @JsonIgnore
    public int getMPBatch() {
        return (Integer) getNotEmptyValueOrDefault(ConfigConstants.MPBATCH);       
    }
    
    @JsonIgnore
    public int getMPThreadsFS() {
        return (Integer) getNotEmptyValueOrDefault(ConfigConstants.MPTHREADSFS);       
    }
    
    @JsonIgnore
    public int getMPThreadsConvert() {
        return (Integer) getNotEmptyValueOrDefault(ConfigConstants.MPTHREADSCONVERT);       
    }
    
    @JsonIgnore
    public int getMPThreadsIndex() {
        return (Integer) getNotEmptyValueOrDefault(ConfigConstants.MPTHREADSINDEX);       
    }
    
    @JsonIgnore
    public String[] getLanguages() {
        String languages = (String) getValueOrDefault(ConfigConstants.NODELANGUAGES);
        return languages.split(",");
    }

    public Boolean wantClassify() {
        return (Boolean) getValueOrDefault(ConfigConstants.NODECLASSIFY);
    }
    
    public boolean wantMahout() {
        return (Boolean) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUT);
    }
    
    public boolean wantMahoutSpark() {
        return (Boolean) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTSPARK);
    }
    
    public boolean wantSparkML() {
        return (Boolean) getValueOrDefault(ConfigConstants.MACHINELEARNINGSPARKML);
    }
    
    public boolean wantOpenNLP() {
        return (Boolean) getValueOrDefault(ConfigConstants.MACHINELEARNINGOPENNLP);
    }
    
    public Boolean wantLucene() {
        return (Boolean) getValueOrDefault(ConfigConstants.SEARCHENGINELUCENE);
    }
    
    public String luceneIndex() {
        return (String) getValueOrDefault(ConfigConstants.SEARCHENGINELUCENEINDEX);
    }

    public Boolean wantSolr() {
        return (Boolean) getValueOrDefault(ConfigConstants.SEARCHENGINESOLR);
    }
    
    public String solrIndex() {
        return (String) getValueOrDefault(ConfigConstants.SEARCHENGINESOLRINDEX);
    }
    
    public Boolean wantElastic() {
        return (Boolean) getValueOrDefault(ConfigConstants.SEARCHENGINEELASTIC);
    }
    
    public String elasticIndex() {
        return (String) getValueOrDefault(ConfigConstants.SEARCHENGINEELASTICINDEX);
    }
    
    public Boolean wantHibernate() {
        return (Boolean) getValueOrDefault(ConfigConstants.DATABASEHIBERNATE);
    }
    
    public Boolean wantHBase() {
        return (Boolean) getValueOrDefault(ConfigConstants.DATABASEHBASE);
    }
    
    public Boolean wantCassandra() {
        return (Boolean) getValueOrDefault(ConfigConstants.DATABASECASSANDRA);
    }
    
    public Boolean wantDynamodb() {
        return (Boolean) getValueOrDefault(ConfigConstants.DATABASEDYNAMODB);
    }
    
    public Boolean wantDataNucleus() {
        return (Boolean) getValueOrDefault(ConfigConstants.DATABASEDATANUCLEUS);
    }
    
    public Boolean wantSpringData() {
        return (Boolean) getValueOrDefault(ConfigConstants.DATABASESPRING);
    }
    
    public Boolean wantSwift() {
        return (Boolean) getValueOrDefault(ConfigConstants.FILESYSTEMSWIFT);
    }
    
    public Boolean wantLocal() {
        return (Boolean) getValueOrDefault(ConfigConstants.FILESYSTEMLOCAL);
    }
    
    public Boolean wantHDFS() {
        return (Boolean) getValueOrDefault(ConfigConstants.FILESYSTEMHDFS);
    }
    
    public Boolean wantS3() {
        return (Boolean) getValueOrDefault(ConfigConstants.FILESYSTEMS3);
    }
    
    @Deprecated
    public Boolean wantZookeeperSmall() {
        return !((Boolean) getValueOrDefault(ConfigConstants.SYNCHRONIZATIONDISTRIBUTEDLOCKMODEBIG));
    }
    
    @JsonIgnore
    public String getZookeeper() {
        return (String) getNotEmptyValueOrDefault(ConfigConstants.SYNCHRONIZATIONZOOKEEPER);
    }
    
    @JsonIgnore
    public String getRedis() {
        return (String) configValueMap.get(ConfigConstants.SYNCHRONIZATIONREDIS);
    }
    
    @JsonIgnore
    public Boolean wantSynchronizationCommunication() {
        return ((Boolean) getValueOrDefault(ConfigConstants.SYNCHRONIZATIONCOMMUNICATION));
    }
    
    @JsonIgnore
    public String getSynchronizationCommunicationName() {
        return (String) configValueMap.get(ConfigConstants.SYNCHRONIZATIONCOMMUNICATIONNAME);
    }
    
    @JsonIgnore
    public String getSynchronizationCommunicationConnection() {
        return (String) configValueMap.get(ConfigConstants.SYNCHRONIZATIONCOMMUNICATIONCONNECTION);
    }
    
    @JsonIgnore
    public String getLocker() {
        if (wantDistributedTraverse()) {
            return roart.common.constants.Constants.HAZELCAST;
        }
        if (getZookeeper() != null && !isLocalhost()) {
            return roart.common.constants.Constants.CURATOR;
        }
        return Constants.LOCAL;
    }
    
    @JsonIgnore
    public boolean isLocalhost() {
        return ConfigConstants.LOCALHOST.equals(getNodename());
    }
    
    @JsonIgnore
    public String getNodename() {
        return (String) configValueMap.get(ConfigConstants.NODENODENAME);       
    }
    
    public Boolean wantDistributedTraverse() {
        return (Boolean) getValueOrDefault(ConfigConstants.SYNCHRONIZATIONDISTRIBUTEDPROCESS);
    }
    
    public Boolean wantAsync() {
        return (Boolean) getNotEmptyValueOrDefault(ConfigConstants.SYNCHRONIZATIONASYNC);
    }
    
    @JsonIgnore
    public String[] getDirListNot() {
        String dirList = (String) getValueOrDefault(ConfigConstants.FSDIRLISTNOT);
        return dirList.split(",");
    }
    
    @JsonIgnore
    public String[] getDirList() {
        String dirList = (String) getValueOrDefault(ConfigConstants.FSDIRLIST);
        return dirList.split(",");
    }
    
    @JsonIgnore
    public String getLanguagesUnsplit() {
        return (String) getValueOrDefault(ConfigConstants.NODELANGUAGES);
    }
    
    @JsonIgnore
    public String getElasticPort() {
        return (String) getValueOrDefault(ConfigConstants.SEARCHENGINEELASTICELASTICPORT);
    }
    
    @JsonIgnore
    public String getElasticHost() {
        return (String) getValueOrDefault(ConfigConstants.SEARCHENGINEELASTICELASTICHOST);
    }
    
    @JsonIgnore
    public boolean getElasticSsl() {
        return (Boolean) getValueOrDefault(ConfigConstants.SEARCHENGINEELASTICSSL);
    }
    
    @JsonIgnore
    public String getElasticUsername() {
        return (String) getValueOrDefault(ConfigConstants.SEARCHENGINEELASTICELASTICUSERNAME);
    }
    
    @JsonIgnore
    public String getElasticPassword() {
        return (String) getValueOrDefault(ConfigConstants.SEARCHENGINEELASTICELASTICPASSWORD);
    }
    
    @JsonIgnore
    public String getHDFSDefaultName() {
        return (String) getValueOrDefault(ConfigConstants.FILESYSTEMHDFSHDFSCONFFS);
    }
    
    @JsonIgnore
    public String getMahoutBasePath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTBASEPATH);
    }
    
    @JsonIgnore
    public String getMahoutModelPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTMODELPATH);
    }
    
    @JsonIgnore
    public String getMahoutLabelIndexPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTLABELINDEXFILEPATH);
    }
    
    @JsonIgnore
    public String getMahoutDictionaryPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTDICTIONARYPATH);
    }
    
    @JsonIgnore
    public String getMahoutDocumentFrequencyPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTDOCUMENTFREQUENCYPATH);
    }
    
    @JsonIgnore
    public String getMahoutAlgorithm() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTALGORITHM);
    }
    
    @JsonIgnore
    public String getMahoutConfFs() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTCONFFS);
    }
    
    @JsonIgnore
    public String getMahoutSparkBasePath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTBASEPATH);
    }
    
    @JsonIgnore
    public String getMahoutSparkModelPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTMODELPATH);
    }
    
    @JsonIgnore
    public String getMahoutSparkLabelIndexPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTLABELINDEXFILEPATH);
    }
    
    @JsonIgnore
    public String getMahoutSparkDictionaryPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTDICTIONARYPATH);
    }
    
    @JsonIgnore
    public String getMahoutSparkDocumentFrequencyPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTDOCUMENTFREQUENCYPATH);
    }
    
    @JsonIgnore
    public String getMahoutSparkConfFs() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTCONFFS);
    }
    
    @JsonIgnore
    public String getMahoutSparkAlgorithm() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTALGORITHM);
    }
    
    @JsonIgnore
    public String getMahoutSparkMaster() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTSPARKMASTER);
    }
    
    @JsonIgnore
    public String getOpenNLPModelPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGOPENNLPOPENNLPMODELPATH);
    }
    
    @JsonIgnore
    public String getSparkMLBasePath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLBASEPATH);
    }
    
    @JsonIgnore
    public String getSparkMLModelPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLMODELPATH);
    }
    
    @JsonIgnore
    public String getSparkMLLabelIndexPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLLABELINDEXPATH);
    }
    
    @JsonIgnore
    public String getSparkMLSparkMaster() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMASTER);
    }
    
    @JsonIgnore
    public String getSwiftUser() {
        return (String) getValueOrDefault(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFUSER);
    }
   
    @JsonIgnore
    public String getSwiftKey() {
        return (String) getValueOrDefault(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFKEY);
    }
   
    @JsonIgnore
    public String getSwiftUrl() {
        return (String) getValueOrDefault(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFURL);
    }
   
    @JsonIgnore
    public String getSwiftContainer() {
        return (String) getValueOrDefault(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFCONTAINER);
    }
   
    @JsonIgnore
    public String getS3Host() {
        return (String) getValueOrDefault(ConfigConstants.FILESYSTEMS3HOST);
    }
   
    @JsonIgnore
    public String getS3Port() {
        return (String) getValueOrDefault(ConfigConstants.FILESYSTEMS3PORT);
    }
   
    @JsonIgnore
    public String getS3Region() {
        return (String) getValueOrDefault(ConfigConstants.FILESYSTEMS3REGION);
    }
   
    @JsonIgnore
    public String getS3AccessKey() {
        return (String) getValueOrDefault(ConfigConstants.FILESYSTEMS3ACCESSKEY);
    }
   
    @JsonIgnore
    public String getS3SecretKey() {
        return (String) getValueOrDefault(ConfigConstants.FILESYSTEMS3SECRETKEY);
    }
   
    @JsonIgnore
    public String getSolrurl() {
        return (String) getValueOrDefault(ConfigConstants.SEARCHENGINESOLRSOLRURL);
    }
   
    @JsonIgnore
    public Integer getMLTMinTF() {
        return (Integer) getValueOrDefault(ConfigConstants.SEARCHENGINEMLTMLTMINTF);
    }
    
    @JsonIgnore
    public Integer getMLTMinDF() {
        return (Integer) getValueOrDefault(ConfigConstants.SEARCHENGINEMLTMLTMINDF);
    }
    
    @JsonIgnore
    public Integer getMLTCount() {
        return (Integer) getValueOrDefault(ConfigConstants.SEARCHENGINEMLTMLTCOUNT);
    }
    
    @JsonIgnore
    public String getLucenepath() {
        return (String) getValueOrDefault(ConfigConstants.SEARCHENGINELUCENELUCENEPATH);
    }
    
    @JsonIgnore
    public Boolean getHighlightmlt() {
        return (Boolean) getValueOrDefault(ConfigConstants.GUIHIGHLIGHTMLT);
    }
    
    @JsonIgnore
    public Boolean getDownloader() {
        return (Boolean) getValueOrDefault(ConfigConstants.GUIDOWNLOADER);
    }
    
    @JsonIgnore
    public Boolean getAuthenticate() {
        return (Boolean) getValueOrDefault(ConfigConstants.GUIAUTHENTICATE);
    }
    
    @JsonIgnore
    public String getHbasequorum() {
        return (String) getValueOrDefault(ConfigConstants.DATABASEHBASEHBASEQUORUM);
    }
    
    @JsonIgnore
    public String getHbaseport() {
        return (String) getValueOrDefault(ConfigConstants.DATABASEHBASEHBASEPORT);
    }
    
    @JsonIgnore
    public String getHbasemaster() {
        return (String) getValueOrDefault(ConfigConstants.DATABASEHBASEHBASEMASTER);
    }
    
    @JsonIgnore
    public String getHbaseTableprefix() {
        return (String) getValueOrDefault(ConfigConstants.DATABASEHBASETABLEPREFIX);
    }

    @JsonIgnore
    public String getCassandraHost() {
        return (String) getValueOrDefault(ConfigConstants.DATABASECASSANDRAHOST);
    }
    
    @JsonIgnore
    public Boolean getCassandraTLS() {
        return (Boolean) getValueOrDefault(ConfigConstants.DATABASECASSANDRATLS);
    }
    
    @JsonIgnore
    public String getCassandraPort() {
        return (String) getValueOrDefault(ConfigConstants.DATABASECASSANDRAPORT);
    }
    
    @JsonIgnore
    public String getCassandraKeyspace() {
        return (String) getValueOrDefault(ConfigConstants.DATABASECASSANDRAKEYSPACE);
    }
    
    @JsonIgnore
    public String getCassandraThriftPort() {
        return (String) getValueOrDefault(ConfigConstants.DATABASECASSANDRATHRIFTPORT);
    }
    
    @JsonIgnore
    public String getCassandraTLSPort() {
        return (String) getValueOrDefault(ConfigConstants.DATABASECASSANDRATLSPORT);
    }
    
    @JsonIgnore
    public String getDynamodbHost() {
        return (String) getValueOrDefault(ConfigConstants.DATABASEDYNAMODBHOST);
    }
    
    @JsonIgnore
    public String getDynamodbPort() {
        return (String) getValueOrDefault(ConfigConstants.DATABASEDYNAMODBPORT);
    }
    
    @JsonIgnore
    public String getDynamodbTableprefix() {
        return (String) getValueOrDefault(ConfigConstants.DATABASEDYNAMODBTABLEPREFIX);
    }
    
    @JsonIgnore
    public Integer getTikaTimeout() {
        return (Integer) getValueOrDefault(ConfigConstants.CONVERSIONTIKATIMEOUT);        
    }
    
    @JsonIgnore
    public Integer getOtherTimeout() {
        return (Integer) getValueOrDefault(ConfigConstants.CONVERSIONOTHERTIMEOUT);        
    }
    
    @JsonIgnore
    public String getConverters() {
        return (String) getNotEmptyValueOrDefault(ConfigConstants.CONVERSION);        
    }
    
    @JsonIgnore
    public String getConnectors() {
        return (String) getNotEmptyValueOrDefault(ConfigConstants.CONNECTOR);        
    }
    
    @JsonIgnore
    public Integer getIndexLimit() {
        return (Integer) getValueOrDefault(ConfigConstants.INDEXINDEXLIMIT);        
    }
    
    @JsonIgnore
    public Integer getReindexLimit() {
        return (Integer) getValueOrDefault(ConfigConstants.INDEXREINDEXLIMIT);        
    }
    
    @JsonIgnore
    public Integer getFailedLimit() {
        return (Integer) getValueOrDefault(ConfigConstants.INDEXFAILEDLIMIT);        
    }
    
    @JsonIgnore
    public String getH2dir() {
        return (String) getValueOrDefault(ConfigConstants.DATABASEHIBERNATEH2DIR);        
    }
    
    @JsonIgnore
    public String getInmemoryServer() {
        return (String) getNotEmptyValueOrDefault(ConfigConstants.INMEMORYSERVER);
    }

    @JsonIgnore
    public String getInmemoryHazelcast() {
        return (String) getNotEmptyValueOrDefault(ConfigConstants.INMEMORYHAZELCAST);
    }

    @JsonIgnore
    public String getInmemoryRedis() {
        return (String) getNotEmptyValueOrDefault(ConfigConstants.INMEMORYREDIS);
    }

    @JsonIgnore
    public Object getValueOrDefault(String key) {
        Object retVal = configValueMap.get(key);
        //System.out.println("r " + retVal + " " + deflt.get(key));
        return Optional.ofNullable(retVal).orElse(deflt.get(key));
    }

    @JsonIgnore
    public Object getNotEmptyValueOrDefault(String key) {
        Object retVal = configValueMap.get(key);
        //System.out.println("r " + retVal + " " + deflt.get(key));
        if (retVal instanceof String) {
            String str = (String) retVal;
            if (str.isEmpty()) {
                retVal = null;
            }
        }
        return Optional.ofNullable(retVal).orElse(deflt.get(key));
    }

    /*
    public String[] languages = null;
    
    public String[] dirlist = null;
    public String[] dirlistnot = null;
    
    public boolean hasHibernate = false;
    public String db = null;
    public String hbasequorum = null;
    public String hbaseport = null;
    public String hbasemaster = null;

    public String index = null;
    public String lucenepath = null;
    public String solrurl = null;
    public String elastichost = null;
    public String elasticport = null;
    public boolean highlightmlt = false;
    //public boolean searchsimilar = false;
    
    public Map<Config, Integer> configMap = new HashMap<Config, Integer>();
    public String hdfsdefaultname = null;
    public String swifturl = null;
    public String swiftuser = null;
    public String swiftkey = null;
    public String swiftcontainer = null;
    public String zookeeper = null;
    public boolean zookeepersmall = false;
    public boolean distributedtraverse = false;
    public String locker = null;
    
    public String classify = null;
    public String opennlpmodelpath = null;
    
    public String mahoutconffs = null;
    public String mahoutbasepath = null;
    public String mahoutmodelpath = null;
    public String mahoutlabelindexpath = null;
    public String mahoutdictionarypath = null;
    public String mahoutdocumentfrequencypath = null;
    public String mahoutalgorithm = null;
    public String mahoutsparkmaster = null;

    public String sparkmlbasepath = null;
    public String sparkmlmodelpath = null;
    public String sparkmllabelindexpath = null;
    public String sparkmaster = null;
    
    public boolean downloader = false;
    public boolean authenticate = false;
    */
}
