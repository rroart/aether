package roart.common.service;

import roart.common.config.NodeConfig;

public class ServiceParam {
public enum Function { INDEX, FILESYSTEM, OVERLAPPING, MEMORYUSAGE, NOTINDEXED, FILESYSTEMLUCENENEW, DBINDEX, DBSEARCH, CONSISTENTCLEAN, SEARCH, SEARCHSIMILAR, DELETEPATH, DBCLEAR, DBDROP, DBCOPY, INDEXCLEAN, INDEXDELETE, DBCHECK }
public NodeConfig config;
    public Function function;
    public String name;
    public String path;
    public String search;
    public String suffix;
    public String lowerdate;
    public String higherdate;
    public boolean reindex;
    public boolean clean;
    public boolean md5checknew;
    public String lang;
    public String webpath;
    public boolean async;
    public String uuid;
}
