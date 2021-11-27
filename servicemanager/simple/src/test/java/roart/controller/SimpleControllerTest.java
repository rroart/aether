package roart.controller;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import roart.common.constants.FileSystemConstants;
import roart.common.model.FileObject;
import roart.common.model.Location;
import roart.common.util.FsUtil;
import roart.common.util.XmlFs;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class SimpleControllerTest {
    @Test
    public void test1() throws Exception {
        String l ="/home/roart/src/aethermicro/books,/usr/games,:swift:chess:/xiangqi,:swift:chess2:/shogi";
        Map<Location, List<FileObject>> filesystemMap = new XmlFs().getDirListMap(l);
        for (Entry<Location, List<FileObject>> entry : filesystemMap.entrySet()) {
            Location loc = entry.getKey();
            if (loc.fs == null) {
                loc.fs = FileSystemConstants.LOCALTYPE;
            }
            //if (!fileSystems.contains(loc.fs)) {
            //    continue;
            //}
            //  -DFS=hdfs -DPATH=/tmp -DZOO=localhost:2181.
            List<FileObject> fos = entry.getValue();
            List<String> paths = fos.stream().map(e -> e.toString()).collect(Collectors.toList());
            String path = StringUtils.join(paths, ',');
            /*
            int index = entry.indexOf(':');
            if (index >= 0) {
                path = path.substring(index + 1);
            }
            */
            System.out.println("0DFS=" + loc.fs);
            System.out.println("1DPATH=" + path);
            System.out.println("2DZOO=localhost:2181");
            System.out.println("3DNODE=" + (loc.nodename != null ? loc.nodename : ""));
            String[] paths2 = path.split(",");
            for (String aPath : paths2) {
            FileObject fo = FsUtil.getFileObject(aPath);
            System.out.println("ff"+fo.location.fs+"ff");
            if (fo.location.fs == null || fo.location.fs.isEmpty()) {
                fo.location.fs = FileSystemConstants.LOCALTYPE;
            }
            String str = "/fs" + stringOrNull(fo.location.nodename) + "/" + fo.location.fs + stringOrNull(fo.location.extra) + fo.object;
            System.out.println(str);
            }
        }
    } 
    
    private static String stringOrNull(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        } else {
            return "/" + string;
        }
    }
     
}