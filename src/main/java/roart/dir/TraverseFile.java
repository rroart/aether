package roart.dir;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import roart.queue.Queues;
import roart.queue.TikaQueueElement;
import roart.queue.ClientQueueElement;
import roart.queue.ClientQueueElement.Function;
import roart.queue.TraverseQueueElement;
import roart.service.ControlService;
import roart.service.SearchService;
import roart.thread.ClientRunner;
import roart.thread.TikaRunner;
import roart.database.IndexFilesDao;
import roart.filesystem.FileSystemDao;
import roart.model.FileObject;
import roart.model.IndexFiles;
import roart.model.FileLocation;
import roart.model.ResultItem;
import roart.model.SearchDisplay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import roart.util.ConfigConstants;
import roart.util.Constants;
import roart.util.ExecCommand;
import roart.util.MyAtomicLong;
import roart.util.MyAtomicLongs;
import roart.util.MyLock;
import roart.util.MyLockFactory;
import roart.util.MySet;
import roart.util.MySets;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.tika.metadata.Metadata;


public class TraverseFile {

    private static Logger log = LoggerFactory.getLogger(TraverseFile.class);

    public static void handleFo3(TraverseQueueElement trav)
            throws Exception {
{
            //          if (ControlService.zookeepersmall) {
            //              handleFo2(retset, md5set, filename);
            //          } else {
            // config with finegrained distrib
    String filename = trav.getFilename();
    FileObject fo = FileSystemDao.get(filename);

    MyLock lock2 = MyLockFactory.create();
    //lock2.lock(fo.toString());
             log.debug("timer");
            String md5 = IndexFilesDao.getMd5ByFilename(filename);
            log.debug("info " + md5 + " " + filename);
            IndexFiles files = null;
            MyLock lock = MyLockFactory.create();
            lock.lock(md5);
           if (trav.getClientQueueElement().md5change == true || md5 == null) {
                try {
                    if (!FileSystemDao.exists(fo)) {
                        throw new FileNotFoundException("File does not exist " + filename);
                    }
                    if (trav.getClientQueueElement().function != Function.INDEX) {
                        InputStream fis = FileSystemDao.getInputStream(fo);
                        md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex( fis );
                        fis.close();
                        if (files == null) {
                            //z.lock(md5);
                            // get read file
                            files = IndexFilesDao.getByMd5(md5);
                        }
                        // modify write file
                        files.addFile(filename);
                        log.info("adding md5 file " + filename);
                    }
                    // calculatenewmd5 and nodbchange are never both true
                    if (md5 == null || (trav.getClientQueueElement().md5change == true && !md5.equals(md5))) {
                        if (trav.getNewsetid() != null) {
                            MySet<String> newset = (MySet<String>) MySets.get(trav.getNewsetid()); 
                            newset.add(filename);
                        }
                    }
                } catch (FileNotFoundException e) {
                    log.error(Constants.EXCEPTION, e);
                    MySet<String> notfoundset = (MySet<String>) MySets.get(trav.getNotfoundsetid()); 
                    notfoundset.add(filename);
                    return;
                } catch (Exception e) {
                    log.info("Error: " + e.getMessage());
                    log.error(Constants.EXCEPTION, e);
                    return;
                }
            } else {
                log.debug("timer2");
                // get read file
                files = IndexFilesDao.getByMd5(md5);
                log.debug("info " + md5 + " " + files);
            }
            files.setLock(lock);
            LinkedBlockingQueue lockqueue = new LinkedBlockingQueue();
            files.setLockqueue(lockqueue);
        String md5sdoneid = "md5sdoneid"+trav.getMyid();
        MySet<String> md5sdoneset = MySets.get(md5sdoneid);
        
            if (md5sdoneset != null && !md5sdoneset.add(md5)) {
                return;
            }
            if (trav.getClientQueueElement().function == Function.FILESYSTEM) {
                return;
            }
            if (!Traverse.filterindex(files, trav)) {
                return;
            }
            try {
                indexsingle(trav, md5, filename, files);
            } catch (Exception e) {
                log.info("Error: " + e.getMessage());
                log.error(Constants.EXCEPTION, e);
            }
                LinkedBlockingQueue lockqueue2 = (LinkedBlockingQueue) files.getLockqueue();
             MyLock unlock = (MyLock) lockqueue2.poll(3600, TimeUnit.SECONDS);
            lock.unlock();
            files.setLock(null);
            //md5set.add(md5);
        }
    }

    public static void indexsingle(TraverseQueueElement trav,
            String md5, String filename, IndexFiles index) {
        int maxfailed = ControlService.configMap.get(ControlService.Config.FAILEDLIMIT);
        if (!trav.getClientQueueElement().reindex && maxfailed > 0) {
        int failed = index.getFailed();
        if (failed >= maxfailed) {
            log.info("failed too much for " + md5);
            return;
        }
        }

        log.info("index " + md5 + " " + filename);
        //InputStream stream = null;
        // file modify still
        index.setTimeoutreason("");
        index.setFailedreason("");
        index.setNoindexreason("");
        TikaQueueElement e = new TikaQueueElement(filename, filename, md5, index, trav.getRetlistid(), trav.getRetnotlistid(), new Metadata(), trav.getClientQueueElement().display);
        Queues.tikaQueue.add(e);
        //size = doTika(filename, filename, md5, index, retlist);
    }

}
