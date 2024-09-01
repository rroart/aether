package roart.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MyCollections;
import roart.common.collections.MyList;
import roart.common.collections.MyMap;
import roart.common.collections.MyQueue;
import roart.common.collections.MySet;
import roart.common.collections.impl.MyAtomicLong;
import roart.common.collections.impl.MyAtomicLongs;
import roart.common.collections.impl.MyLists;
import roart.common.collections.impl.MyMaps;
import roart.common.collections.impl.MyQueues;
import roart.common.collections.impl.MySets;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.service.ServiceParam;
import roart.common.service.ServiceParam.Function;
import roart.common.synchronization.MyLock;
import roart.common.synchronization.impl.MyLockFactory;
import roart.common.util.FsUtil;
import roart.common.util.QueueUtil;
import roart.common.util.TimeUtil;
import roart.common.zkutil.ZKMessageUtil;
import roart.common.zkutil.ZKUtil;
import roart.database.IndexFilesDao;
import roart.dir.Traverse;
import roart.dir.TraverseFile;
import roart.queue.Queues;
import roart.service.ControlService;
import roart.util.FilterUtil;
import roart.util.TraverseUtil;
import roart.common.queue.QueueElement;

public abstract class AbstractFunction {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private ServiceParam param;

    protected NodeConfig nodeConf;

    protected ControlService controlService;

    private IndexFilesDao indexFilesDao;

    public AbstractFunction(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        this.param = param;
        this.nodeConf = nodeConf;
        this.controlService = controlService;
        this.indexFilesDao = new IndexFilesDao(nodeConf, controlService);
    }

    public abstract List doClient(ServiceParam param);

