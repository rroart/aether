package roart.function;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import roart.common.collections.MySet;
import roart.common.config.MyConfig;
import roart.common.constants.Constants;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.Files;
import roart.common.model.ResultItem;
import roart.common.service.ServiceParam;
import roart.common.synchronization.MyLock;
import roart.common.util.FsUtil;
import roart.common.zkutil.ZKMessageUtil;
import roart.database.IndexFilesDao;
import roart.dir.Traverse;
import roart.filesystem.FileSystemDao;
import roart.model.MyLockFactory;
import roart.model.MySets;
import roart.search.SearchDao;
import roart.service.ControlService;

public class ConsistentClean extends AbstractFunction {

    private FileSystemDao fileSystemDao = new FileSystemDao();
    
    private IndexFilesDao indexFilesDao = new IndexFilesDao();
    
    private SearchDao searchDao = new SearchDao();
    
    public ConsistentClean(ServiceParam param) {
        super(param);
    }

    public FileSystemDao getFileSystemDao() {
        return fileSystemDao;
    }

    public void setFileSystemDao(FileSystemDao fileSystemDao) {
        this.fileSystemDao = fileSystemDao;
    }

    public IndexFilesDao getIndexFilesDao() {
        return indexFilesDao;
    }

    public void setIndexFilesDao(IndexFilesDao indexFilesDao) {
        this.indexFilesDao = indexFilesDao;
    }

    public SearchDao getSearchDao() {
        return searchDao;
    }

