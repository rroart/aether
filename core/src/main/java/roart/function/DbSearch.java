package roart.function;

import java.util.ArrayList;
import java.util.List;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.FileLocation;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.service.ServiceParam;
import roart.database.IndexFilesDao;
import roart.service.ControlService;

public class DbSearch extends AbstractFunction {

    public DbSearch(ServiceParam param, NodeConfig nodeConf) {
        super(param, nodeConf);
    }

    @Override
    public List doClient(ServiceParam param) {
        IndexFilesDao indexFilesDao = new IndexFilesDao(nodeConf);
        try {
            ServiceParam.Function function = param.function;
            String searchexpr = param.file;
            int i = searchexpr.indexOf(":");
            log.info("function " + function + " " + searchexpr);

            List<List> retlistlist = new ArrayList<>();
            if (i < 0) {
                return retlistlist;
            }
            String field = searchexpr.substring(0, i);
            String text = searchexpr.substring(i + 1);
            List<ResultItem> indexList = new ArrayList<>();
            indexList.add(IndexFiles.getHeader());

            List<IndexFiles> indexes = indexFilesDao.getAll();
            for (IndexFiles index : indexes) {
                boolean match = false;

                if (field.equals("indexed")) {
                    Boolean indexedB = index.getIndexed();
                    boolean ind = indexedB != null && indexedB.booleanValue();
                    if (ind && text.equals("true")) {
                        match = true;
                    }
                    if (!ind && text.equals("false")) {
                        match = true;
                    }
                }
                if (field.equals("convertsw")) {
                    String convertsw = index.getConvertsw();
                    if (convertsw != null) {
                        match = convertsw.contains(text);
                    }
                }
                if (field.equals("classification")) {
                    String classification = index.getClassification();
                    if (classification != null) {
                        match = classification.contains(text);
                    }
                }
                if (field.equals("failedreason")) {
                    String failedreason = index.getFailedreason();
                    if (failedreason != null) {
                        match = failedreason.contains(text);
                    }
                }
                if (field.equals("noindexreason")) {
                    String noindexreason = index.getNoindexreason();
                    if (noindexreason != null) {
                        match = noindexreason.contains(text);
                    }
                }
                if (field.equals("timeoutreason")) {
                    String timeoutreason = index.getTimeoutreason();
                    if (timeoutreason != null) {
                        match = timeoutreason.contains(text);
                    }
                }
                if (field.equals("language")) {
                    String language = index.getLanguage();
                    if (language != null) {
                        match = language.equals(text);
                    }
                }
                if (match) {
                    FileLocation aFl = index.getaFilelocation();
                    indexList.add(IndexFiles.getResultItem(index, index.getLanguage(), ControlService.nodename, aFl));
                }
            }

            retlistlist.add(indexList);
            return retlistlist;
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return null;
    }

}
