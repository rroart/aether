package roart.function;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MySet;
import roart.common.config.MyConfig;
import roart.common.constants.Constants;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.service.ServiceParam;
import roart.common.synchronization.MyLock;
import roart.common.zkutil.ZKMessageUtil;
import roart.database.IndexFilesDao;
import roart.dir.Traverse;
import roart.queue.Queues;
import roart.queue.TraverseQueueElement;
import roart.service.ControlService;
import roart.util.MyAtomicLong;
import roart.util.MyAtomicLongs;
import roart.util.MyCollections;
import roart.util.MyList;
import roart.util.MyLists;
import roart.util.MyLockFactory;
import roart.util.MySets;

public abstract class AbstractFunction {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private ServiceParam param;

    public AbstractFunction(ServiceParam param) {
        this.param = param;
    }

    public abstract List doClient(ServiceParam param);

    @SuppressWarnings("rawtypes")
    public List<List> clientDo(ServiceParam el) {
        IndexFilesDao indexFilesDao = new IndexFilesDao();
        synchronized (ControlService.writelock) {
            try {
                MyLock lock = null;
                if (MyConfig.conf.getZookeeper() != null && !MyConfig.conf.wantZookeeperSmall()) {
                    lock = MyLockFactory.create();
                    lock.lock(Constants.GLOBALLOCK);
                }
                ServiceParam.Function function = el.function;
                String filename = el.add;
                //boolean reindex = el.reindex;
                //boolean newmd5 = el.md5change;
                log.info("function " + function + " " + filename + " " + el.reindex);

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

                String myid = ControlService.getMyId();
                String filesetnewid = Constants.FILESETNEWID + myid;
                MySet<String> filesetnew = MySets.get(filesetnewid);
                //MySets.put(filesetnewid, filesetnew);

                String notfoundsetid = Constants.NOTFOUNDSETID + myid;
                MySet<String> notfoundset = MySets.get(notfoundsetid);
                //MySets.put(notfoundsetid, notfoundset);

                String retlistid = Constants.RETLISTID + myid;
                MyList<ResultItem> retlist = MyLists.get(retlistid);
                //MyLists.put(retlistid, retlist);

                String retnotlistid = Constants.RETNOTLISTID + myid;
                MyList<ResultItem> retnotlist = MyLists.get(retnotlistid);
                //MyLists.put(retnotlistid, retnotlist);

                String traversecountid = Constants.TRAVERSECOUNT + myid;
                MyAtomicLong traversecount = MyAtomicLongs.get(traversecountid);

                String filestodosetid = Constants.FILESTODOSETID + myid;
                MySet<String> filestodoset = MySets.get(filestodosetid);
                //MyLists.put(retnotlistid, retnotlist);
                Queues.workQueues.add(filestodoset);

                Traverse traverse = new Traverse(myid, el, retlistid, retnotlistid, filesetnewid, MyConfig.conf.getDirListNot(), notfoundsetid, filestodosetid, traversecountid, false);

                // filesystem
                // reindexsuffix
                // index
                // reindexdate
                // filesystemlucenenew

                traverse(filename, traverse);
                //traverse.traverse(filename, this);

                TimeUnit.SECONDS.sleep(5);

                while ((traversecount.get() + Queues.queueSize() + Queues.runSize()) > 0 /* || filestodoset.size() > 0 */) {
                    TimeUnit.SECONDS.sleep(5);
                    Queues.queueStat();
                }

                for (String str : filestodoset.getAll()) {
                    System.out.println("todo " + str);
                }

                for (String ret : Queues.convertTimeoutQueue) {
                    retConvertTimeoutList.add(new ResultItem(ret));
                }

                Queues.resetConvertTimeoutQueue();
                //IndexFilesDao.commit();
                while (indexFilesDao.dirty() > 0) {
                    TimeUnit.SECONDS.sleep(60);
                }

                for (ResultItem file : retlist.getAll()) {
                    retList.add(file);
                }

                for (ResultItem s : retnotlist.getAll()) {
                    retNotList.add(s);
                }

                for (String file : notfoundset.getAll()) {
                    retNotExistList.add(new ResultItem(file));
                }

                for (String s : filesetnew.getAll()) {
                    retNewFilesList.add(new ResultItem(s));
                }

                // TODO set clear

                MyCollections.remove(retlistid);
                MyCollections.remove(retnotlistid);
                MyCollections.remove(notfoundsetid);
                MyCollections.remove(filesetnewid);
                MyCollections.remove(filestodosetid);
                MyCollections.remove(traversecountid);
                Queues.workQueues.remove(filestodoset);

                retlistlist.add(retList);
                retlistlist.add(retNotList);
                retlistlist.add(retNewFilesList);
                retlistlist.add(retDeletedList);
                retlistlist.add(retTikaTimeoutList);
                retlistlist.add(retNotExistList);
                if (MyConfig.conf.getZookeeper() != null && !MyConfig.conf.wantZookeeperSmall()) {
                    ZKMessageUtil.dorefresh(ControlService.nodename);
                    lock.unlock();
                    //ClientRunner.notify("Sending refresh request");
                }
                return retlistlist;
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
        return null;        
    }

    protected void traverse(String filename, Traverse traverse) {
    }

    public boolean indexFilter(IndexFiles index, TraverseQueueElement trav) {
        return true;
    }
}
