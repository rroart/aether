package roart.filesystem;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.constants.Constants;
import roart.common.constants.FileSystemConstants;
import roart.common.filesystem.MyFile;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.FileObject;
import roart.common.model.Location;
import roart.common.util.FsUtil;
import roart.service.ControlService;
import java.util.Map.Entry;

public class FileSystemDao {

    private static Logger log = LoggerFactory.getLogger(FileSystemDao.class);

    private static FileSystemAccess filesystemJpa = null;

    //private static Map<String, MyServer> myservers = new HashMap<>();

    public static void instance(String type) {
    }

    public static List<FileObject> listFiles(FileObject f) {
        return getFileSystemAccess(f).listFiles(f);
    }

    public static List<MyFile> listFilesFull(FileObject f) {
        return getFileSystemAccess(f).listFilesFull(f);
    }

    public static boolean exists(FileObject f) {
        return getFileSystemAccess(f).exists(f);
    }

    public static boolean isDirectory(FileObject f) {
        return getFileSystemAccess(f).isDirectory(f);
    }

    public static String getAbsolutePath(FileObject f) {
        return getFileSystemAccess(f).getAbsolutePath(f);
    }
    
    public static InputStream getInputStream(FileObject f) {
        return getFileSystemAccess(f).getInputStream(f);
    }

    public static Map<FileObject, MyFile> getWithInputStream(Set<FileObject> filenames) {
        FileObject f = filenames.iterator().next();
        Map<String, MyFile> map = getFileSystemAccess(f).getWithInputStream(filenames);
        Map<FileObject, MyFile> retMap = new HashMap<>();
        for (Entry<String, MyFile> entry : map.entrySet()) {
            retMap.put(new FileObject(filenames.iterator().next().location, entry.getKey()), entry.getValue());
        }
        return retMap;
    }

    public static Map<FileObject, MyFile> getWithoutInputStream(Set<FileObject> filenames) {
        FileObject f = filenames.iterator().next();
        Map<String, MyFile> map = getFileSystemAccess(f).getWithoutInputStream(filenames);
        Map<FileObject, MyFile> retMap = new HashMap<>();
        for (Entry<String, MyFile> entry : map.entrySet()) {
            retMap.put(new FileObject(filenames.iterator().next().location, entry.getKey()), entry.getValue());
        }
        return retMap;
    }

    public static FileObject get(FileObject fo) {
        return getFileSystemAccess(fo).get(fo);
    }

    public static FileObject getParent(FileObject f) {
        return getFileSystemAccess(f).getParent(f);
    }

    public static InmemoryMessage readFile(FileObject f) {
        return getFileSystemAccess(f).readFile(f);
    }

    // TODO make this OO
    private static FileSystemAccess getFileSystemAccess(FileObject f) {
        if (f == null) {
            log.error("f null");
            return new LocalFileSystemAccess();
        }
        /*
        if (f.fs == null) {
            log.error("f.fs null " + f.object);
            return new LocalFileSystemAccess();
        }
    	if (f.fs.equals("HDFS")) {
    		return new HDFSAccess();
    	} else if (f.fs.equals("Swift")) {
     		return new SwiftAccess();
    	} else {
    		return new LocalFileSystemAccess();
    	}
         */
        return getFileSystemAccess(f.location, f.object);
    }
    
    private static FileSystemAccess getFileSystemAccess(Location fs, String path) {
        Location fs2 = new Location(fs.nodename, fs.fs, fs.extra);
        if (fs2.fs == null || fs2.fs.isEmpty()) {
            fs2.fs = FileSystemConstants.LOCALTYPE;
        }
        String url = getUrl(ControlService.curatorClient, fs2, path, "");
        if (url == null) {
            log.error("URL null for {} {}", fs, path);
        }
        FileSystemAccess access = new FileSystemAccess();
        access.constructor("http://" + url + "/");
        return access;
    }

    static String getUrl(CuratorFramework curatorClient, Location fs, String path, String s) {
        // //fstype/path
        // node and openshift?
        // zk nodename type path
        //ControlServer.z
        String url = null;
        try {
            String str = "/" + Constants.AETHER + "/" + Constants.FS + stringOrNull(fs.nodename) + "/" + fs.fs + stringOrNull(fs.extra) + s;
            String zPath = "/" + Constants.AETHER + "/" + Constants.FS + stringOrNull(fs.nodename) + "/" + fs.fs + stringOrNull(fs.extra) + s;
            log.debug("Path {}", zPath);
            Stat b = curatorClient.checkExists().forPath(zPath);
            if (b == null) {
                return null;
            }
            List<String> children = curatorClient.getChildren().forPath(zPath);
            log.debug("Children {}", children.size());
            if (children.isEmpty()) {
                Stat stat = curatorClient.checkExists().forPath(zPath);
                log.debug("Time {} {}", System.currentTimeMillis(), stat.getMtime());;
                long time = System.currentTimeMillis() - stat.getMtime();
                log.debug("Time {}", time);
                if (time < 20000) {
                    return new String(curatorClient.getData().forPath(zPath));
                } else {
                    System.out.println("timeout");
                    log.error("Timeout");
                    return null;
                }
            }
            for (String child : children) {
                log.debug("Child {}", child);
                String newPath = s + "/" + child;
                log.debug("Compare {} {}", path, newPath);
                if (path.startsWith(newPath)) {
                    return getUrl(curatorClient, fs, path, newPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(Constants.EXCEPTION, e);
        }
        return url;
    }

    private static String stringOrNull(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        } else {
            return "/" + string;
        }
    }
}
