package roart.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.impl.MyAtomicLong;
import roart.common.collections.impl.MyAtomicLongs;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.Location;
import roart.common.model.ResultItem;
import roart.common.service.ServiceParam;
import roart.common.util.FsUtil;
import roart.common.util.QueueUtil;
import roart.database.IndexFilesDao;
import roart.dir.Traverse;
import roart.filesystem.FileSystemDao;
import roart.queue.Queues;
import roart.service.ControlService;
import roart.common.queue.QueueElement;

public class TraverseUtil {
    private static Logger log = LoggerFactory.getLogger(TraverseUtil.class);

    public static boolean isMaxed(String myid, ServiceParam element, NodeConfig nodeConf, ControlService controlService) {
        int max = nodeConf.getReindexLimit();
        int maxindex = nodeConf.getIndexLimit();
        // counter not working / used?
        MyAtomicLong indexcount = MyAtomicLongs.get(Constants.INDEXCOUNT + myid, nodeConf, controlService.curatorClient); 
        boolean isMaxed = false;
        if (element.reindex && max > 0 && indexcount.get() >= max) {
            isMaxed = true;
        }		
        if (!element.reindex && maxindex > 0 && indexcount.get() >= maxindex) {
            isMaxed = true;
        }
        return isMaxed;
    }

