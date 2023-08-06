package roart.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MyCollections;
import roart.common.collections.MyList;
import roart.common.collections.MyQueue;
import roart.common.collections.MySet;
import roart.common.collections.impl.MyAtomicLong;
import roart.common.collections.impl.MyAtomicLongs;
import roart.common.collections.impl.MyLists;
import roart.common.collections.impl.MyQueues;
import roart.common.collections.impl.MySets;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.service.ServiceParam;
import roart.common.synchronization.MyLock;
import roart.common.synchronization.impl.MyLockFactory;
import roart.common.util.QueueUtil;
import roart.common.zkutil.ZKMessageUtil;
import roart.database.IndexFilesDao;
import roart.dir.Traverse;
import roart.hcutil.GetHazelcastInstance;
import roart.queue.Queues;
import roart.queue.TraverseQueueElement;
import roart.service.ControlService;

public abstract class AbstractFunction {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private ServiceParam param;

    protected NodeConfig nodeConf;

    protected ControlService controlService;

    public AbstractFunction(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        this.param = param;
        this.nodeConf = nodeConf;
        this.controlService = controlService;
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
            String filename = el.add;
            //boolean reindex = el.reindex;
            //boolean newmd5 = el.md5change;
            log.info("function " + function + " " + filename + " " + el.reindex);

            Set<String> filestodoSet = new HashSet<>();

            List<List> retlistlist = new ArrayList<>();
            List<ResultItem> retList = new ArrayList<>();
            retList.add(IndexFiles.getHeader());
            List<ResultItem> retTikaTimeoutList = new ArrayList<>();
            retTikaTimeoutList.add(new ResultItem("Tika timeout"));
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
            List<String> notfoundList = new ArrayList<>();
            List<String> newfileList = new ArrayList<>();
            Queues queues = new Queues(nodeConf, controlService);
            String myid = controlService.getMyId();
            String filesetnewid = QueueUtil.filesetnewQueue(myid);
            MyQueue<String> newfileQueue = MyQueues.get(filesetnewid, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
            //MySets.put(filesetnewid, filesetnew);

            String notfoundsetid = QueueUtil.notfoundsetQueue(myid);
            MyQueue<String> notfoundQueue = MyQueues.get(notfoundsetid, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
            //MySets.put(notfoundsetid, notfoundset);

            String retlistid = QueueUtil.retlistQueue(myid);
            MyQueue retQueue = MyQueues.get(retlistid, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
            //MyLists.put(retlistid, retlist);

            String retnotlistid = QueueUtil.retlistnotQueue(myid);
            MyQueue<ResultItem> retnotQueue = MyQueues.get(retnotlistid, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
            //MyLists.put(retnotlistid, retnotlist);

            String traversecountid = QueueUtil.traversecount(myid);
            MyAtomicLong traversecount = MyAtomicLongs.get(traversecountid, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());

            String filestodosetid = QueueUtil.filestodoQueue(myid);
            MyQueue<String> filestodoQueue = MyQueues.get(filestodosetid, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
            String filesdonesetid = QueueUtil.filesdoneQueue(myid);
            MyQueue<String> filesdoneQueue = MyQueues.get(filestodosetid, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance());
            //MyLists.put(retnotlistid, retnotlist);
            queues.workQueues.add(filestodoSet);

            Traverse traverse = new Traverse(myid, el, nodeConf.getDirListNot(), traversecountid, false, nodeConf, controlService);

            // filesystem
            // reindexsuffix
            // index
            // reindexdate
            // filesystemlucenenew

            traverse(filename, traverse);
            //traverse.traverse(filename, this);

            TimeUnit.SECONDS.sleep(5);

            while ((traversecount.get() + queues.queueSize() + queues.runSize()) > 0 /* || filestodoset.size() > 0 */) {
                log.info("My queues {} {} {} {} {} {} {}", traversecount.get(), queues.getListingQueueSize(), queues.getTraverseQueueSize(), queues.getConvertQueueSize(), queues.getIndexQueueSize(), queues.getMyConverts().get(), queues.getMyIndexs().get());
                TimeUnit.SECONDS.sleep(5);
                queues.queueStat();
                fromQueueToList(retList, retQueue, ResultItem.class);
                fromQueueToList(retNotList, retnotQueue, ResultItem.class);
                fromQueueToList(filestodoSet, filestodoQueue, String.class);
                Set<String> filesdoneSet = new HashSet<>();
                fromQueueToList(filesdoneSet, filesdoneQueue, String.class);
                filestodoSet.removeAll(filesdoneSet);
                fromQueueToList(newfileList, newfileQueue, String.class);
                fromQueueToList(notfoundList, notfoundQueue, String.class);
            }

            for (String str : filestodoSet) {
                System.out.println("todo " + str);
            }

            for (String ret : queues.convertTimeoutQueue) {
                retConvertTimeoutList.add(new ResultItem(ret));
            }

            queues.resetConvertTimeoutQueue();
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
            MyCollections.remove(traversecountid);
            queues.workQueues.remove(filestodoSet);

            retlistlist.add(retList);
            retlistlist.add(retNotList);
            retlistlist.add(retNewFilesList);
            retlistlist.add(retDeletedList);
            retlistlist.add(retTikaTimeoutList);
            retlistlist.add(retNotExistList);
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

    private <T> void fromQueueToList(Collection<T> resultList, MyQueue<T> resultQueue, Class<T> clazz) {
        while (true) {
            T s = resultQueue.poll(clazz);
            if (s == null) {
                break;
            }
            resultList.add(s);
        }
    }

    protected void traverse(String filename, Traverse traverse) {
    }

    public boolean indexFilter(IndexFiles index, TraverseQueueElement trav) {
        return true;
    }
}
