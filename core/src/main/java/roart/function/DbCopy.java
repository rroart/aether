package roart.function;

import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.Files;
import roart.common.model.IndexFiles;
import roart.common.service.ServiceParam;
import roart.database.IndexFilesAccess;
import roart.database.IndexFilesAccessFactory;
import roart.database.IndexFilesDao;

public class DbCopy extends AbstractFunction {

    public DbCopy(ServiceParam param, NodeConfig nodeConf) {
        super(param, nodeConf);
    }

    @Override
    public List doClient(ServiceParam param) {
        List<Integer> retList = new ArrayList<>();
        //List<IndexFiles> list = new IndexFilesDao().getAll();
        //List<Files> list2 = new IndexFilesDao().getAllFiles();
        IndexFilesDao out = new IndexFilesDao(nodeConf);
        String src = param.name;
        String dst = param.add;
        IndexFilesAccess srcAccess = IndexFilesAccessFactory.get(src, nodeConf);
        IndexFilesAccess dstAccess = IndexFilesAccessFactory.get(dst, nodeConf);
        try {
            long time0;
            dstAccess.clear();
            time0 = System.currentTimeMillis();
            List<IndexFiles> indexFiles = srcAccess.getAll();
            retList.add((int) ((System.currentTimeMillis() - time0) / 1000));
            Set<IndexFiles> saves = new HashSet<>(indexFiles);
            time0 = System.currentTimeMillis();
            dstAccess.save(saves);
            retList.add((int) ((System.currentTimeMillis() - time0) / 1000));
            time0 = System.currentTimeMillis();
            List<IndexFiles> indexFiles2 = dstAccess.getAll();
            retList.add((int) ((System.currentTimeMillis() - time0) / 1000));
            Map<String, IndexFiles> map = util(indexFiles);
            Map<String, IndexFiles> map2 = util(indexFiles2);
            if (map.size() != map2.size()) {
                log.error("Different sizes {} {}", map.size(), map2.size());
            }
            for (Entry<String, IndexFiles> entry : map.entrySet()) {
                String md5 = entry.getKey();
                IndexFiles i = entry.getValue();
                IndexFiles i2 = map2.get(md5);
                if (!i.equals(i2)) {
                    log.error("Different for {}", md5);
                }
            }
       } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            e.printStackTrace();
        }
        return retList;
    }

    public Map<String, IndexFiles> util(List<IndexFiles> indexFiles) {
        Map<String, IndexFiles> map = new HashMap<>();
        for (IndexFiles i : indexFiles) {
            map.put(i.getMd5(), i);
        }
        return map;
    }
}
