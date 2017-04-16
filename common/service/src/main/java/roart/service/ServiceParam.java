package roart.service;

import roart.config.NodeConfig;

public class ServiceParam {
public enum Function { INDEX, FILESYSTEM, OVERLAPPING, REINDEXSUFFIX, REINDEXDATE, MEMORYUSAGE, NOTINDEXED, FILESYSTEMLUCENENEW, DBINDEX, DBSEARCH, CONSISTENTCLEAN, SEARCH, SEARCHSIMILAR, REINDEXLANGUAGE, DELETEPATH }
public NodeConfig config;
    public Function function;
    public String name;
    public String add;
    public String file;
    public String suffix;
    public String lowerdate;
    public String higherdate;
    public boolean reindex;
    public boolean md5change;
    public boolean clean;
    public String path;
    public String md5;
    public boolean md5checknew;
    public String dirname;
    public String lang;
}