    public void setSearchDao(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    @Override
    public List doClient(ServiceParam param) {
        IndexFilesDao indexFilesDao = new IndexFilesDao();
        boolean clean = param.clean;
        List<ResultItem> delList = new ArrayList<>();
        List<ResultItem> nonexistList = new ArrayList<>();
        List<ResultItem> newList = new ArrayList<>();
        ResultItem ri = new ResultItem("Filename delete");
        delList.add(ri);
        ri = new ResultItem("Filename nonexist");
        nonexistList.add(ri);
        ri = new ResultItem("Filename new");
        newList.add(ri);

        Set<FileObject> delfileset = new HashSet<>();

        Set<String> filesetnew = new HashSet<String>(); // just a dir list
        //Set<String> newset = new HashSet<String>();

        String myid = ControlService.getMyId();
        String newsetid = "newsetid"+myid;
        MySet<String> newset = MySets.get(newsetid);
        //MySets.put(newsetid, newset);

        String notfoundsetid = "notfoundsetid"+myid;
        MySet<String> notfoundset = MySets.get(notfoundsetid);
        //MySets.put(notfoundsetid, notfoundset);

        String md5sdoneid = "md5sdoneid"+myid;
        MySet<String> md5sdoneset = MySets.get(md5sdoneid);

        //Traverse traverse = new Traverse(myid, param, null, null, newsetid, MyConfig.conf.getDirListNot(), notfoundsetid, null, null, true);
        String path = param.file;

        List<IndexFiles> indexes;
        synchronized (ControlService.writelock) {
            try {
                /*
                MyLock lock = null;
                if (MyConfig.conf.getZookeeper() != null && !MyConfig.conf.wantZookeeperSmall()) {
                    lock = MyLockFactory.create();
                    lock.lock(Constants.GLOBALLOCK);
                }
                */
                Set<IndexFiles> ifs = new HashSet<>();
                indexes = indexFilesDao.getAll();
                extracted(delList, delfileset, path, indexes, ifs, true, clean);

                while (indexFilesDao.dirty() > 0) {
                    TimeUnit.SECONDS.sleep(60);
                }

                // TODO why copied from below?
                // TODO or indexes?
                for (IndexFiles i : ifs) {
                    MyLock filelock = i.getLock();
                    if (filelock != null) {
                        filelock.unlock();
                        i.setLock(null);
                    } else {
                        System.out.println("locknull");
                    }
                }

                if (MyConfig.conf.getZookeeper() != null && !MyConfig.conf.wantZookeeperSmall()) {
                    ZKMessageUtil.dorefresh(ControlService.nodename);
                    //lock.unlock();
                    //ClientRunner.notify("Sending refresh request");
                }
                /*
                traverse.traverse(null, this);

                for (String file : newset.getAll()) {
                    newList.add(new ResultItem(file));
                }
                for (String file : notfoundset.getAll()) {
                    nonexistList.add(new ResultItem(file));
                }
                */

                if (false && clean) {
                    //DbRunner.doupdate = false;
                    for (FileObject filename : delfileset) {
                        String md5 = indexFilesDao.getMd5ByFilename(filename);
                        // common3?
                        if (md5 != null) {
                            MyLock lock2 = MyLockFactory.create();
                            lock2.lock(md5);
                            IndexFiles ifile = indexFilesDao.getByMd5(md5);
                            FileLocation fl = new FileLocation(filename.location.toString(), filename.object, null);
                            boolean removed = ifile.removeFilelocation(fl);
                            //log.info("fls2 size " + removed + ifile.getFilelocations().size());
                            //IndexFilesDao.add(ifile);
                            ifs.add(ifile);
                        } else {
                            log.info("trying the hard way, no md5 for " + filename);
                            for (IndexFiles index : indexes) {
                                FileLocation fl = new FileLocation(filename.location.toString(), filename.object, null);
                                if (index.getFilelocations().contains(fl)) {
                                    boolean removed = index.removeFilelocation(fl);
                                    //log.info("fls3 size " + removed + index.getFilelocations().size());
                                    //IndexFilesDao.add(index);
                                    ifs.add(index);
                                }
                            }
                        }
                    }
                    //DbRunner.doupdate = true;
                    //IndexFilesDao.commit();
                    while (indexFilesDao.dirty() > 0) {
                        TimeUnit.SECONDS.sleep(60);
                    }
                    for (IndexFiles i : ifs) {
                        MyLock filelock = i.getLock();
                        if (filelock != null) {
                            filelock.unlock();
                            i.setLock(null);
                        }
                    }

                    if (MyConfig.conf.getZookeeper() != null && !MyConfig.conf.wantZookeeperSmall()) {
                        ZKMessageUtil.dorefresh(ControlService.nodename);
                        //lock.unlock();
                    }
                }

            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }

        List<List> retlistlist = new ArrayList<>();
        log.error("Not exists {}", delList);
        retlistlist.add(delList);
        retlistlist.add(nonexistList);
        retlistlist.add(newList);
        return retlistlist;
    }

    void extracted(List<ResultItem> delList, Set<FileObject> delfileset, final String path, final List<IndexFiles> indexes,
            Set<IndexFiles> ifs, boolean checkexist, boolean clean) throws Exception {
        log.info("size {}", indexes.size());
        for (IndexFiles index : indexes) {
            MyLock lock = MyLockFactory.create();
            lock.lock(index.getMd5());               
            // if last deletepathdb
            // common 2
            // find out if to be deleted
            Set<FileLocation> deletes = new HashSet<>();
            for (FileLocation fl : index.getFilelocations()) {
                //System.out.println("cont2 " + fl.toString() + " " + path);
                // path is nonnull AND checkexist false
                // path is null AND checkexist true
                if (path == null || fl.toString().contains(path)) {
                    if (checkexist) {
                        String filename = fl.getFilename();
                        FileObject fo = fileSystemDao.get(FsUtil.getFileObject(fl));
                        //System.out.println("f " + fileSystemDao + " " + fo);
                        if (!fileSystemDao.exists(fo)) {
                            //System.out.println("notexists");
                            delList.add(new ResultItem(filename));
                            delfileset.add(fo);
                            deletes.add(fl);
                        }
                    } else {
                        delList.add(new ResultItem(fl.toString()));
                        FileObject fo = fileSystemDao.get(FsUtil.getFileObject(fl));
                        delfileset.add(fo);
                        deletes.add(fl);
                    }
                }
            }
            // common 3
            // then delete from collection, and eventually remove from search
            if (clean && !deletes.isEmpty()) {
                index.getFilelocations().removeAll(deletes);
                if (index.getFilelocations().isEmpty()) {
                    indexFilesDao.delete(index);
                    searchDao.deleteme(index.getMd5());
                } else {
                    indexFilesDao.add(index);
                }
                for (FileLocation fl : deletes) {
                    indexFilesDao.delete(new Files(fl.toString(), index.getMd5()));
                }
                ifs.add(index);
            }
            lock.unlock();
        }
    }

}