    // old, probably oudated by overlapping?
    public static Set<String> dupdir (FileObject fileObject, NodeConfig nodeConf, ControlService controlService) throws Exception {
        IndexFilesDao indexFilesDao = new IndexFilesDao(nodeConf, controlService);
        boolean onlyone = false;
        boolean error = false;
        int count = 0;
        long size = 0;
        Set<String> retset = new HashSet<>();
        HashSet<String> md5set = new HashSet<>();
        FileObject dir = new FileSystemDao(nodeConf, controlService).get(fileObject);
        List<FileObject> listDir = new FileSystemDao(nodeConf, controlService).listFiles(dir);
        for (FileObject fo : listDir) {
            String filename = new FileSystemDao(nodeConf, controlService).getAbsolutePath(fo);
            if (filename.length() > Traverse.MAXFILE) {
                log.info("Too large filesize {}", filename);
                error = true;
                continue;
            }
            if (new FileSystemDao(nodeConf, controlService).isDirectory(fo)) {
                retset.addAll(dupdir(fo, nodeConf, controlService));
            } else {
                if (error) {
                    continue;
                }
                // TODO batch
                String md5 = indexFilesDao.getMd5ByFilename(fo);
                // TODO batch
                IndexFiles files = indexFilesDao.getByMd5(md5);
                if (files == null) {
                    error = true;
                    continue;
                }
                if (md5 == null) {
                    error = true;
                    continue;
                }
                if (indexFilesDao.getByMd5(md5).getFilelocations().size() < 2) {
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

    public static List<ResultItem> notindexed(ServiceParam el, IndexFilesDao indexFilesDao, ControlService controlService) throws Exception {
        List<ResultItem> retlist = new ArrayList<>();
        ResultItem ri = new ResultItem();
        retlist.add(IndexFiles.getHeader());
        List<IndexFiles> indexes = indexFilesDao.getAll();
        log.info("sizes {}", indexes.size());
        for (IndexFiles index : indexes) {
            Boolean indexed = index.getIndexed();
            if (indexed != null && indexed.booleanValue() == true) {
                continue;
            }
            FileLocation aFl = index.getaFilelocation();
            ri = IndexFiles.getResultItem(index, index.getLanguage(), controlService.nodename, aFl);
            retlist.add(ri);
        }
        return retlist;
    }

    public static List<ResultItem> indexed(ServiceParam el, IndexFilesDao indexFilesDao, ControlService controlService) throws Exception {
        List<ResultItem> retlist = new ArrayList<ResultItem>();
        List<IndexFiles> indexes = indexFilesDao.getAll();
        log.info("sizes {}", indexes.size());
        for (IndexFiles index : indexes) {
            Boolean indexed = index.getIndexed();
            for (FileLocation filename : index.getFilelocations()) {
                if (indexed != null) {
                    if (indexed.booleanValue()) {
                        FileLocation aFl = index.getaFilelocation();
                        retlist.add(IndexFiles.getResultItem(index, index.getLanguage(), controlService.nodename, aFl));
                    }
                }
            }
        }
        return retlist;
    }

    // retset will be returned empty
    // dirset will contain a map of directories, and the md5 files is contains
    // fileset will contain a map of md5 and the directories it has files in
    public static Set<String> doList2 (Map<String, HashSet<String>> dirset, Map<String, HashSet<String>> fileset, NodeConfig nodeConf, ControlService controlService) throws Exception {
        Set<String> retset = new HashSet<>();
        IndexFilesDao indexFilesDao = new IndexFilesDao(nodeConf, controlService);

        List<IndexFiles> files = indexFilesDao.getAll();
        log.info("size {}", files.size());
        for (IndexFiles file : files) {
            String md5 = file.getMd5();
            for (FileLocation filename : file.getFilelocations()) {
                FileObject tmpfile = new FileSystemDao(nodeConf, controlService).get(FsUtil.getFileObject(filename));
                FileObject dir = new FileSystemDao(nodeConf, controlService).getParent(tmpfile);
                String dirname = new FileSystemDao(nodeConf, controlService).getAbsolutePath(dir);
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

    public static FileLocation getExistingLocalFilelocation(IndexFiles i, NodeConfig nodeConf, ControlService controlService) {
        // next up : locations
        Set<FileLocation> filelocations = i.getFilelocations();
        if (filelocations == null) {
            return null;
        }
        for (FileLocation filelocation : filelocations) {
            Location node = FsUtil.getLocation(filelocation.getNode());
            String filename = filelocation.getFilename();
            if (node == null || node.equals(controlService.nodename)) {
                FileObject file = new FileSystemDao(nodeConf, controlService).get(new FileObject(node, filename));
                if (file == null) {
                    log.error("try file {}", filename);
                    continue;
                }
                if (new FileSystemDao(nodeConf, controlService).exists(file)) {
                    return filelocation;			
                }
            }
        }
        return null;
    }

    @Deprecated
    public static FileLocation getExistingLocalFilelocationMaybe(IndexFiles i, NodeConfig nodeConf, ControlService controlService) {
        // next up : locations
        FileLocation fl = getExistingLocalFilelocation(i, nodeConf, controlService);
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

    /**
     * Check if filename/directory is among excluded directories
     * 
     * @param fileObject of file to be tested
     * @param dirlist an array of excluded directories
     * @return whether excluded
     */

    public static boolean indirlist(FileObject fileObject, FileObject[] dirlist) {
        if (dirlist == null) {
            return false;
        }
        for (int i = 0; i < dirlist.length; i++) {
            if (!fileObject.location.equals(dirlist[i].location)) {
                continue;
            }
            if (!dirlist[i].object.isEmpty() && fileObject.object.indexOf(dirlist[i].object)>=0) {
                return true;
            }
        }
        return false;
    }

    public static void doCounters(QueueElement trav, int value, NodeConfig nodeConf, ControlService controlService) {
        MyAtomicLong total = MyAtomicLongs.get(Constants.TRAVERSECOUNT + new Queues(null, null).prefix(), nodeConf, controlService.curatorClient);
        total.addAndGet(value);
        MyAtomicLong count = MyAtomicLongs.get(QueueUtil.traversecount(trav.getMyid()), nodeConf, controlService.curatorClient);
        count.addAndGet(value);
        log.debug("Count {} {}", value, trav.getFileObject());
    }

    public static FileObject indirlistmatch(FileObject fileObject, FileObject[] dirlist) {
        if (dirlist == null) {
            return null;
        }
        for (int i = 0; i < dirlist.length; i++) {
            if (!fileObject.location.equals(dirlist[i].location)) {
                continue;
            }
            if (!dirlist[i].object.isEmpty() && fileObject.object.indexOf(dirlist[i].object)>=0) {
                return dirlist[i];
            }
        }
        return null;
    }

    public static void log(QueueElement element, String text) {
        log.info("QueueElement {} {} roundtrip {}", element.getMd5(), text, (System.currentTimeMillis() - element.getTimestamp()) / 1000);
    }

    public static boolean checklimits(IndexFiles index, NodeConfig nodeConf, ServiceParam element) {
        long now = System.currentTimeMillis();
        long indextime = index.getTimestamp() != null ? Long.valueOf(index.getTimestamp()) : 0;
        long checktime = index.getChecked() != null ? Long.valueOf(index.getChecked()) : 0;
        long indextimelimit = nodeConf.getIndexTimeLimit() * 86400 * 1000;
        long indexretrytimelimit = nodeConf.getIndexRetryTimeLimit() * 86400 * 1000;
        if (indextime > 0 && now - indextime < indextimelimit) {
            return false;
        }
        if (checktime > 0 && now - checktime < indexretrytimelimit) {
            return false;
        }

        if (FilterUtil.hasFailedLimit(nodeConf, index, element)) return false;

        return true;
    }
}
