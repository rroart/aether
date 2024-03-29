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
import roart.common.synchronization.MyLock;
import roart.common.synchronization.MyObjectLock;
import roart.common.synchronization.MyObjectLockData;
import roart.common.util.JsonUtil;
import roart.common.util.LockUtils;
import roart.common.util.QueueUtil;
import roart.common.zkutil.ZKUtil;
import roart.database.IndexFilesDao;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.collections.MyList;
import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyLists;
import roart.common.collections.impl.MyQueues;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.inmemory.common.Inmemory;
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
import roart.common.queue.QueueElement;

public class Search {
    private static Logger log = LoggerFactory.getLogger(Search.class);

    private NodeConfig nodeConf;

    private ControlService controlService;

    public Search(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
    }

    //public static int indexme(String type, String md5, InputStream inputStream) {
    public void indexme(QueueElement el) {
        if (el == null) {
            log.error("empty queue");
            return;
        }
        // vulnerable spot
        //new Queues(nodeConf, controlService).incIndexs();
        long now = System.currentTimeMillis();

        String md5 = el.getMd5();
        //InputStream inputStream = el.inputStream;
        IndexFiles dbindex = el.getIndexFiles();
        FileObject filename = el.getFileObject();
        Map<String, String> metadata = el.getMetadata();
        String lang = dbindex.getLanguage();
        InmemoryMessage message = el.getMessage();
        String classification = dbindex.getClassification();
        MyQueue<ResultItem> retlist = MyQueues.get(QueueUtil.retlistQueue(el.getMyid()), nodeConf, controlService.curatorClient);
        MyQueue<ResultItem> retlistnot = MyQueues.get(QueueUtil.retlistnotQueue(el.getMyid()), nodeConf, controlService.curatorClient);

        int retsize = 0;

        try {
            retsize = new SearchDao(nodeConf, controlService).indexme(md5, filename, metadata, lang, classification, dbindex, message);
        } catch (Exception e) {
            log.error(roart.common.constants.Constants.EXCEPTION, e);
            dbindex.setNoindexreason("index exception " + e.getClass().getName());
            retsize = -1;
        } catch (OutOfMemoryError e) {
            System.gc();
            log.error("Error " + Thread.currentThread().getId() + " " + filename);
            log.error(roart.common.constants.Constants.ERROR, e);
            dbindex.setNoindexreason(dbindex.getNoindexreason() + "outofmemory " + e.getClass().getName() + " ");
            retsize = -1;
        }

        if (retsize < 0) {
            //dbindex.setNoindexreason(Constants.EXCEPTION); // later, propagate the exception
            FileLocation aFl = dbindex.getaFilelocation();
            ResultItem ri = IndexFiles.getResultItem(dbindex, dbindex.getLanguage(), controlService.nodename, aFl);
            ri.get().set(IndexFiles.FILENAMECOLUMN, filename);
            retlistnot.offer(ri);
        } else {

            log.info("size2 " + md5 + " " + retsize);
            dbindex.setIndexed(Boolean.TRUE);
            dbindex.setTimestamp("" + System.currentTimeMillis());
            //dbindex.save();
            long time = System.currentTimeMillis() - now;
            dbindex.setTimeindex("" + time);
            log.info("timerStop filename " + time);

            FileLocation maybeFl = TraverseUtil.getExistingLocalFilelocationMaybe(dbindex, nodeConf, controlService);
            ResultItem ri = IndexFiles.getResultItem(dbindex, lang, controlService.nodename, maybeFl);
            ri.get().set(IndexFiles.FILENAMECOLUMN, filename);
            retlist.offer(ri);

        }
        dbindex.setPriority(1);
        // file unlock dbindex
        // config with finegrained distrib
        dbindex.setChecked("" + System.currentTimeMillis());             
        new IndexFilesDao(nodeConf, controlService).add(dbindex);
        TraverseUtil.log(el, "indexed");
        
        if (el.getMessage() != null) {
            Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
            inmemory.delete(el.getMessage());
            try {
                controlService.curatorClient.delete().forPath(ZKUtil.getCommonPathIfCommon(Constants.DATA) + el.getMessage().getId());
            } catch (Exception e) {
                log.info(Constants.EXCEPTION, e);
            }
        }
        MyLock lock = dbindex.getLock();
        if (lock != null) {
            //lock.unlock();
        } else {
            log.error("Missing lock");
        }
        MyQueue<String> filesdoneset = (MyQueue<String>) MyQueues.get(QueueUtil.filesdoneQueue(el.getMyid()), nodeConf, controlService.curatorClient); 
        filesdoneset.offer(filename.toString());
    }

    public ResultItem[] searchme(String str, String searchtype) {
        return new SearchDao(nodeConf, controlService).searchme(str, searchtype);
    }

