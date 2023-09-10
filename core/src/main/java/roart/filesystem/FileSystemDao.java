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

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.FileSystemConstants;
import roart.common.constants.QueueConstants;
import roart.common.filesystem.MyFile;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.FileObject;
import roart.common.model.Location;
import roart.common.queue.QueueElement;
import roart.common.util.FsUtil;
import roart.service.ControlService;
import roart.util.TraverseUtil;

import java.util.Map.Entry;

public class FileSystemDao {

    private static Logger log = LoggerFactory.getLogger(FileSystemDao.class);

    private FileSystemAccess filesystemJpa = null;

    private NodeConfig nodeConf;

    private ControlService controlService;
    
    public FileSystemDao(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
    }

    //private static Map<String, MyServer> myservers = new HashMap<>();

    public void instance(String type) {
    }

    public List<FileObject> listFiles(FileObject f) {
        return getFileSystemAccess(f).listFiles(f);
    }

    public List<MyFile> listFilesFull(FileObject f) {
        return getFileSystemAccess(f).listFilesFull(f);
    }

    public boolean exists(FileObject f) {
        return getFileSystemAccess(f).exists(f);
    }

    public boolean isDirectory(FileObject f) {
        return getFileSystemAccess(f).isDirectory(f);
    }

    public String getAbsolutePath(FileObject f) {
        return getFileSystemAccess(f).getAbsolutePath(f);
    }
    
    public InputStream getInputStream(FileObject f) {
        return getFileSystemAccess(f).getInputStream(f);
    }

    public Map<FileObject, MyFile> getWithInputStream(Set<FileObject> filenames) {
        FileObject f = filenames.iterator().next();
        Map<String, MyFile> map = getFileSystemAccess(f).getWithInputStream(filenames);
        Map<FileObject, MyFile> retMap = new HashMap<>();
        for (Entry<String, MyFile> entry : map.entrySet()) {
            retMap.put(new FileObject(filenames.iterator().next().location, entry.getKey()), entry.getValue());
        }
        return retMap;
    }

    public Map<FileObject, MyFile> getWithoutInputStream(Set<FileObject> filenames) {
        FileObject f = filenames.iterator().next();
        Map<String, MyFile> map = getFileSystemAccess(f).getWithoutInputStream(filenames);
        Map<FileObject, MyFile> retMap = new HashMap<>();
        for (Entry<String, MyFile> entry : map.entrySet()) {
            retMap.put(new FileObject(filenames.iterator().next().location, entry.getKey()), entry.getValue());
        }
        return retMap;
    }

    public FileObject get(FileObject fo) {
        return getFileSystemAccess(fo).get(fo);
    }

    public FileObject getParent(FileObject f) {
        return getFileSystemAccess(f).getParent(f);
    }

    public InmemoryMessage readFile(FileObject f) {
        Set<FileObject> filenames = new HashSet<>();
        filenames.add(f);
        Map<FileObject, InmemoryMessage> map = readFile(filenames);
        return map.get(f);
    }

    public Map<FileObject, InmemoryMessage> readFile(Set<FileObject> filenames) {
        FileObject f = filenames.iterator().next();
        Map<String, InmemoryMessage> map = getFileSystemAccess(f).readFile(filenames);
        Map<FileObject, InmemoryMessage> retMap = new HashMap<>();
        for (Entry<String, InmemoryMessage> entry : map.entrySet()) {
            retMap.put(new FileObject(filenames.iterator().next().location, entry.getKey()), entry.getValue());
        }
        return retMap;
    }

    public Map<FileObject, String> getMd5(Set<FileObject> filenames) {
        FileObject f = filenames.iterator().next();
        Map<String, String> map = getFileSystemAccess(f).getMd5(filenames);
        Map<FileObject, String> retMap = new HashMap<>();
        for (Entry<String, String> entry : map.entrySet()) {
            retMap.put(new FileObject(filenames.iterator().next().location, entry.getKey()), entry.getValue());
        }
        return retMap;
    }

    // TODO make this OO
    private FileSystemAccess getFileSystemAccess(FileObject f) {
        if (f == null) {
            log.error("f null");
            return new LocalFileSystemAccess(nodeConf, controlService);
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
    
    private FileSystemAccess getFileSystemAccess(Location fs, String path) {
        Location fs2 = new Location(fs.nodename, fs.fs, fs.extra);
        if (fs2.fs == null || fs2.fs.isEmpty()) {
            fs2.fs = FileSystemConstants.LOCALTYPE;
        }
        String url = getUrl(controlService.curatorClient, fs2, path, "");
        if (url == null) {
            log.error("URL null for {} {}", fs, path);
            return null;
        }
        FileSystemAccess access = new FileSystemAccess(nodeConf, controlService);
        access.constructor("http://" + url + "/");
        return access;
    }

    String getUrl(CuratorFramework curatorClient, Location fs, String path, String s) {
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
                if (path.equals(newPath) || path.startsWith(newPath + "/")) {
                    return getUrl(curatorClient, fs, path, newPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(Constants.EXCEPTION, e);
        }
        return url;
    }

    private String stringOrNull(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        } else {
            return "/" + string;
        }
    }
    private FileSystemAccess getFileSystemAccessQueue(FileObject f) {
        String[] dirlistarr = nodeConf.getDirList();
        FileObject[] dirlist = new FileObject[dirlistarr.length];
        int i = 0;
        for (String dir : dirlistarr) {
            dirlist[i++] = FsUtil.getFileObject(dir);
        }
        FileObject fo = TraverseUtil.indirlistmatch(f, dirlist);
        FileSystemAccess access = new FileSystemAccess(nodeConf, controlService);
        String queueName = QueueConstants.FS + "_" + fo.toString();
        access.setQueue(queueName);
        return access;
    }

    public void listFilesFullQueue(QueueElement element, FileObject fileObject) {
        getFileSystemAccessQueue(fileObject).listFilesFullQueue(element, fileObject);
    }

    public void getMd5Queue(QueueElement element, FileObject filename) {
        getFileSystemAccessQueue(filename).getMd5Queue(element, filename);
    }
    
    public void readFileQueue(QueueElement element, FileObject fileObject) {
        getFileSystemAccess(fileObject).readFileQueue(element, fileObject);
    }

}
