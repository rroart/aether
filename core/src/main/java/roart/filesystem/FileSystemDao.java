package roart.filesystem;

import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.model.FileObject;
import roart.util.FileSystemConstants;

public class FileSystemDao {

    private static Logger log = LoggerFactory.getLogger(FileSystemDao.class);

	private static FileSystemAccess filesystemJpa = null;

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

}
