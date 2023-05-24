package roart.function;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import roart.common.config.MyConfig;
import roart.common.constants.Constants;
import roart.common.model.FileLocation;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.service.ServiceParam;
import roart.common.synchronization.MyLock;
import roart.common.zkutil.ZKMessageUtil;
import roart.database.IndexFilesDao;
import roart.search.SearchDao;
import roart.service.ControlService;
import roart.util.MyLockFactory;
import roart.common.model.FileObject;

public class DeletePath extends AbstractFunction {

    public DeletePath(ServiceParam param) {
        super(param);
    }

    @Override
    public List doClient(ServiceParam param) {
	ConsistentClean cc = new ConsistentClean(param);
        synchronized (ControlService.writelock) {
            try {
                MyLock lock = null;
                if (MyConfig.conf.getZookeeper() != null && !MyConfig.conf.wantZookeeperSmall()) {
                    lock = MyLockFactory.create();
                    lock.lock(Constants.GLOBALLOCK);
                }
                List<List> retlistlist = new ArrayList<>();
                List<ResultItem> delList = new ArrayList<>();
                delList.add(new ResultItem("Deleted"));
                Set<IndexFiles> ifs = new HashSet<>();
                String path = param.file;
                if (path.isEmpty()) {
                    log.info("skipping empty path");
                    retlistlist.add(delList);
                    return retlistlist;
                }
                //Set<String> indexes = IndexFilesDao.getAllMd5();
		List<IndexFiles> indexes;
		indexes = new IndexFilesDao().getAll();

                Set<FileObject> delfileset = new HashSet<>();
                cc.extracted(delList, delfileset, path, indexes, ifs, false, true);
		/*
                for (IndexFiles index : indexes) {
                    MyLock lock2 = MyLockFactory.create();
                    lock2.lock(index.getMd5());               
                    //IndexFiles index = IndexFilesDao.getByMd5(md5);
		    // common 2
		    // find out if to be deleted
                    Set<FileLocation> deletes = new HashSet<FileLocation>();
                    for (FileLocation fl : index.getFilelocations()) {
                        if (fl.toString().contains(path)) {
                            delList.add(new ResultItem(fl.toString()));
                            deletes.add(fl);
                            //delfileset.add(filename);
                        }
                    }
		    // common 3
		    // then delete from collection, and eventually remove from search
                    if (!deletes.isEmpty()) {
                        index.getFilelocations().removeAll(deletes);
                        if (index.getFilelocations().isEmpty()) {
                            IndexFilesDao.delete(index);
                            SearchDao.deleteme(index.getMd5());
                        }
                        IndexFilesDao.add(index);
                        ifs.add(index);
                    }
                    lock2.unlock();
                }
		*/
                while (IndexFilesDao.dirty() > 0) {
                    TimeUnit.SECONDS.sleep(60);
                }

                if (MyConfig.conf.getZookeeper() != null && !MyConfig.conf.wantZookeeperSmall()) {
                    ZKMessageUtil.dorefresh(ControlService.nodename);
                    lock.unlock();
                    //ClientRunner.notify("Sending refresh request");
                }
                retlistlist.add(delList);
                return retlistlist;
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
        return null;
    }

}