    @SuppressWarnings("rawtypes")
    public List<List> clientDo(ServiceParam el) {
        IndexFilesDao indexFilesDao = new IndexFilesDao(nodeConf, controlService);
        try {
            /*
                MyLock lock = null;
                if (MyConfig.conf.getZookeeper() != null && !MyConfig.conf.wantZookeeperSmall()) {
                    lock = MyLockFactory.create();
                    lock.lock(Constants.GLOBALLOCK);
                }
             */
            ServiceParam.Function function = el.function;
            String filename = el.path;
            //boolean reindex = el.reindex;
            //boolean newmd5 = el.md5change;
            log.info("function " + function + " " + filename + " " + el.reindex);

            Set<String> filestodoSet = new HashSet<>();

            List<List> retlistlist = new ArrayList<>();
            List<ResultItem> retList = new ArrayList<>();
            retList.add(IndexFiles.getHeader());
            List<ResultItem> retConvertTimeoutList = new ArrayList<>();
            retConvertTimeoutList.add(new ResultItem("Convert timeout"));
            List<ResultItem> retNotList = new ArrayList<>();
            retNotList.add(IndexFiles.getHeader());
            List<ResultItem> retNewFilesList = new ArrayList<>();
            retNewFilesList.add(new ResultItem("New file"));
            List<ResultItem> retDeletedList = new ArrayList<>();
            retDeletedList.add(new ResultItem("Deleted"));
            List<ResultItem> retNotExistList = new ArrayList<>();
            retNotExistList.add(new ResultItem("File does not exist"));
            List<ResultItem> retNotConvertedList = new ArrayList<>();
            retNotConvertedList.add(new ResultItem("File not converted"));
            List<String> retChangedList = new ArrayList<>();
            retChangedList.add("File changed");
            List<String> notfoundList = new ArrayList<>();
            List<String> newfileList = new ArrayList<>();
            Queues queues = new Queues(nodeConf, controlService);
            String myid = controlService.getMyId();
            String filesetnewid = QueueUtil.filesetnewQueue(myid);
            MyQueue<String> newfileQueue = MyQueues.get(filesetnewid, nodeConf, controlService.curatorClient);
            //MySets.put(filesetnewid, filesetnew);

            String notfoundsetid = QueueUtil.notfoundsetQueue(myid);
            MyQueue<String> notfoundQueue = MyQueues.get(notfoundsetid, nodeConf, controlService.curatorClient);
            //MySets.put(notfoundsetid, notfoundset);

            String retlistid = QueueUtil.retlistQueue(myid);
            MyQueue retQueue = MyQueues.get(retlistid, nodeConf, controlService.curatorClient);
            //MyLists.put(retlistid, retlist);

            String retnotlistid = QueueUtil.retlistnotQueue(myid);
            MyQueue<ResultItem> retnotQueue = MyQueues.get(retnotlistid, nodeConf, controlService.curatorClient);
            //MyLists.put(retnotlistid, retnotlist);

            String traversecountid = QueueUtil.traversecount(myid);
            MyAtomicLong traversecount = MyAtomicLongs.get(traversecountid, nodeConf, controlService.curatorClient);

            MyMap<String, String> mymaps = queues.getTraverseCountMap();
            mymaps.put(traversecountid, "" + System.currentTimeMillis());
            
            String filestodosetid = QueueUtil.filestodoQueue(myid);
            MyQueue<String> filestodoQueue = MyQueues.get(filestodosetid, nodeConf, controlService.curatorClient);
            String filesdonesetid = QueueUtil.filesdoneQueue(myid);
            MyQueue<String> filesdoneQueue = MyQueues.get(filesdonesetid, nodeConf, controlService.curatorClient);

            String traversedsetid = QueueUtil.traversedSet("");
            MySet<String> traversedSet = MySets.get(traversedsetid, nodeConf, controlService.curatorClient);

            String deletedsetid = QueueUtil.deletedQueue(myid);
            MyQueue<ResultItem> deletedQueue = MyQueues.get(deletedsetid, nodeConf, controlService.curatorClient);

            String changedsetid = QueueUtil.changedQueue(myid);
            MyQueue<String> changedQueue = MyQueues.get(changedsetid, nodeConf, controlService.curatorClient);

            String notconvertedsetid = QueueUtil.notconvertedQueue(myid);
            MyQueue<ResultItem> notconvertedQueue = MyQueues.get(notconvertedsetid, nodeConf, controlService.curatorClient);

            //MyLists.put(retnotlistid, retnotlist);
            //queues.workQueues.add(filestodoSet);

            List<String> queueList = new ArrayList<>();
            //queueList.add(filesdonesetid);
            queueList.add(notfoundsetid);
            queueList.add(retlistid);
            queueList.add(retnotlistid);
            queueList.add(traversecountid); // TODO not a queue, rename
            queueList.add(filestodosetid);
            queueList.add(filesdonesetid);
            queueList.add(deletedsetid);
            queueList.add(changedsetid);
            queueList.add(notconvertedsetid);
            
            Set<String> traversed = traversedb(null, myid);
            if (param.md5checknew != true) {
                for (String file : traversed) {
                    traversedSet.add(file);
                }
            }
            
            Traverse traverse = new Traverse(myid, el, nodeConf.getDirListNot(), traversecountid, false, nodeConf, controlService);

            // filesystem
            // reindexsuffix
            // index
            // reindexdate
            // filesystemlucenenew

            Runnable runnable = () -> { traverse(filename, traverse); };
            new Thread(runnable).start();
            //traverse.traverse(filename, this);

            // something other, from traverse
            TimeUnit.SECONDS.sleep(15);

            boolean doLoop = true;
            
            // TODO try again
            while (doLoop /* || filestodoset.size() > 0 */) {
                mylogs(queues, traversecount);
		// queues.getConvertQueueSize()
                TimeUnit.SECONDS.sleep(15);
                fetchFromQueues(filestodoSet, retList, retNotList, notfoundList, newfileList, retDeletedList, retChangedList, retNotConvertedList, newfileQueue,
                        notfoundQueue, retQueue, retnotQueue, filestodoQueue, filesdoneQueue, deletedQueue, changedQueue, notconvertedQueue);
                for (String queue : queueList) {
                    String path = ZKUtil.getAppidPath(Constants.QUEUES) + queue;
                    Stat stat = controlService.curatorClient.checkExists().forPath(path);
                    if (stat == null) {
                        controlService.curatorClient.create().creatingParentsIfNeeded().forPath(path, new byte[0]);
                    } else {
                        controlService.curatorClient.setData().forPath(path);
                    }
                }
                mymaps.put(traversecountid, "" + System.currentTimeMillis());
                doLoop = traversecount.get() > 0;
            }
            
            TimeUnit.SECONDS.sleep(15);

            mylogs(queues, traversecount);
            fetchFromQueues(filestodoSet, retList, retNotList, notfoundList, newfileList,retDeletedList, retChangedList, retNotConvertedList, newfileQueue,
                    notfoundQueue, retQueue, retnotQueue, filestodoQueue, filesdoneQueue, deletedQueue, changedQueue, notconvertedQueue);

            for (String str : filestodoSet) {
                log.info("todo {}", str);
            }

            //IndexFilesDao.commit();
            while (indexFilesDao.dirty() > 0) {
                TimeUnit.SECONDS.sleep(60);
            }

            for (String file : notfoundList) {
                retNotExistList.add(new ResultItem(file));
            }

            for (String s : newfileList) {
                retNewFilesList.add(new ResultItem(s));
            }

            // TODO set clear

            MyCollections.remove(retlistid);
            MyCollections.remove(retnotlistid);
            MyCollections.remove(notfoundsetid);
            MyCollections.remove(filesetnewid);
            MyCollections.remove(filestodosetid);
            MyCollections.remove(filesdonesetid);
            MyCollections.remove(traversecountid);
            MyCollections.remove(deletedsetid);
            MyCollections.remove(changedsetid);
            MyCollections.remove(notconvertedsetid);
            //queues.workQueues.remove(filestodoSet);
            mymaps.remove(traversecountid);

            // indexed and not indexed
            if (retList.size() > 1) {
                retlistlist.add(retList);
            }
            if (retNotList.size() > 1) {            
                retlistlist.add(retNotList);
            }
            
            // not used
            if (retNewFilesList.size() > 1) {            
                retlistlist.add(retNewFilesList);
            }
            
            // del?
            if (retDeletedList.size() > 1) {            
                retlistlist.add(retDeletedList);
            }
            
            // not exist
            if (retNotExistList.size() > 1) {            
                retlistlist.add(retNotExistList);
            }
            
            // unconverted
            if (retNotConvertedList.size() > 1) {            
                retlistlist.add(retNotConvertedList);
            }
            
            // changed file
            if (retChangedList.size() > 1) {            
                retlistlist.add(retChangedList);
            }
            
            if (nodeConf.getZookeeper() != null && !nodeConf.wantZookeeperSmall()) {
                ZKMessageUtil.dorefresh(controlService.nodename);
                //lock.unlock();
                //ClientRunner.notify("Sending refresh request");
            }
            return retlistlist;
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return null;        
    }

    private void fetchFromQueues(Set<String> filestodoSet, List<ResultItem> retList, List<ResultItem> retNotList,
            List<String> notfoundList, List<String> newfileList, List<ResultItem> retDeletedList, List<String> retChangedList, List<ResultItem> retNotConvertedList, MyQueue<String> newfileQueue,
            MyQueue<String> notfoundQueue, MyQueue retQueue, MyQueue<ResultItem> retnotQueue,
            MyQueue<String> filestodoQueue, MyQueue<String> filesdoneQueue, MyQueue<ResultItem> deletedQueue, MyQueue<String> changedQueue, MyQueue<ResultItem> notconvertedQueue) {
        fromQueueToList(retList, retQueue, ResultItem.class);
        fromQueueToList(retNotList, retnotQueue, ResultItem.class);
        fromQueueToList(filestodoSet, filestodoQueue, String.class);
        Set<String> filesdoneSet = new HashSet<>();
        fromQueueToList(filesdoneSet, filesdoneQueue, String.class);
        filestodoSet.removeAll(filesdoneSet);
        fromQueueToList(newfileList, newfileQueue, String.class);
        fromQueueToList(notfoundList, notfoundQueue, String.class);
        fromQueueToList(retDeletedList, deletedQueue, ResultItem.class);
        fromQueueToList(retChangedList, changedQueue, String.class);
        fromQueueToList(retNotConvertedList, notconvertedQueue, ResultItem.class);
    }

    private void mylogs(Queues queues, MyAtomicLong traversecount) {
        log.info("Queues {} {} {} {}", queues.getListingQueueSize(), queues.getTraverseQueueSize(), queues.getConvertQueueSize(), queues.getIndexQueueSize());
        log.info("Queues {} {} {} {}", queues.getMyListings().get(), queues.getMyTraverses().get(), queues.getMyConverts().get(), queues.getMyIndexs().get());
        log.info("My queues {} {}", traversecount.get(), queues.total());
        queues.queueStat();
    }

    private <T> void fromQueueToList(Collection<T> resultList, MyQueue<T> resultQueue, Class<T> clazz) {
        while (true) {
            T s = null;
            try {
                s = resultQueue.poll(clazz);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e); 
            }
            if (s == null) {
                break;
            }
            resultList.add(s);
        }
    }

    protected void traverse(String filename, Traverse traverse) {
    }

    public boolean indexFilter(IndexFiles index, QueueElement trav) {
        return true;
    }

    public Set<String> traversedb(AbstractFunction function, String myid) throws Exception {
        Set<String> done = new HashSet<>();
        indexFilesDao.getAllFiles();
        List<IndexFiles> indexes = indexFilesDao.getAll();
        for (IndexFiles index : indexes) {
            try {
                /*
                if (TraverseUtil.isMaxed(myid, param, nodeConf, controlService)) {
                    break;
                }
                if (!TraverseUtil.checklimits(index, nodeConf, param)) {
                    continue;
                }
                if (!FilterUtil.indexFilter(index, param)) {
                    continue;
                }
                */

                for (FileLocation fl : index.getFilelocations()) {
                    done.add(fl.toString());
                }
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e); 
                TimeUtil.sleep(10);
            }
        }
        return done;
    }
}
