package roart.filesystem;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.model.FileObject;
import roart.service.ControlService;
import roart.util.Constants;
import roart.util.FileSystemConstants;

public class FileSystemDao {

    private static Logger log = LoggerFactory.getLogger(FileSystemDao.class);

	private static FileSystemAccess filesystemJpa = null;

	//private static Map<String, MyServer> myservers = new HashMap<>();
	
    public static void instance(String type) {
    }

    public static List<FileObject> listFiles(FileObject f) {
	return getFileSystemAccess(f).listFiles(f);
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

	public static FileObject get(String string) {
		return getFileSystemAccess(string).get(string);
	}

	public static FileObject getParent(FileObject f) {
		return getFileSystemAccess(f).getParent(f);
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
        String url = getUrl(ControlService.curatorClient, f, "");
        FileSystemAccess access = new FileSystemAccess();
        access.constructor(url);
        return access;
	}

	static String getUrl(CuratorFramework curatorClient, FileObject f, String s) {
	        // //fstype/path
	        // node and openshift?
	        // zk nodename type path
	        //ControlServer.z
	        String url = null;
	        String fs = f.fs;
	        String path = (String) f.object;
	        try {
	            System.out.println("here");
	            String zPath = "/fs/" + fs + s;
	            List<String> children = curatorClient.getChildren().forPath(zPath);
	            System.out.println("ch " + children.size());
	            if (children.isEmpty()) {
	                Stat stat = curatorClient.checkExists().forPath(zPath);
                        System.out.println("m " + System.currentTimeMillis() + " " + stat.getMtime());;
	                long time = System.currentTimeMillis() - stat.getMtime();
	                System.out.println("time " + time);
	                if (time < 10000) {
	                return new String(curatorClient.getData().forPath(zPath));
	                } else {
	                    System.out.println("timeout");
	                    log.info("timeout");
	                    return null;
	                }
	            }
	            for (String child : children) {
	                System.out.println("child " + child);
	                String newPath = s + "/" + child;
	                System.out.println("cmp " + path + " " + newPath);
	                if (path.startsWith(newPath)) {
	                    return getUrl(curatorClient, f, newPath);
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            log.error(Constants.EXCEPTION, e);
	        }
                return url;
    }

    // TODO make this OO
   private static FileSystemAccess getFileSystemAccess(String s) {
    	if (s.startsWith(FileSystemConstants.HDFS)) {
    		return new HDFSAccess();
    	} else if (s.startsWith(FileSystemConstants.SWIFT)){
    		return new SwiftAccess();
    	} else {
    		return new LocalFileSystemAccess();
    	}
	}

   class MyServer {
       String host;
       String port;
       String path;
   }
}
