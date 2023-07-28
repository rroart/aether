package roart.search;

import java.util.List;

import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.common.service.ServiceParam;
import roart.common.service.ServiceParam.Function;
import roart.common.service.ServiceResult;
import roart.eureka.util.EurekaUtil;
import roart.queue.Queues;

public class Util {

    private Sender sender;
    
    private NodeConfig nodeConf;
    
    public Util(Sender sender) {
        this.sender = sender;
    }
    
    public Object search(String search, String type) {
        String path = "/home/roart/src/aethermicro/books";
        SearchEngineSearchParam param = new SearchEngineSearchParam();
        param.conf = getConfig();
        param.str = search;
        param.searchtype = type;
        return sender.send(param, "search");
    }

    public Object searchmlt(String search) {
        SearchEngineSearchParam param = new SearchEngineSearchParam();
        param.conf = getConfig();
        param.str = search;
        return sender.send(param, "searchmlt");
    }

    public Object filesystemlucenenew(String add, boolean md5checknew) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.FILESYSTEMLUCENENEW;
        param.add = add;
        param.md5checknew = md5checknew;
        param.webpath = EurekaConstants.FILESYSTEMLUCENENEW;
        return sender.send(param, param.webpath);
    }

    public Object index(String add, boolean reindex) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.INDEX;
        param.add = add;
        param.reindex = reindex;
        param.webpath = EurekaConstants.INDEX;
        return sender.send(param, param.webpath);
    }

    public Object traverse(String add) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.FILESYSTEM;
        param.add = add;
        param.webpath = EurekaConstants.TRAVERSE;
        return sender.send(param, param.webpath);
    }

    // called from ui
    public Object overlapping() {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.OVERLAPPING;
        param.webpath = EurekaConstants.OVERLAPPING;
        return sender.send(param, param.webpath);
    }

    // called from ui
    // returns list: indexed file list
    // returns list: tika timeout
    // returns list: not indexed
    // returns list: deleted
    public Object indexsuffix(String suffix, boolean reindex) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.REINDEXSUFFIX;
        param.suffix = suffix;
        param.reindex = reindex;
        param.webpath = EurekaConstants.INDEXSUFFIX;
        return sender.send(param, param.webpath);
    }

    public Object reindexdatelower(String date, boolean reindex) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.REINDEXDATE;
        param.lowerdate = date;
        param.reindex = reindex;
        param.webpath = EurekaConstants.REINDEXDATELOWER;
        return sender.send(param, param.webpath);
    }

    public Object reindexdatehigher(String date, boolean reindex) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.REINDEXDATE;
        param.higherdate = date;
        param.reindex = reindex;
        param.webpath = EurekaConstants.REINDEXDATEHIGHER;
        return sender.send(param, param.webpath);
    }

    // called from ui
    // returns list: indexed file list
    // returns list: tika timeout
    // returns list: file does not exist
    // returns list: not indexed
    public Object reindexlanguage(String lang) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.REINDEXLANGUAGE;
        param.lang = lang;
        param.webpath = EurekaConstants.REINDEXLANGUAGE;
        return sender.send(param, param.webpath);
    }

    // old, probably oudated by overlapping?
    public Object cleanupfs(String dirname) {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.CONSISTENTCLEAN;
        param.dirname = dirname;
        param.webpath = EurekaConstants.CLEANUPFS;
        return sender.send(param, param.webpath);
    }

    public Object dbcheck(String db) {
        ServiceParam param = new ServiceParam();
        param.name = db;
        param.config = getConfig();
        param.function = Function.DBCHECK;
        param.webpath = EurekaConstants.DBCHECK;
        return sender.send(param, param.webpath);
    }

    // called from ui
    public Object memoryusage() {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.MEMORYUSAGE;
        param.webpath = EurekaConstants.MEMORYUSAGE;
        return sender.send(param, param.webpath);
    }

    // called from ui
    // returns list: not indexed
    // returns list: another with columns
    public Object notindexed() throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.NOTINDEXED;
        param.webpath = EurekaConstants.NOTINDEXED;
        return sender.send(param, param.webpath);
    }
    public Object dbindex(String md5) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.DBINDEX;
        param.md5 = md5;
        param.webpath = EurekaConstants.DBINDEX;
        return sender.send(param, param.webpath);
    }

    public Object dbsearch(String md5) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.DBSEARCH;
        param.md5 = md5;
        param.webpath = EurekaConstants.DBSEARCH;
        return sender.send(param, param.webpath);
    }


    public Object consistentclean(boolean clean) {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.CONSISTENTCLEAN;
        param.clean = clean;
        param.webpath = EurekaConstants.CONSISTENTCLEAN;
        return sender.send(param, param.webpath);
    }

    public Object deletepathdb(String path) {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        //param.function = Function.DELETEPATHDB;
        param.file = path;
        param.webpath = EurekaConstants.DELETEPATHDB;
        return sender.send(param, param.webpath);
    }

    public Object dbclear(String db) {
        ServiceParam param = new ServiceParam();
        param.name = db;
        param.config = getConfig();
        param.function = Function.DBCLEAR;
        param.webpath = EurekaConstants.DBCLEAR;
        return sender.send(param, param.webpath);
    }
    
    public Object dbdrop(String db) {
        ServiceParam param = new ServiceParam();
        param.name = db;
        param.config = getConfig();
        param.function = Function.DBDROP;
        param.webpath = EurekaConstants.DBDROP;
        return sender.send(param, param.webpath);
    }
    
    public Object dbcopy(String in, String out) {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.DBCOPY;
        param.webpath = EurekaConstants.DBCOPY;
        param.name = in;
        param.add = out;
        return sender.send(param, param.webpath);
    }
    
    public Object indexclean() {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.INDEXCLEAN;
        param.webpath = EurekaConstants.INDEXCLEAN;
        return sender.send(param, param.webpath);
    }
    
    public Object indexdelete() {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.INDEXDELETE;
        param.webpath = EurekaConstants.INDEXDELETE;
        return sender.send(param, param.webpath);
    }
    
    private NodeConfig getConfig() {
        return nodeConf;
    }

    private String getAppName() {
        return EurekaConstants.AETHER;
    }
}
