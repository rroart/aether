package roart.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.FileLocation;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.service.ServiceParam;
import roart.content.ClientHandler;
import roart.database.IndexFilesDao;
import roart.service.ControlService;

public class DbIndex extends AbstractFunction {

    public DbIndex(ServiceParam param, NodeConfig nodeConf) {
        super(param, nodeConf);
    }

    @Override
    public List doClient(ServiceParam param) {
        IndexFilesDao indexFilesDao = new IndexFilesDao(nodeConf);
        try {
            ServiceParam.Function function = param.function;
            String md5 = param.file;
            log.info("function " + function + " " + md5);

            List<List> retlistlist = new ArrayList<>();
            List<ResultItem> indexList = new ArrayList<>();
            indexList.add(IndexFiles.getHeader());
            List<ResultItem> indexfilesList = new ArrayList<>();
            indexfilesList.add(new ResultItem("Files"));
            List<ResultItem> filesList = new ArrayList<>();
            filesList.add(new ResultItem("Files"));

            IndexFiles index = indexFilesDao.getByMd5(md5);
            if (index != null) {
                FileLocation aFl = index.getaFilelocation();
                indexList.add(IndexFiles.getResultItem(index, index.getLanguage(), ControlService.nodename, aFl));
                Set<FileLocation> files = index.getFilelocations();
                if (files != null) {
                    for (FileLocation filename : files) {
                        indexfilesList.add(new ResultItem(filename.toString()));
                    }
                }
                // TODO batch or not needed?
                Set<FileLocation> flSet = indexFilesDao.getFilelocationsByMd5(md5);
                if (flSet != null) {
                    for (FileLocation fl : flSet) {
                        if (fl == null) {
                            filesList.add(new ResultItem(""));
                        } else {
                            filesList.add(new ResultItem(fl.toString()));
                        }
                    }
                }

            }

            retlistlist.add(indexList);
            retlistlist.add(indexfilesList);
            retlistlist.add(filesList);
            return retlistlist;
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return null;
    }
}
