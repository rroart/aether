package roart.search;

import roart.queue.IndexQueueElement;
import roart.queue.Queues;
import roart.service.ControlService;
import roart.service.SearchService;
import roart.util.TraverseUtil;
import roart.lang.LanguageDetect;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.searchengine.SearchEngineDeleteResult;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.common.searchengine.SearchResult;
import roart.common.util.JsonUtil;
import roart.database.IndexFilesDao;
import roart.hcutil.GetHazelcastInstance;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.inmemory.model.Inmemory;
import roart.common.collections.MyList;
import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyLists;
import roart.common.collections.impl.MyQueues;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.inmemory.factory.InmemoryFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Search {
    private static Logger log = LoggerFactory.getLogger(Search.class);

    private NodeConfig nodeConf;

    public Search(NodeConfig nodeConf) {
        super();
        this.nodeConf = nodeConf;
    }

    //public static int indexme(String type, String md5, InputStream inputStream) {
    public void indexme(IndexQueueElement el) {
        if (el == null) {
            log.error("empty queue");
            return;
        }
        // vulnerable spot
        new Queues(nodeConf).incIndexs();
        long now = System.currentTimeMillis();

        String type = el.type;
        String md5 = el.md5;
        //InputStream inputStream = el.inputStream;
        IndexFiles dbindex = el.index;
        FileObject dbfilename = el.dbfilename;
        Map<String, String> metadata = el.metadata;
        String lang = el.lang;
        InmemoryMessage message = el.message;
        String classification = el.index.getClassification();
        MyQueue<ResultItem> retlist = MyQueues.get(el.retlistid, nodeConf, ControlService.curatorClient, GetHazelcastInstance.instance());
        MyQueue<ResultItem> retlistnot = MyQueues.get(el.retlistnotid, nodeConf, ControlService.curatorClient, GetHazelcastInstance.instance());

        int retsize = 0;

        try {
            retsize = new SearchDao(nodeConf).indexme(type, md5, dbfilename, metadata, lang, classification, dbindex, message);
        } catch (Exception e) {
            log.error(roart.common.constants.Constants.EXCEPTION, e);
            dbindex.setNoindexreason("index exception " + e.getClass().getName());
            retsize = -1;
        } catch (OutOfMemoryError e) {
            System.gc();
            log.error("Error " + Thread.currentThread().getId() + " " + dbfilename);
            log.error(roart.common.constants.Constants.ERROR, e);
            dbindex.setNoindexreason(dbindex.getNoindexreason() + "outofmemory " + e.getClass().getName() + " ");
            retsize = -1;
        }

        if (retsize < 0) {
            //dbindex.setNoindexreason(Constants.EXCEPTION); // later, propagate the exception
            FileLocation aFl = el.index.getaFilelocation();
            ResultItem ri = IndexFiles.getResultItem(el.index, el.index.getLanguage(), ControlService.nodename, aFl);
            ri.get().set(IndexFiles.FILENAMECOLUMN, dbfilename);
            retlistnot.offer(ri);
        } else {

            log.info("size2 " + md5 + " " + retsize);
            el.size = retsize;
            dbindex.setIndexed(Boolean.TRUE);
            dbindex.setTimestamp("" + System.currentTimeMillis());
            dbindex.setConvertsw(el.convertsw);
            //dbindex.save();
            long time = System.currentTimeMillis() - now;
            dbindex.setTimeindex("" + time);
            log.info("timerStop filename " + time);

            FileLocation maybeFl = TraverseUtil.getExistingLocalFilelocationMaybe(el.index, nodeConf);
            ResultItem ri = IndexFiles.getResultItem(el.index, lang, ControlService.nodename, maybeFl);
            ri.get().set(IndexFiles.FILENAMECOLUMN, dbfilename);
            retlist.offer(ri);

        }
        dbindex.setPriority(1);
        // file unlock dbindex
        // config with finegrained distrib
        new IndexFilesDao(nodeConf).add(dbindex);
        new Queues(nodeConf).decIndexs();

        if (el.message != null) {
            Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
            inmemory.delete(el.message);
        }
    }

    public ResultItem[] searchme(String str, String searchtype) {
        return new SearchDao(nodeConf).searchme(str, searchtype);
    }

    // not yet usable, lacking termvector
    public ResultItem[] searchsimilar(String md5i, String searchtype) {
        return new SearchDao(nodeConf).searchsimilar(md5i, searchtype);
    }

    // not yet usable, lacking termvector
    public void docsLike(int id, int max) throws IOException {
    }

    public void deleteme(String str) {
        new SearchDao(nodeConf).deleteme(str);
    }

    // outdated, did run once, had a bug which made duplicates
    public static List<String> removeDuplicate() throws Exception {
        return null;
    }//End of removeDuplicate method

    // outdated, used once, when bug added filename instead of md5
    public static List<String> cleanup2() throws Exception {
        return null;
    }//End of removeDuplicate method

}
