package roart.filesystem;

import java.io.InputStream;
import java.util.HashMap;
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
import roart.common.model.FileObject;
import roart.common.util.FsUtil;
import roart.service.ControlService;

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

    public static Map<String, MyFile> getWithInputStream(Set<String> filenames) {
        String f = filenames.iterator().next();
        return getFileSystemAccess(f).getWithInputStream(filenames);
    }

    public static FileObject get(String string) {
        return getFileSystemAccess(string).get(string);
    }

    public static FileObject getParent(FileObject f) {
        return getFileSystemAccess(f).getParent(f);
    }

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
        return getFileSystemAccess(f.fs, (String) f.object);
    }
    
    private static FileSystemAccess getFileSystemAccess(String fs, String path) {
        String url = getUrl(ControlService.curatorClient, fs, path, "");
        if (url == null) {
            log.error("URL null for {} {}", fs, path);
        }
        FileSystemAccess access = new FileSystemAccess();
        access.constructor("http://" + url + "/");
        return access;
    }

    static String getUrl(CuratorFramework curatorClient, String fs, String path, String s) {
        // //fstype/path
        // node and openshift?
        // zk nodename type path
        //ControlServer.z
        String url = null;
        try {
            String zPath = "/fs/" + fs + s;
            log.info("here" + zPath);
            List<String> children = curatorClient.getChildren().forPath(zPath);
            log.info("ch " + children.size());
            if (children.isEmpty()) {
                Stat stat = curatorClient.checkExists().forPath(zPath);
                log.info("m " + System.currentTimeMillis() + " " + stat.getMtime());;
                long time = System.currentTimeMillis() - stat.getMtime();
                log.info("time " + time);
                if (time < 10000) {
                    return new String(curatorClient.getData().forPath(zPath));
                } else {
                    System.out.println("timeout");
                    log.info("timeout");
                    return null;
                }
            }
            for (String child : children) {
                log.info("child " + child);
                String newPath = s + "/" + child;
                log.info("cmp " + path + " " + newPath);
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

    private static FileSystemAccess getFileSystemAccess(String s) {
        String fs = "LocalFileSystem";
        String path = FsUtil.getFsPath(s);
        if (s.startsWith(FileSystemConstants.HDFS)) {
            fs = "HDFS";
        }
        if (s.startsWith(FileSystemConstants.SWIFT)){
            fs = "Swift";
        }
        log.info("getgrr " + s + " :: " + fs + " " + path);
        return getFileSystemAccess(fs, path);
    }
}
