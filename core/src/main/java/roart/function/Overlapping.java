package roart.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.ResultItem;
import roart.common.service.ServiceParam;
import roart.util.TraverseUtil;
import roart.service.ControlService;

public class Overlapping extends AbstractFunction {

    public Overlapping(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        super(param, nodeConf, controlService);
    }

    private static int dirsizelimit = 100;
    
    @Override
    public List doClient(ServiceParam param) {
        List<ResultItem> retList = new ArrayList<>();
        List<ResultItem> retList2 = new ArrayList<>();
        ResultItem ri = new ResultItem();
        ri.add("Percent");
        ri.add("Count");
        ri.add("Directory 1");
        ri.add("Directory 2");
        retList.add(ri);
        ri = new ResultItem();
        ri.add("Percent");
        ri.add("Count");
        ri.add("Count 2");
        ri.add("Directory");
        retList2.add(ri);
    
        Set<String> filesetnew = new HashSet<String>();
        Map<Integer, List<String[]>> sortlist = new TreeMap<Integer, List<String[]>>();
        Map<Integer, List<String[]>> sortlist2 = new TreeMap<Integer, List<String[]>>();
        Map<String, HashSet<String>> dirset = new HashMap<String, HashSet<String>>();
        Map<String, HashSet<String>> fileset = new HashMap<String, HashSet<String>>();
    
        // filesetnew/2 will be empty before and after
        // dirset will contain a map of directories, and the md5 files is contains
        // fileset will contain a map of md5 and the directories it has files in
        try {
            Set<String> filesetnew2 = TraverseUtil.doList2(dirset, fileset, nodeConf, controlService);
            filesetnew.addAll(filesetnew2);
        } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
        }
    
        log.info("dirs " + dirset.size());
        log.info("files " + fileset.size());
    
        // start at i+1 to avoid comparing twice
    
        List<String> keyList = new ArrayList<String>(dirset.keySet());
        for (int i = 0; i < keyList.size(); i++ ) {
            if (dirset.get(keyList.get(i)).size() < dirsizelimit) {
                continue;
            }
            for (int j = i+1; j < keyList.size(); j++ ) {
                // set1,3 with md5 files contained in dir number i
                // set2,4 with md5 files contained in dir number j
                HashSet<String> set1 = (HashSet<String>) dirset.get(keyList.get(i)).clone();
                HashSet<String> set2 = (HashSet<String>) dirset.get(keyList.get(j)).clone();
                int size0 = set1.size();
                if (set2.size() > size0) {
                    size0 = set2.size();
                }
                set1.retainAll(set2);
                // sum
                int size = set1.size();
                if (size0 == 0) {
                    size0 = 1000000;
                    log.error("size0");
                }
                // add result
                int ratio = (int) (100*size/size0);
                if (ratio > 50 && size > 4) {
                    Integer intI = new Integer(ratio);
                    String sizestr = "" + size;
                    sizestr = "      ".substring(sizestr.length()) + sizestr;
                    String[] str = new String[]{ sizestr, keyList.get(i), keyList.get(j)}; // + " " + set1;
                    List<String[]> strSet = sortlist.get(intI);
                    if (strSet == null) {
                        strSet = new ArrayList<String[]>();
                    }
                    strSet.add(str);
                    sortlist.put(intI, strSet);
                }
            }
        }
        // get biggest overlap
        for (Integer intI : sortlist.keySet()) {
            for (String[] strs : sortlist.get(intI)) {
                ResultItem ri2 = new ResultItem();
                ri2.add("" + intI.intValue());
                for (String str : strs) {
                    ri2.add(str);
                }
                retList.add(ri2);
            }
        }
        for (int i = 0; i < keyList.size(); i++ ) {
            int fileexist = 0;
            String dirname = keyList.get(i);
            Set<String> dirs = dirset.get(dirname);
            int dirsize = dirs.size();
            for (String md5 : dirs) {
                Set<String> files = fileset.get(md5);
                if (files != null && files.size() >= 2) {
                    fileexist++;
                }
            }
            int ratio = (int) (100*fileexist/dirsize);
            // overlapping?
            if (ratio > 50 && dirsize > dirsizelimit) {
                Integer intI = new Integer(ratio);
                String sizestr = "" + dirsize;
                sizestr = "      ".substring(sizestr.length()) + sizestr;
                String[] str = new String[]{sizestr, "" + fileexist, dirname};
                List<String[]> strSet = sortlist2.get(intI);
                if (strSet == null) {
                    strSet = new ArrayList<String[]>();
                }
                strSet.add(str);
                sortlist2.put(intI, strSet);
            }
        }
        for (Integer intI : sortlist2.keySet()) {
            for (String[] strs : sortlist2.get(intI)) {
                    ResultItem ri2 = new ResultItem();
                    ri2.add("" + intI.intValue());
                for (String str : strs) {
                    ri2.add(str);
                }
                retList2.add(ri2);
            }
        }
        List<List> retlistlist = new ArrayList<>();
        retlistlist.add(retList);
        retlistlist.add(retList2);
        return retlistlist;
    }

}
