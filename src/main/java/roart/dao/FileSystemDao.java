package roart.dao;

import java.io.InputStream;
import java.util.List;

import org.apache.hadoop.fs.Path;

import roart.jpa.FileSystemJpa;
import roart.jpa.HDFSJpa;
import roart.jpa.LocalFileSystemJpa;
import roart.model.FileObject;

public class FileSystemDao {

	public static String FILE = "file:";
	public static String HDFS = "hdfs:";
	public static String FILESLASH = "file://";
	public static String HDFSSLASH = "hdfs://";
    private static FileSystemJpa filesystemJpa = null;

    public static void instance(String type) {
        if (false && filesystemJpa == null) {
            if (type.equals("local")) {
                filesystemJpa = new LocalFileSystemJpa();
            }
            if (type.equals("hadoop")) {
                filesystemJpa = new HDFSJpa();
            }
        }
    }

    public static List<FileObject> listFiles(FileObject f) {
	return getFileSystemJpa(f).listFiles(f);
    }

	public static boolean exists(FileObject f) {
	return getFileSystemJpa(f).exists(f);
    }

    public static boolean isDirectory(FileObject f) {
    	return getFileSystemJpa(f).isDirectory(f);
        }

    public static String getAbsolutePath(FileObject f) {
    	return getFileSystemJpa(f).getAbsolutePath(f);
    }
    public static InputStream getInputStream(FileObject f) {
    	return getFileSystemJpa(f).getInputStream(f);
    }

	public static FileObject get(String string) {
		return getFileSystemJpa(string).get(string);
	}

	public static FileObject getParent(FileObject f) {
		return getFileSystemJpa(f).getParent(f);
	}
	
    private static FileSystemJpa getFileSystemJpa(FileObject f) {
    	if (f.object.getClass().isAssignableFrom(Path.class)) {
    		return new HDFSJpa();
    	} else {
    		return new LocalFileSystemJpa();
    	}   	
	}

    private static FileSystemJpa getFileSystemJpa(String s) {
    	if (s.startsWith(HDFS)) {
    		return new HDFSJpa();
    	} else {
    		return new LocalFileSystemJpa();
    	}
	}

}
