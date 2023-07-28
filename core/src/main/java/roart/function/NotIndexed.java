package roart.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.service.ServiceParam;
import roart.database.IndexFilesDao;
import roart.util.TraverseUtil;

public class NotIndexed extends AbstractFunction {

    public NotIndexed(ServiceParam param, NodeConfig nodeConf) {
        super(param, nodeConf);
    }

    @Override
    public List doClient(ServiceParam param) {
        IndexFilesDao indexFilesDao = new IndexFilesDao(nodeConf);
        List<List> retlistlist = new ArrayList<>();
        List<ResultItem> retlist = new ArrayList<>();
        List<ResultItem> retlist2 = new ArrayList<>();
        ResultItem ri3 = new ResultItem();
        ri3.add("Column 1");
        ri3.add("Column 2");
        ri3.add("Column 3");
        retlist2.add(ri3);
        List<ResultItem> retlistyes = null;
        try {
            retlist.addAll(TraverseUtil.notindexed(param, indexFilesDao));
            retlistyes = TraverseUtil.indexed(param, indexFilesDao);
            Map<String, Integer> plusretlist = new HashMap<>();
            Map<String, Integer> plusretlistyes = new HashMap<>();
            for(ResultItem ri : retlist) {
                if (ri == retlist.get(0)) {
                    continue;
                }
                String filename = (String) ri.get().get(IndexFiles.FILENAMECOLUMN);
                if (filename == null) {
                    continue;
                }
                int ind = filename.lastIndexOf(".");
                if (ind == -1 || ind <= filename.length() - 6) {
                    continue;
                }
                String suffix = filename.substring(ind+1);
                Integer i = plusretlist.get(suffix);
                if (i == null) {
                    i = new Integer(0);
                }
                i++;
                plusretlist.put(suffix, i);
            }
            for(ResultItem ri : retlistyes) {
                //String filename = (String) ri.get().get(0); // or for a whole list?
                String filename = (String) ri.get().get(IndexFiles.FILENAMECOLUMN);
                if (filename == null) {
                    continue;
                }
                int ind = filename.lastIndexOf(".");
                if (ind == -1 || ind <= filename.length() - 6) {
                    continue;
                }
                String suffix = filename.substring(ind+1);
                Integer i = plusretlistyes.get(suffix);
                if (i == null) {
                    i = new Integer(0);
                }
                i++;
                plusretlistyes.put(suffix, i);
            }
            log.info("size " + plusretlist.size());
            log.info("sizeyes " + plusretlistyes.size());
            for(String string : plusretlist.keySet()) {
                ResultItem ri2 = new ResultItem();
                ri2.add("Format");
                ri2.add(string);
                ri2.add("" + plusretlist.get(string).intValue());
                retlist2.add(ri2);
            }
            for(String string : plusretlistyes.keySet()) {
                ResultItem ri2 = new ResultItem();
                ri2.add("Formatyes");
                ri2.add(string);
                ri2.add("" + plusretlistyes.get(string).intValue());
                retlist2.add(ri2);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        log.info("sizes " + retlist.size() + " " + retlist2.size() + " " + System.currentTimeMillis());
        retlistlist.add(retlist);
        retlistlist.add(retlist2);
        return retlistlist;
    }

}
