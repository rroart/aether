package roart.filesystem;

import java.io.InputStream;
import java.util.List;

import org.apache.hadoop.fs.Path;

import roart.model.FileObject;

public class FileSystemDao {

	public static final String FILE = "file:";
	public static final int FILELEN = 5;
	public static final String HDFS = "hdfs:";
	public static final int HDFSLEN = 5;
	public static final String FILESLASH = "file://";
	public static final int FILESLASHLEN = 7;
	public static final String HDFSSLASH = "hdfs://";
	public static final int HDFSSLASHLEN = 7;
	public static final String DOUBLESLASH = "//";
	public static final int DOUBLESLASHLEN = 2;
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
	
    private static FileSystemAccess getFileSystemAccess(FileObject f) {
    	if (f.object.getClass().isAssignableFrom(Path.class)) {
    		return new HDFSAccess();
    	} else {
    		return new LocalFileSystemAccess();
    	}   	
	}

    private static FileSystemAccess getFileSystemAccess(String s) {
    	if (s.startsWith(HDFS)) {
    		return new HDFSAccess();
    	} else {
    		return new LocalFileSystemAccess();
    	}
	}

}
