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
import roart.common.model.ResultItem;
import roart.common.service.ServiceParam;
import roart.common.synchronization.MyLock;
import roart.common.util.FsUtil;
import roart.common.zkutil.ZKMessageUtil;
import roart.database.IndexFilesDao;
import roart.dir.Traverse;
import roart.filesystem.FileSystemDao;
import roart.service.ControlService;
import roart.util.MyLockFactory;
import roart.util.MySets;

public class ConsistentClean extends AbstractFunction {

    public ConsistentClean(ServiceParam param) {
        super(param);
    }

    @Override
    public List doClient(ServiceParam param) {
        boolean clean = param.reindex;
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
    
    Traverse traverse = new Traverse(myid, param, null, null, newsetid, MyConfig.conf.getDirListNot(), notfoundsetid, null, null, true);
                    
        List<IndexFiles> indexes;
        try {
                indexes = IndexFilesDao.getAll();
        log.info("size " + indexes.size());
        for (IndexFiles index : indexes) {
                for (FileLocation fl : index.getFilelocations()) {
                        if (fl.isLocal(ControlService.nodename)) {
                                String filename = fl.getFilename();
                                FileObject fo = FileSystemDao.get(FsUtil.getFileObject(fl));
                                if (!FileSystemDao.exists(fo)) {
                                        delList.add(new ResultItem(filename));
                                        delfileset.add(fo);
                                }
                        }
                }
        }
        
        traverse.traverse(null, this);
        
        for (String file : newset.getAll()) {
                newList.add(new ResultItem(file));
        }
        for (String file : notfoundset.getAll()) {
                nonexistList.add(new ResultItem(file));
        }
        
        if (clean) {
            synchronized (ControlService.writelock) {
                MyLock lock = null;
                if (MyConfig.conf.getZookeeper() != null && !MyConfig.conf.wantZookeeperSmall()) {
             lock = MyLockFactory.create();
                lock.lock(Constants.GLOBALLOCK);
                }
                Set<IndexFiles> ifs = new HashSet<>();
            //DbRunner.doupdate = false;
                for (FileObject filename : delfileset) {
                        String md5 = IndexFilesDao.getMd5ByFilename(filename);
                        if (md5 != null) {
                    MyLock lock2 = MyLockFactory.create();
                    lock2.lock(md5);
                                IndexFiles ifile = IndexFilesDao.getByMd5(md5);
                                FileLocation fl = new FileLocation(filename.location.toString(), filename.object, null);
                                boolean removed = ifile.removeFilelocation(fl);
                                //log.info("fls2 size " + removed + ifile.getFilelocations().size());
                IndexFilesDao.add(ifile);
                ifs.add(ifile);
                        } else {
                                log.info("trying the hard way, no md5 for " + filename);
                                for (IndexFiles index : indexes) {
                                    FileLocation fl = new FileLocation(filename.location.toString(), filename.object, null);
                                    if (index.getFilelocations().contains(fl)) {
                                        boolean removed = index.removeFilelocation(fl);
                                        //log.info("fls3 size " + removed + index.getFilelocations().size());
                    IndexFilesDao.add(index);
                    ifs.add(index);
                                    }
                                }
                        }
                }
                //DbRunner.doupdate = true;
                //IndexFilesDao.commit();
                while (IndexFilesDao.dirty() > 0) {
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
                    lock.unlock();
                }
            }
        }
        
        } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
        }

        List<List> retlistlist = new ArrayList<>();
        retlistlist.add(delList);
        retlistlist.add(nonexistList);
        retlistlist.add(newList);
        return retlistlist;
    }

}
