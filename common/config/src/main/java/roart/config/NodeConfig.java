package roart.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        return (Boolean) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUT);
    }
    
    public Boolean wantLucene() {
        return (Boolean) getValueOrDefault(ConfigConstants.SEARCHENGINELUCENE);
    }
    
    public Boolean wantSolr() {
        return (Boolean) getValueOrDefault(ConfigConstants.SEARCHENGINESOLR);
    }
    
    public Boolean wantElastic() {
        return (Boolean) getValueOrDefault(ConfigConstants.SEARCHENGINEELASTIC);
    }
    
    public Boolean wantHibernate() {
        return (Boolean) getValueOrDefault(ConfigConstants.DATABASEHIBERNATE);
    }
    
    public Boolean wantHBase() {
        return (Boolean) getValueOrDefault(ConfigConstants.DATABASEHBASE);
    }
    
    public Boolean wantDataNucleus() {
        return (Boolean) getValueOrDefault(ConfigConstants.DATABASEDATANUCLEUS);
    }
    
    public Boolean wantSwift() {
        return (Boolean) getValueOrDefault(ConfigConstants.FILESYSTEMSWIFT);
    }
    
    public Boolean wantHDFS() {
        return (Boolean) getValueOrDefault(ConfigConstants.FILESYSTEMHDFS);
    }
    
    public Boolean wantZookeeperSmall() {
        return !((Boolean) getValueOrDefault(ConfigConstants.SYNCHRONIZATIONDISTRIBUTEDLOCKMODEBIG));
    }
    
    public String getZookeeper() {
        return (String) getValueOrDefault(ConfigConstants.SYNCHRONIZATIONZOOKEEPER);
    }
    
    public String getLocker() {
        if (wantDistributedTraverse()) {
            return roart.util.Constants.HAZELCAST;
        }
        if (getZookeeper() != null && !isLocalhost()) {
            return roart.util.Constants.CURATOR;
        }
        return null;
    }
    
    public boolean isLocalhost() {
        return ConfigConstants.LOCALHOST.equals(getNodename());
    }
    
    public String getNodename() {
        return (String) getValueOrDefault(ConfigConstants.NODENODENAME);       
    }
    
    public Boolean wantDistributedTraverse() {
        return (Boolean) getValueOrDefault(ConfigConstants.SYNCHRONIZATIONDISTRIBUTEDPROCESS);
    }
    
    public String[] getDirListNot() {
        String dirList = (String) getValueOrDefault(ConfigConstants.FSDIRLISTNOT);
        return dirList.split(",");
    }
    
    public String[] getDirList() {
        String dirList = (String) getValueOrDefault(ConfigConstants.FSDIRLIST);
        return dirList.split(",");
    }
    
    public String getLanguagesUnsplit() {
        return (String) getValueOrDefault(ConfigConstants.NODELANGUAGES);
    }
    
    public String getElasticPort() {
        return (String) getValueOrDefault(ConfigConstants.SEARCHENGINEELASTICELASTICPORT);
    }
    
    public String getElasticHost() {
        return (String) getValueOrDefault(ConfigConstants.SEARCHENGINEELASTICELASTICHOST);
    }
    
    public String getHDFSDefaultName() {
        return (String) getValueOrDefault(ConfigConstants.FILESYSTEMHDFSHDFSCONFFS);
    }
    
    public String getMahoutBasePath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTBASEPATH);
    }
    
    public String getMahoutModelPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTMODELPATH);
    }
    
    public String getMahoutLabelIndexPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTLABELINDEXFILEPATH);
    }
    
    public String getMahoutDictionaryPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTDICTIONARYPATH);
    }
    
    public String getMahoutDocumentFrequencyPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTDOCUMENTFREQUENCYPATH);
    }
    
    public String getMahoutAlgorithm() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTALGORITHM);
    }
    
    public String getMahoutConfFs() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTMAHOUTCONFFS);
    }
    
    public String getMahoutSparkMaster() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGMAHOUTSPARKMAHOUTSPARKMASTER);
    }
    
    public String getOpenNLPModelPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGOPENNLPOPENNLPMODELPATH);
    }
    
    public String getSparkMLBasePath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLBASEPATH);
    }
    
    public String getSparkMLModelPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLMODELPATH);
    }
    
    public String getSparkMLLabelIndexPath() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMLLABELINDEXPATH);
    }
    
    public String getSparkMLSparkMaster() {
        return (String) getValueOrDefault(ConfigConstants.MACHINELEARNINGSPARKMLSPARKMASTER);
    }
    
    public String getSwiftUser() {
        return (String) getValueOrDefault(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFUSER);
    }
   
    public String getSwiftKey() {
        return (String) getValueOrDefault(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFKEY);
    }
   
    public String getSwiftUrl() {
        return (String) getValueOrDefault(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFURL);
    }
   
    public String getSwiftContainer() {
        return (String) getValueOrDefault(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFCONTAINER);
    }
   
    public String getSolrurl() {
        return (String) getValueOrDefault(ConfigConstants.SEARCHENGINESOLRSOLRURL);
    }
   
    public Integer getMLTMinTF() {
        return (Integer) getValueOrDefault(ConfigConstants.SEARCHENGINEMLTMLTMINTF);
    }
    
    public Integer getMLTMinDF() {
        return (Integer) getValueOrDefault(ConfigConstants.SEARCHENGINEMLTMLTMINDF);
    }
    
    public Integer getMLTCount() {
        return (Integer) getValueOrDefault(ConfigConstants.SEARCHENGINEMLTMLTCOUNT);
    }
    
    public String getLucenepath() {
        return (String) getValueOrDefault(ConfigConstants.SEARCHENGINELUCENELUCENEPATH);
    }
    
    public Boolean getHighlightmlt() {
        return (Boolean) getValueOrDefault(ConfigConstants.GUIHIGHLIGHTMLT);
    }
    
    public Boolean getDownloader() {
        return (Boolean) getValueOrDefault(ConfigConstants.GUIDOWNLOADER);
    }
    
    public Boolean getAuthenticate() {
        return (Boolean) getValueOrDefault(ConfigConstants.GUIAUTHENTICATE);
    }
    
    public String getHbasequorum() {
        return (String) getValueOrDefault(ConfigConstants.DATABASEHBASEHBASEQUORUM);
    }
    
    public String getHbaseport() {
        return (String) getValueOrDefault(ConfigConstants.DATABASEHBASEHBASEPORT);
    }
    
    public String getHbasemaster() {
        return (String) getValueOrDefault(ConfigConstants.DATABASEHBASEHBASEMASTER);
    }
    
    public Integer getTikaTimeout() {
        return (Integer) getValueOrDefault(ConfigConstants.CONVERSIONTIKATIMEOUT);        
    }
    
    public Integer getOtherTimeout() {
        return (Integer) getValueOrDefault(ConfigConstants.CONVERSTIONOTHERTIMEOUT);        
    }
    
    public Integer getIndexLimit() {
        return (Integer) getValueOrDefault(ConfigConstants.INDEXINDEXLIMIT);        
    }
    
    public Integer getReindexLimit() {
        return (Integer) getValueOrDefault(ConfigConstants.INDEXREINDEXLIMIT);        
    }
    
    public Integer getFailedLimit() {
        return (Integer) getValueOrDefault(ConfigConstants.INDEXFAILEDLIMIT);        
    }
    
   public Object getValueOrDefault(String key) {
        Object retVal = configValueMap.get(key);
        //System.out.println("r " + retVal + " " + deflt.get(key));
        return Optional.ofNullable(retVal).orElse(deflt.get(key));
    }

    // TODO fix
    public boolean admin = true;
    
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
