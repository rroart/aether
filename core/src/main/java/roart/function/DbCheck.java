package roart.function;

import java.util.List;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.FileLocation;
import roart.common.model.Files;
import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;
import roart.database.IndexFilesDS;
import roart.database.IndexFilesDSFactory;
import roart.database.IndexFilesDao;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import roart.common.model.ResultItem;
import roart.service.ControlService;

public class DbCheck extends AbstractFunction {

    public DbCheck(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        super(param, nodeConf, controlService);
    }

    @Override
    public List doClient(ServiceParam param) {
        IndexFilesDao indexFilesDao = new IndexFilesDao(nodeConf, controlService);
        try {
            List<IndexFiles> indexes;
            List<Files> files;
            String db = param.name;
            if (db == null) {
                indexes = indexFilesDao.getAll();
                files = indexFilesDao.getAllFiles();
            } else {
                IndexFilesDS access = IndexFilesDSFactory.get(db, nodeConf, controlService);
                indexes = access.getAll();
                files = access.getAllFiles();
            }
        List<Files> checklist = new ArrayList<>();
        for (IndexFiles indexFiles : indexes) {
            for (FileLocation filename : indexFiles.getFilelocations()) {
                checklist.add(new Files(filename.toString(), indexFiles.getMd5()));
            }
        }
        Set<Files> set1 = new HashSet<>(files);
        Set<Files> set2 = new HashSet<>(files);
        Set<Files> set3 = new HashSet<>(checklist);
        Set<Files> set4 = new HashSet<>(checklist);
        set1.removeAll(set3);
        set4.removeAll(set2);
        List<ResultItem> resultList1 = new ArrayList<>();
        List<ResultItem> resultList2 = new ArrayList<>();
        resultList1.add(new ResultItem("Present in files"));
        resultList1.add(new ResultItem("" + set1));
        resultList2.add(new ResultItem("Present in indexfiles"));
        resultList2.add(new ResultItem("" + set4));
        log.info("Present in file {}", set1);
        log.info("Present in indexfile {}", set4);
        List<List> retlistlist = new ArrayList<>();
        retlistlist.add(resultList1);
        retlistlist.add(resultList2);
        return retlistlist;
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            return null;
        }
    }

}
