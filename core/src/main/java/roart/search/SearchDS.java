package roart.search;

import roart.service.ControlService;
import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyQueueFactory;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.constants.OperationConstants;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.queue.QueueElement;
import roart.common.searchengine.SearchEngineConstructorParam;
import roart.common.searchengine.SearchEngineConstructorResult;
import roart.common.searchengine.SearchEngineDeleteParam;
import roart.common.searchengine.SearchEngineDeleteResult;
import roart.common.searchengine.SearchEngineIndexParam;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineParam;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.common.searchengine.SearchResult;
import roart.database.IndexFilesDao;
import roart.eureka.util.EurekaUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SearchDS {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private NodeConfig nodeConf;

    private ControlService controlService;
    
    private MyQueue<QueueElement> queue;
    
    public SearchDS(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
        String appId = System.getenv(Constants.SEARCHAPPID) != null ? System.getenv(Constants.SEARCHAPPID) : "";
        this.queue =  new MyQueueFactory().create(getQueueName() + appId, nodeConf, controlService.curatorClient);
    }

    public abstract String getAppName();

    public String constructor() {
        SearchEngineConstructorParam param = new SearchEngineConstructorParam();
        configureParam(param);
        SearchEngineConstructorResult result = EurekaUtil.sendMe(SearchEngineConstructorResult.class, param, getAppName(), EurekaConstants.CONSTRUCTOR, nodeConf);
        return result.error;
    }

    public String destructor() {
        SearchEngineConstructorParam param = new SearchEngineConstructorParam();
        configureParam(param);
        SearchEngineConstructorResult result = EurekaUtil.sendMe(SearchEngineConstructorResult.class, param, getAppName(), EurekaConstants.DESTRUCTOR, nodeConf);
        return result.error;
    }

    public String clear() {
        SearchEngineConstructorParam param = new SearchEngineConstructorParam();
        configureParam(param);
        SearchEngineConstructorResult result = EurekaUtil.sendMe(SearchEngineConstructorResult.class, param, getAppName(), EurekaConstants.CLEAR, nodeConf);
        return result.error;
    }

    public String drop() {
        SearchEngineConstructorParam param = new SearchEngineConstructorParam();
        configureParam(param);
        SearchEngineConstructorResult result = EurekaUtil.sendMe(SearchEngineConstructorResult.class, param, getAppName(), EurekaConstants.DROP, nodeConf);
        return result.error;
    }

    public SearchEngineIndexResult indexme(String md5, FileObject dbfilename, Map<String, String> metadata, String lang, String classification, IndexFiles index, InmemoryMessage message) {
        Map<String, String> md = metadata;
        String[] str = new String[md.keySet().size()];
        int i = 0;
        for (String name : md.keySet()) {
            String value = md.get(name);
            str[i++] = name + "=" + value;
        }
        SearchEngineIndexParam param = new SearchEngineIndexParam();
        configureParam(param);
        param.md5 = md5;
        param.dbfilename = dbfilename;
        param.metadata = str;
        param.lang = lang;
        param.message = message;
        param.classification = classification;

        return EurekaUtil.sendMe(SearchEngineIndexResult.class, param, getAppName(), EurekaConstants.INDEX, nodeConf);
    }

    public ResultItem[] searchme(String str, String searchtype) {
        SearchEngineSearchParam param = new SearchEngineSearchParam();
        configureParam(param);
        param.str = str;
        param.searchtype = searchtype;

        SearchEngineSearchResult result = EurekaUtil.sendMe(SearchEngineSearchResult.class, param, getAppName(), EurekaConstants.SEARCH, nodeConf);
        return getResultItems(result);
    }

    public ResultItem[] searchsimilar(String id, String searchtype) {
        SearchEngineSearchParam param = new SearchEngineSearchParam();
        configureParam(param);
        param.str = id;
        param.searchtype = searchtype;

        SearchEngineSearchResult result = EurekaUtil.sendMe(SearchEngineSearchResult.class, param, getAppName(), EurekaConstants.SEARCHMLT, nodeConf);
        return getResultItems(result);
    }

    /**
     * Delete the entry with given id
     * 
     * @param str md5 id
     */

    public void delete(String str) {
        SearchEngineDeleteParam param = new SearchEngineDeleteParam();
        configureParam(param);
        param.delete = str;

        SearchEngineDeleteResult result = EurekaUtil.sendMe(SearchEngineDeleteResult.class, param, getAppName(), EurekaConstants.DELETE, nodeConf);

    }

    private ResultItem[] getResultItems(SearchEngineSearchResult result) {
        SearchResult[] results = result.results;
        ResultItem[] strarr = new ResultItem[results.length + 1];
        strarr[0] = IndexFiles.getHeaderSearch();
        try {
            int i = 1;
            Set<String> md5s = new HashSet<>();
            for (SearchResult res : results) {
                md5s.add(res.md5);
            }
            IndexFilesDao indexFilesDao = new IndexFilesDao(nodeConf, controlService);
            Map<String, IndexFiles> indexmd5s = indexFilesDao.getByMd5(md5s);
            for (SearchResult res : results) {
                String md5 = res.md5;
                IndexFiles indexmd5 = indexmd5s.get(md5);

                String filename = indexmd5.getFilelocation();
                FileLocation aFl = indexmd5.getaFilelocation();
                log.info("Hit {}.{} : {} {}",i ,md5, filename, res.score);
                FileLocation maybeFl = null;
                /*
                        try {
                            // slow
                            maybeFl = Traverse.getExistingLocalFilelocationMaybe(indexmd5);
                        } catch (Exception e) {
                            log.error(Constants.EXCEPTION, e);
                        }
                 */
                strarr[i] = IndexFiles.getSearchResultItem(indexmd5, res.lang, res.score, res.highlights, res.metadata, controlService.nodename, aFl);
                i++;
            }
        } catch (Exception e) {
            log.error(roart.common.constants.Constants.EXCEPTION, e);
        }
        return strarr;
    }

    private void configureParam(SearchEngineParam param) {
        param.configname = controlService.getConfigName();
        param.configid = controlService.getConfigId();
        param.iconf = controlService.iconf;
        param.iserver = nodeConf.getInmemoryServer();
        if (Constants.REDIS.equals(nodeConf.getInmemoryServer())) {
            param.iconnection = nodeConf.getInmemoryRedis();
        }
    }

    public abstract String getQueueName();

    public void indexmeQueue(QueueElement element, String md5, FileObject dbfilename, Map<String, String> metadata, String lang, String classification, IndexFiles index, InmemoryMessage message) {
        Map<String, String> md = metadata;
        String[] str = new String[md.keySet().size()];
        int i = 0;
        for (String name : md.keySet()) {
            String value = md.get(name);
            str[i++] = name + "=" + value;
        }
        SearchEngineIndexParam param = new SearchEngineIndexParam();
        configureParam(param);
        param.md5 = md5;
        param.dbfilename = dbfilename;
        param.metadata = str;
        param.lang = lang;
        param.message = message;
        param.classification = classification;

        element.setOpid(OperationConstants.INDEX);
        element.setSearchEngineIndexParam(param);
        queue.offer(element);
    }

    public MyQueue<QueueElement> getQueue() {
        return queue;
    }

}

