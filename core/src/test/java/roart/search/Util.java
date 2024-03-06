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

    public Object filesystemlucenenew(String add, boolean md5checknew, String suffix) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.FILESYSTEMLUCENENEW;
        param.path = add;
        param.md5checknew = md5checknew;
        param.suffix = suffix;
        param.webpath = EurekaConstants.TASK;
        return sender.send(param, param.webpath);
    }

    public Object index(String add, boolean reindex, String suffix, String language, String lowerdate, String higherdate) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.INDEX;
        param.path = add;
        param.reindex = reindex;
        param.suffix = suffix;
        param.lang = language;
        param.reindex = reindex;
        param.lowerdate = lowerdate;
        param.higherdate = higherdate;
        param.webpath = EurekaConstants.TASK;
        return sender.send(param, param.webpath);
    }

    public Object traverse(String add, String suffix) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.FILESYSTEM;
        param.path = add;
        param.suffix = suffix;
        param.webpath = EurekaConstants.TASK;
        return sender.send(param, param.webpath);
    }

    // called from ui
    public Object overlapping() {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.OVERLAPPING;
        param.webpath = EurekaConstants.TASK;
        return sender.send(param, param.webpath);
    }

    // old, probably oudated by overlapping?
    public Object cleanupfs(String dirname) {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.CONSISTENTCLEAN;
        param.path = dirname;
        param.webpath = EurekaConstants.TASK;
        return sender.send(param, param.webpath);
    }

    public Object dbcheck(String db) {
        ServiceParam param = new ServiceParam();
        param.name = db;
        param.config = getConfig();
        param.function = Function.DBCHECK;
        param.webpath = EurekaConstants.TASK;
        return sender.send(param, param.webpath);
    }

    // called from ui
    public Object memoryusage() {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.MEMORYUSAGE;
        param.webpath = EurekaConstants.TASK;
        return sender.send(param, param.webpath);
    }

    // called from ui
    // returns list: not indexed
    // returns list: another with columns
    public Object notindexed() throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.NOTINDEXED;
        param.webpath = EurekaConstants.TASK;
        return sender.send(param, param.webpath);
    }
    public Object dbindex(String search) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.DBINDEX;
        param.search = search;
        param.webpath = EurekaConstants.TASK;
        return sender.send(param, param.webpath);
    }

    public Object dbsearch(String search) throws Exception {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.DBSEARCH;
        param.search = search;
        param.webpath = EurekaConstants.TASK;
        return sender.send(param, param.webpath);
    }


    public Object consistentclean(boolean clean) {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.CONSISTENTCLEAN;
        param.clean = clean;
        param.webpath = EurekaConstants.TASK;
        return sender.send(param, param.webpath);
    }

    public Object deletepathdb(String path) {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        //param.function = Function.DELETEPATHDB;
        param.path = path;
        param.webpath = EurekaConstants.TASK;
        return sender.send(param, param.webpath);
    }

    public Object dbclear(String db) {
        ServiceParam param = new ServiceParam();
        param.name = db;
        param.config = getConfig();
        param.function = Function.DBCLEAR;
        param.webpath = EurekaConstants.TASK;
        return sender.send(param, param.webpath);
    }
    
    public Object dbdrop(String db) {
        ServiceParam param = new ServiceParam();
        param.name = db;
        param.config = getConfig();
        param.function = Function.DBDROP;
        param.webpath = EurekaConstants.TASK;
        return sender.send(param, param.webpath);
    }
    
    public Object dbcopy(String in, String out) {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.DBCOPY;
        param.webpath = EurekaConstants.TASK;
        param.name = in;
        param.path = out;
        return sender.send(param, param.webpath);
    }
    
    public Object indexclean() {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.INDEXCLEAN;
        param.webpath = EurekaConstants.TASK;
        return sender.send(param, param.webpath);
    }
    
    public Object indexdelete() {
        ServiceParam param = new ServiceParam();
        param.config = getConfig();
        param.function = Function.INDEXDELETE;
        param.webpath = EurekaConstants.TASK;
        return sender.send(param, param.webpath);
    }
    
    private NodeConfig getConfig() {
        return nodeConf;
    }

    private String getAppName() {
        return EurekaConstants.AETHER;
    }
}
