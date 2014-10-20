package roart.dao;

import java.io.InputStream;
import java.util.List;

import roart.jpa.FileSystemJpa;
import roart.jpa.HDFSJpa;
import roart.jpa.LocalFileSystemJpa;
import roart.model.FileObject;

public class FileSystemDao {

    private static FileSystemJpa filesystemJpa = null;

    public static void instance(String type) {
        if (filesystemJpa == null) {
            if (type.equals("local")) {
                filesystemJpa = new LocalFileSystemJpa();
            }
            if (type.equals("hadoop")) {
                filesystemJpa = new HDFSJpa();
            }
        }
    }

    public static List<FileObject> listFiles(FileObject f) {
	return filesystemJpa.listFiles(f);
    }

    public static boolean exists(FileObject f) {
	return filesystemJpa.exists(f);
    }

    public static boolean isDirectory(FileObject f) {
    	return filesystemJpa.isDirectory(f);
        }

    public static String getAbsolutePath(FileObject f) {
    	return filesystemJpa.getAbsolutePath(f);
    }
    public static InputStream getInputStream(FileObject f) {
    	return filesystemJpa.getInputStream(f);
    }

	public static FileObject get(String string) {
		// TODO Auto-generated method stub
		return filesystemJpa.get(string);
	}

	public static FileObject getParent(FileObject f) {
		// TODO Auto-generated method stub
		return filesystemJpa.getParent(f);
	}
}