    // not yet usable, lacking termvector
    public ResultItem[] searchsimilar(String md5i, String searchtype) {
        return new SearchDao(nodeConf, controlService).searchsimilar(md5i, searchtype);
    }

    // not yet usable, lacking termvector
    public void docsLike(int id, int max) throws IOException {
    }

    public void deleteme(String str) {
        new SearchDao(nodeConf, controlService).deleteme(str);
    }

    // outdated, did run once, had a bug which made duplicates
    public static List<String> removeDuplicate() throws Exception {
        return null;
    }//End of removeDuplicate method

    // outdated, used once, when bug added filename instead of md5
    public static List<String> cleanup2() throws Exception {
        return null;
    }//End of removeDuplicate method

    public void indexmeQueue(QueueElement el) {
        if (el == null) {
            log.error("empty queue");
            return;
        }
        //LockUtils.fix(el.getIndexFiles(), nodeConf.getLocker(), controlService.curatorClient);
        if (el.getOpid() == null) {
            // vulnerable spot
            //new Queues(nodeConf, controlService).incIndexs();

            String md5 = el.getMd5();
            //InputStream inputStream = el.inputStream;
            IndexFiles dbindex = el.getIndexFiles();
            FileObject filename = el.getFileObject();
            Map<String, String> metadata = el.getMetadata();
            String lang = dbindex.getLanguage();
            InmemoryMessage message = el.getMessage();
            String classification = dbindex.getClassification();

            int retsize = 0;

            try {
                el.setQueue(QueueUtil.getIndexQueue());
                new SearchDao(nodeConf, controlService).indexmeQueue(el, md5, filename, metadata, lang, classification, dbindex, message);
                return;
            } catch (Exception e) {
                log.error(roart.common.constants.Constants.EXCEPTION, e);
                dbindex.setNoindexreason("index exception " + e.getClass().getName());
                retsize = -1;
            } catch (OutOfMemoryError e) {
                System.gc();
                log.error("Error " + Thread.currentThread().getId() + " " + filename);
                log.error(roart.common.constants.Constants.ERROR, e);
                dbindex.setNoindexreason(dbindex.getNoindexreason() + "outofmemory " + e.getClass().getName() + " ");
                retsize = -1;
            }
        } else {
            MyQueue<ResultItem> retlist = MyQueues.get(QueueUtil.retlistQueue(el.getMyid()), nodeConf, controlService.curatorClient);
            MyQueue<ResultItem> retlistnot = MyQueues.get(QueueUtil.retlistnotQueue(el.getMyid()), nodeConf, controlService.curatorClient);
            int retsize = el.getSearchEngineIndexResult().size;
            IndexFiles dbindex = el.getIndexFiles();
            String md5 = el.getMd5();
            FileObject filename = el.getFileObject();
            if (retsize < 0) {
                //dbindex.setNoindexreason(Constants.EXCEPTION); // later, propagate the exception
                FileLocation aFl = dbindex.getaFilelocation();
                ResultItem ri = IndexFiles.getResultItem(dbindex, dbindex.getLanguage(), controlService.nodename, aFl);
                ri.get().set(IndexFiles.FILENAMECOLUMN, filename);
                retlistnot.offer(ri);
            } else {

                log.info("size2 " + md5 + " " + retsize);
                dbindex.setIndexed(Boolean.TRUE);
                dbindex.setTimestamp("" + System.currentTimeMillis());
                //dbindex.save();
                log.info("timerStop filename {}", dbindex.getTimeindex());
                String lang = dbindex.getLanguage();

                FileLocation maybeFl = TraverseUtil.getExistingLocalFilelocationMaybe(dbindex, nodeConf, controlService);
                ResultItem ri = IndexFiles.getResultItem(dbindex, lang, controlService.nodename, maybeFl);
                ri.get().set(IndexFiles.FILENAMECOLUMN, filename);
                retlist.offer(ri);

            }
            dbindex.setPriority(1);
            // file unlock dbindex
            // config with finegrained distrib
            dbindex.setChecked("" + System.currentTimeMillis());             
            new IndexFilesDao(nodeConf, controlService).add(dbindex);
            TraverseUtil.log(el, "indexed");

            if (el.getMessage() != null) {
                Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
                inmemory.delete(el.getMessage());
                try {
                    controlService.curatorClient.delete().forPath(ZKUtil.getCommonPathIfCommon(Constants.DATA) + el.getMessage().getId());
                } catch (Exception e) {
                    log.info(Constants.EXCEPTION, e);
                }
            }

            MyQueue<String> filesdoneset = (MyQueue<String>) MyQueues.get(QueueUtil.filesdoneQueue(el.getMyid()), nodeConf, controlService.curatorClient); 
            filesdoneset.offer(filename.toString());

            MyObjectLockData lockdata = dbindex.getObjectlock();
            if (lockdata != null) {
                //lock.unlock();
            } else {
                log.error("Missing lock");
            }
        }
    }
}
