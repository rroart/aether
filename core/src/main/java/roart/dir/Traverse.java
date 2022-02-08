package roart.dir;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import roart.queue.Queues;
import roart.queue.TikaQueueElement;
import roart.queue.TraverseQueueElement;
import roart.service.ControlService;
import roart.service.SearchService;
import roart.thread.TikaRunner;
import roart.common.collections.MySet;
import roart.common.config.ConfigConstants;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.filesystem.MyFile;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.model.SearchDisplay;
import roart.common.service.ServiceParam;
import roart.common.synchronization.MyLock;
import roart.common.util.ExecCommand;
import roart.common.util.FsUtil;
import roart.database.IndexFilesDao;
import roart.filesystem.FileSystemDao;
import roart.function.AbstractFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import roart.util.MyAtomicLong;
import roart.util.MyAtomicLongs;
import roart.util.MyLockFactory;
import roart.util.MyQueue;
import roart.util.MyQueues;
import roart.util.MySets;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.tika.metadata.Metadata;
import roart.common.model.Location;

public class Traverse {

    private static final int MAXFILE = 500;

    private static Logger log = LoggerFactory.getLogger(Traverse.class);

    /**
     * Check if filename/directory is among excluded directories
     * 
     * @param fileObject of file to be tested
     * @param dirlistnot2 an array of excluded directories
     * @return whether excluded
     */

    private static boolean indirlistnot(FileObject fileObject, FileObject[] dirlistnot2) {
        if (dirlistnot2 == null) {
            return false;
        }
        for (int i = 0; i < dirlistnot2.length; i++) {
            if (!fileObject.location.equals(dirlistnot2[i].location)) {
                continue;
            }
            if (!dirlistnot2[i].object.isEmpty() && fileObject.object.indexOf(dirlistnot2[i].object)>=0) {
                return true;
            }
        }
        return false;
    }

    // cases:
    // INDEX
    // reindex true, path null, not used
    // reindex true, path non-null, reindex given path
    // reindex false, path null, for indexing all unindexed found in all fs (eventually from db)
    // reindex false, path non-null, for indexing all unindexed found in given path 
    // REINDEXLANGUAGE
    // reindex true
    // path null
    // on lang
    // (eventually from db) 
    // REINDEXSUFFIX
    // reindex true or false
    // path null
    // on suffix
    // (eventually from db) 
    // REINDEXDATE
    // reindex true
    // path null
    // on date
    // (eventually from db) 
    // FILESYSTEM
    // check all fs for new if null path
    // check gives fs for new with path
    // FILESYSTEMLUCENENEW
    // check all fs for new if null path
    // check gives fs for new with path, eventually also compute new md5
    // then index if new

