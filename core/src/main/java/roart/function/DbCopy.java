package roart.function;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import roart.common.model.Files;
import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;
import roart.database.IndexFilesAccess;
import roart.database.IndexFilesAccessFactory;
import roart.database.IndexFilesDao;

public class DbCopy extends AbstractFunction {

    public DbCopy(ServiceParam param) {
        super(param);
    }

    @Override
    public List doClient(ServiceParam param) {
        //List<IndexFiles> list = new IndexFilesDao().getAll();
        //List<Files> list2 = new IndexFilesDao().getAllFiles();
        IndexFilesDao out = new IndexFilesDao();
        String src = param.name;
        String dst = param.add;
        IndexFilesAccess srcAccess = IndexFilesAccessFactory.get(src);
        IndexFilesAccess dstAccess = IndexFilesAccessFactory.get(dst);
        try {
            List<IndexFiles> indexFiles = srcAccess.getAll();
            Set<IndexFiles> saves = new HashSet<>(indexFiles);
            dstAccess.save(saves);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
