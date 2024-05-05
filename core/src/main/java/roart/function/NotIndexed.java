package roart.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.service.ServiceParam;
import roart.database.IndexFilesDao;
import roart.util.TraverseUtil;
import roart.service.ControlService;

public class NotIndexed extends AbstractFunction {

    public NotIndexed(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        super(param, nodeConf, controlService);
    }

    @Override
    public List doClient(ServiceParam param) {
        IndexFilesDao indexFilesDao = new IndexFilesDao(nodeConf, controlService);
        List<List> retlistlist = new ArrayList<>();
        List<ResultItem> retlist = new ArrayList<>();
        List<ResultItem> retlist2 = new ArrayList<>();
        List<ResultItem> retlist3 = new ArrayList<>();
        ResultItem ri3 = new ResultItem();
        ri3.add("Suffix");
        ri3.add("Success");
        ri3.add("Fail");
        retlist2.add(ri3);
        ResultItem ri4 = new ResultItem();
        ri4.add("Mimetype");
        ri4.add("Success");
        ri4.add("Fail");
        retlist3.add(ri4);
        List<ResultItem> retlistyes = null;
        try {
            retlist.addAll(TraverseUtil.notindexed(param, indexFilesDao, controlService));
            retlistyes = TraverseUtil.indexed(param, indexFilesDao, controlService);
            Map<String, Integer> plusretlist = new HashMap<>();
            Map<String, Integer> plusretlistyes = new HashMap<>();
            Map<String, Integer> plusretlistmime = new HashMap<>();
            Map<String, Integer> plusretlistmimeyes = new HashMap<>();
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
                plusretlist.merge(suffix, 1, Integer::sum);
                String mimetype = (String) ri.get().get(IndexFiles.MIMETYPECOLUMN);
                if (mimetype != null && !mimetype.isEmpty()) {
                    plusretlistmime.merge(mimetype, 1, Integer::sum);
                }
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
                plusretlistyes.merge(suffix, 1, Integer::sum);
                String mimetype = (String) ri.get().get(IndexFiles.MIMETYPECOLUMN);
                if (mimetype != null && !mimetype.isEmpty()) {
                    plusretlistmimeyes.merge(mimetype, 1, Integer::sum);
                }
            }
            log.info("size " + plusretlist.size());
            log.info("sizeyes " + plusretlistyes.size());
            Set<String> keys = new TreeSet<>();
            keys.addAll(plusretlist.keySet());
            keys.addAll(plusretlistyes.keySet());
            Set<String> mimekeys = new TreeSet<>();
            mimekeys.addAll(plusretlistmime.keySet());
            mimekeys.addAll(plusretlistmimeyes.keySet());
            for(String string : keys) {
                ResultItem ri2 = new ResultItem();
                ri2.add(string);
                ri2.add(plusretlistyes.get(string) != null ? "" + plusretlistyes.get(string) : "");
                ri2.add(plusretlist.get(string) != null ? "" + plusretlist.get(string) : "");
                retlist2.add(ri2);
            }
            for(String string : mimekeys) {
                ResultItem ri2 = new ResultItem();
                ri2.add(string);
                ri2.add(plusretlistmimeyes.get(string) != null ? "" + plusretlistmimeyes.get(string) : "");
                ri2.add(plusretlistmime.get(string) != null ? "" + plusretlistmime.get(string) : "");
                retlist3.add(ri2);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        log.info("sizes " + retlist.size() + " " + retlist2.size() + " " + System.currentTimeMillis());
        retlistlist.add(retlist);
        retlistlist.add(retlist3);
        retlistlist.add(retlist2);
        return retlistlist;
    }

}
