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
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.Location;
import roart.common.model.ResultItem;
import roart.common.service.ServiceParam;
import roart.common.util.FsUtil;
import roart.database.IndexFilesDao;
import roart.dir.Traverse;
import roart.filesystem.FileSystemDao;
import roart.hcutil.GetHazelcastInstance;
import roart.service.ControlService;

public class TraverseUtil {
    public static Logger log = LoggerFactory.getLogger(TraverseUtil.class);

    public static boolean isMaxed(String myid, ServiceParam element, NodeConfig nodeConf) {
        int max = nodeConf.getReindexLimit();
        int maxindex = nodeConf.getIndexLimit();
        MyAtomicLong indexcount = MyAtomicLongs.get(Constants.INDEXCOUNT + myid, nodeConf, ControlService.curatorClient, GetHazelcastInstance.instance()); 
        boolean isMaxed = false;
        if (element.reindex && max > 0 && indexcount.get() > max) {
            isMaxed = true;
        }		
        if (!element.reindex && maxindex > 0 && indexcount.get() > maxindex) {
            isMaxed = true;
        }
        return isMaxed;
    }

    // old, probably oudated by overlapping?
    public static Set<String> dupdir (FileObject fileObject, NodeConfig nodeConf) throws Exception {
        IndexFilesDao indexFilesDao = new IndexFilesDao(nodeConf);
        boolean onlyone = false;
        boolean error = false;
        int count = 0;
        long size = 0;
        Set<String> retset = new HashSet<>();
        HashSet<String> md5set = new HashSet<>();
        FileObject dir = new FileSystemDao(nodeConf).get(fileObject);
        List<FileObject> listDir = new FileSystemDao(nodeConf).listFiles(dir);
        for (FileObject fo : listDir) {
            String filename = new FileSystemDao(nodeConf).getAbsolutePath(fo);
            if (filename.length() > Traverse.MAXFILE) {
                log.info("Too large filesize {}", filename);
                error = true;
                continue;
            }
            if (new FileSystemDao(nodeConf).isDirectory(fo)) {
                retset.addAll(dupdir(fo, nodeConf));
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

    public static List<ResultItem> notindexed(ServiceParam el, IndexFilesDao indexFilesDao) throws Exception {
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
            ri = IndexFiles.getResultItem(index, index.getLanguage(), ControlService.nodename, aFl);
            retlist.add(ri);
        }
        return retlist;
    }

    public static List<ResultItem> indexed(ServiceParam el, IndexFilesDao indexFilesDao) throws Exception {
        List<ResultItem> retlist = new ArrayList<ResultItem>();
        List<IndexFiles> indexes = indexFilesDao.getAll();
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

    // retset will be returned empty
    // dirset will contain a map of directories, and the md5 files is contains
    // fileset will contain a map of md5 and the directories it has files in
    public static Set<String> doList2 (Map<String, HashSet<String>> dirset, Map<String, HashSet<String>> fileset, NodeConfig nodeConf) throws Exception {
        Set<String> retset = new HashSet<>();
        IndexFilesDao indexFilesDao = new IndexFilesDao(nodeConf);

        List<IndexFiles> files = indexFilesDao.getAll();
        log.info("size {}", files.size());
        for (IndexFiles file : files) {
            String md5 = file.getMd5();
            for (FileLocation filename : file.getFilelocations()) {
                FileObject tmpfile = new FileSystemDao(nodeConf).get(FsUtil.getFileObject(filename));
                FileObject dir = new FileSystemDao(nodeConf).getParent(tmpfile);
                String dirname = new FileSystemDao(nodeConf).getAbsolutePath(dir);
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

    public static FileLocation getExistingLocalFilelocation(IndexFiles i, NodeConfig nodeConf) {
        // next up : locations
        Set<FileLocation> filelocations = i.getFilelocations();
        if (filelocations == null) {
            return null;
        }
        for (FileLocation filelocation : filelocations) {
            Location node = FsUtil.getLocation(filelocation.getNode());
            String filename = filelocation.getFilename();
            if (node == null || node.equals(ControlService.nodename)) {
                FileObject file = new FileSystemDao(nodeConf).get(new FileObject(node, filename));
                if (file == null) {
                    log.error("try file {}", filename);
                    continue;
                }
                if (new FileSystemDao(nodeConf).exists(file)) {
                    return filelocation;			
                }
            }
        }
        return null;
    }

    @Deprecated
    public static FileLocation getExistingLocalFilelocationMaybe(IndexFiles i, NodeConfig nodeConf) {
        // next up : locations
        FileLocation fl = getExistingLocalFilelocation(i, nodeConf);
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
     * @param dirlistnot2 an array of excluded directories
     * @return whether excluded
     */

    public static boolean indirlistnot(FileObject fileObject, FileObject[] dirlistnot2) {
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

}