    public Set<String> doList(FileObject fileObject) throws Exception {
        Set<String> retset = new HashSet<>();
        if (isMaxed(myid, element)) {
            return retset;
        }

        if (indirlistnot(fileObject, dirlistnot)) {
            return retset;
        }
        //HashSet<String> md5set = new HashSet<String>();
        long time0 = System.currentTimeMillis();
        FileObject dir = FileSystemDao.get(fileObject);
        List<MyFile> listDir = FileSystemDao.listFilesFull(dir);
        long time1 = System.currentTimeMillis();
        log.debug("Time0 {}", usedTime(time1, time0));
        //log.info("dir " + dirname);
        //log.info("listDir " + listDir.length);
        if (listDir == null) {
            return retset;
        }
        for (MyFile file : listDir) {
            FileObject fo = file.fileObject[0];
            long time2 = System.currentTimeMillis();
            String filename = file.absolutePath;
            // for encoding problems
            if (!file.exists) {
                MySet<String> notfoundset = (MySet<String>) MySets.get(notfoundsetid); 
                notfoundset.add(filename);
                continue;
                //throw new FileNotFoundException("File does not exist " + filename);
            }
            long time3 = System.currentTimeMillis();
            log.debug("Time2 {}", usedTime(time3, time2));
            if (filename.length() > MAXFILE) {
                log.info("Too large filesize {}", filename);
                continue;
            }
            //log.info("file " + filename);
            if (file.isDirectory) {
                log.debug("isdir {}", filename);
                retset.addAll(doList(fo));
            } else {
                retset.add(filename);
                if (!nomd5) {
                    MyQueue<TraverseQueueElement> queue = Queues.getTraverseQueue();
                    TraverseQueueElement trav = new TraverseQueueElement(myid, fo, element, retlistid, retnotlistid, newsetid, notfoundsetid, filestodosetid, traversecountid);
                    MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT);
                    total.addAndGet(1);
                    MyAtomicLong count = MyAtomicLongs.get(traversecountid);
                    count.addAndGet(1);
                    // save
                    queue.offer(trav);
                    log.debug("Count inc {}", trav.getFileobject());
                    //TraverseFile.handleFo3(null, fo);
                }
            }
        }
        /*
        if (dirset != null) {
            dirset.put(dirname, md5set);
        }
         */
        //log.info("retsize " + retset.size());
        return retset;
    }

    // retset will be returned empty
    // dirset will contain a map of directories, and the md5 files is contains
    // fileset will contain a map of md5 and the directories it has files in
    public static Set<String> doList2 (Map<String, HashSet<String>> dirset, Map<String, HashSet<String>> fileset) throws Exception {
        Set<String> retset = new HashSet<>();

        List<IndexFiles> files = IndexFilesDao.getAll();
        log.info("size {}", files.size());
        for (IndexFiles file : files) {
            String md5 = file.getMd5();
            for (FileLocation filename : file.getFilelocations()) {
                FileObject tmpfile = FileSystemDao.get(FsUtil.getFileObject(filename));
                FileObject dir = FileSystemDao.getParent(tmpfile);
                String dirname = FileSystemDao.getAbsolutePath(dir);
                HashSet<String> md5set = dirset.get(dirname);
                if (md5set == null) {
                    md5set = new HashSet<>();
                    dirset.put(dirname, md5set);
                }
                md5set.add(md5);

                HashSet<String> dir5set = fileset.get(md5);
                if (dir5set == null) {
                    dir5set = new HashSet<>();
                    fileset.put(md5, dir5set);
                }
                dir5set.add(dirname);
            }
        }
        return retset;
    }

    // old, probably oudated by overlapping?
    public static Set<String> dupdir (FileObject fileObject) throws Exception {
        boolean onlyone = false;
        boolean error = false;
        int count = 0;
        long size = 0;
        Set<String> retset = new HashSet<>();
        HashSet<String> md5set = new HashSet<>();
        FileObject dir = FileSystemDao.get(fileObject);
        List<FileObject> listDir = FileSystemDao.listFiles(dir);
        for (FileObject fo : listDir) {
            String filename = FileSystemDao.getAbsolutePath(fo);
            if (filename.length() > MAXFILE) {
                log.info("Too large filesize {}", filename);
                error = true;
                continue;
            }
            if (FileSystemDao.isDirectory(fo)) {
                retset.addAll(dupdir(fo));
            } else {
                if (error) {
                    continue;
                }
                String md5 = IndexFilesDao.getMd5ByFilename(fo);
                IndexFiles files = IndexFilesDao.getByMd5(md5);
                if (files == null) {
                    error = true;
                    continue;
                }
                if (md5 == null) {
                    error = true;
                    continue;
                }
                if (IndexFilesDao.getByMd5(md5).getFilelocations().size() < 2) {
                    onlyone = true;
                }
                count++;
                //size+=new File(filename).length();
            }
        }
        if (!error && !onlyone && count>0) {
            retset.add(fileObject + " size " + size);
        }
        return retset;
    }

    public static List<ResultItem> notindexed(ServiceParam el) throws Exception {
        List<ResultItem> retlist = new ArrayList<>();
        ResultItem ri = new ResultItem();
        retlist.add(IndexFiles.getHeader());
        List<IndexFiles> indexes = IndexFilesDao.getAll();
        log.info("sizes {}", indexes.size());
        for (IndexFiles index : indexes) {
            Boolean indexed = index.getIndexed();
            if (indexed != null && indexed.booleanValue() == true) {
                continue;
            }
            FileLocation aFl = index.getaFilelocation();
            ri = IndexFiles.getResultItem(index, index.getLanguage(), ControlService.nodename, aFl);
            retlist.add(ri);
        }
        return retlist;
    }

    public static List<ResultItem> indexed(ServiceParam el) throws Exception {
        List<ResultItem> retlist = new ArrayList<ResultItem>();
        List<IndexFiles> indexes = IndexFilesDao.getAll();
        log.info("sizes {}", indexes.size());
        for (IndexFiles index : indexes) {
            Boolean indexed = index.getIndexed();
            for (FileLocation filename : index.getFilelocations()) {
                if (indexed != null) {
                    if (indexed.booleanValue()) {
                        FileLocation aFl = index.getaFilelocation();
                        retlist.add(IndexFiles.getResultItem(index, index.getLanguage(), ControlService.nodename, aFl));
                    }
                }
            }
        }
        return retlist;
    }

    public static String getExistingLocalFile(IndexFiles i) {
        FileLocation fl = null;
        try {
            fl = getExistingLocalFilelocation(i);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        if (fl != null) {
            return fl.getFilename();
        }
        return null;
    }

    public static FileLocation getExistingLocalFilelocation(IndexFiles i) {
        // next up : locations
        Set<FileLocation> filelocations = i.getFilelocations();
        if (filelocations == null) {
            return null;
        }
        for (FileLocation filelocation : filelocations) {
            Location node = FsUtil.getLocation(filelocation.getNode());
            String filename = filelocation.getFilename();
            if (node == null || node.equals(ControlService.nodename)) {
                FileObject file = FileSystemDao.get(new FileObject(node, filename));
                if (file == null) {
                log.error("try file {}", filename);
                continue;
                }
                if (FileSystemDao.exists(file)) {
                    return filelocation;			
                }
            }
        }
        return null;
    }

    @Deprecated
    public static FileLocation getExistingLocalFilelocationMaybe(IndexFiles i) {
        // next up : locations
        FileLocation fl = getExistingLocalFilelocation(i);
        if (fl != null) {
            return fl;
        }
        Set<FileLocation> filelocations = i.getFilelocations();
        if (filelocations == null || filelocations.size() == 0) {
            return null;
        }
        for (FileLocation filelocation : filelocations) {
            return filelocation;
        }
        return null;
    }

    public Set<String> traversedb(AbstractFunction function, String add) throws Exception {
        MyQueue<TraverseQueueElement> queue = Queues.getTraverseQueue();
        List<IndexFiles> indexes = IndexFilesDao.getAll();
        for (IndexFiles index : indexes) {
            if (isMaxed(myid, element)) {
                break;
            }
            String md5 = index.getMd5();
            //String name = getExistingLocalFile(index);
            FileLocation fl = index.getaFilelocation();
            FileObject filename = FsUtil.getFileObject(fl);
            /*
            if (name == null) {
                log.error("filename should not be null {}", md5);
                continue;
            }
            */
            // TODO check if fo needed
            TraverseQueueElement trav = new TraverseQueueElement(myid, filename, element, retlistid, retnotlistid, newsetid, notfoundsetid, filestodosetid, traversecountid);
            if (!(function.indexFilter(index, trav) > 0)) {
                continue;
            }
            // config with finegrained distrib
            MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT);
            total.addAndGet(1);
            MyAtomicLong count = MyAtomicLongs.get(traversecountid);
            count.addAndGet(1);
            // ?
            //queue.offer(trav);
            String md5sdoneid = "md5sdoneid"+trav.getMyid();
            MySet<String> md5sdoneset = MySets.get(md5sdoneid);
            if (TraverseFile.getDoIndex(trav, md5, index, md5sdoneset, function)) {
            TraverseFile.indexsingle(trav, md5, FsUtil.getFileObject(index.getaFilelocation()), index, null, null);
            }
        }

        return null;
    }

    static boolean isMaxed(String myid, ServiceParam element) {
        int max = MyConfig.conf.getReindexLimit();
        int maxindex = MyConfig.conf.getIndexLimit();
        MyAtomicLong indexcount = MyAtomicLongs.get(Constants.INDEXCOUNT + myid); 
        boolean isMaxed = false;
        if (element.reindex && max > 0 && indexcount.get() > max) {
            isMaxed = true;
        }		
        if (!element.reindex && maxindex > 0 && indexcount.get() > maxindex) {
            isMaxed = true;
        }
        return isMaxed;
    }

    public Set<String> traverse(String add, AbstractFunction function) throws Exception {
        try {
            log.info("function: {}", element.function);
            if (add != null) {
                return doList(FsUtil.getFileObject(add));    
            } else {
                Set<String> retList = new HashSet<>();
                String[] dirlist = MyConfig.conf.getDirList();
                for (int i = 0; i < dirlist.length; i ++) {
                    retList.addAll(doList(FsUtil.getFileObject(dirlist[i])));
                }
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return null;
    }

    //int max = 0;
    //int indexcount = 0;

    String myid;
    ServiceParam element;
    String retlistid = null;
    String retnotlistid = null;
    //List<ResultItem> retList = null;
    //List<ResultItem> retNotList = null;
    String newsetid = null; 
    //	MySet<String> newset = null; 
    //	Map<String, HashSet<String>> dirset;
    String notfoundsetid;
    //boolean reindex = false;
    //boolean calculatenewmd5;
    String filestodosetid;
    String traversecountid;
    boolean nomd5;

    FileObject[] dirlistnot;
    SearchDisplay display;

    //Set<String> md5sdone = new HashSet<String>();

    public Traverse(String myid, ServiceParam element, String retlistid, String retnotlistid, String newsetid, String[] dirlistnotarr, String notfoundsetid, String filestodosetid, String traversecountid, boolean nomd5) {

        this.myid = myid;
        this.element = element;
        this.retlistid = retlistid;
        this.retnotlistid = retnotlistid;
        this.newsetid = newsetid;
        this.notfoundsetid = notfoundsetid;
        //this.reindex = reindex;
        //this.calculatenewmd5 = newmd5;
        this.filestodosetid = filestodosetid;
        this.traversecountid = traversecountid;
        this.nomd5 = nomd5;

        dirlistnotarr = MyConfig.conf.getDirListNot();
        dirlistnot = new FileObject[dirlistnotarr.length];
        int i = 0;
        for (String dir : dirlistnotarr) {
            dirlistnot[i++] = FsUtil.getFileObject(dir);
        }
        //UI ui = element.ui;
        //this.display = SearchService.getSearchDisplay(ui);
    }

    /**
     * Check whether fileobject is local
     * 
     * @param fo Fileobject to be checked
     * @return boolean state
     */

    // ?
    @Deprecated
    public static boolean isLocal(FileObject fo) {
        return FileSystemDao.exists(fo);
    }

    private int usedTime(long time2, long time1) {
        return (int) (time2 - time1); // / 1000;
    }
    
}
