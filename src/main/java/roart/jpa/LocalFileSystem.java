package roart.jpa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import roart.dao.FileSystemDao;
import roart.model.FileObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalFileSystem {

    private static final Logger log = LoggerFactory.getLogger(LocalFileSystem.class);

	public static List<FileObject> listFiles(FileObject f) {
		List<FileObject> foList = new ArrayList<FileObject>();
		File dir = (File) f.object;
		File listDir[] = dir.listFiles();
		if (listDir != null) {
		for (File file : listDir) {
			FileObject fo = new FileObject(file);
			foList.add(fo);
		}
		}
		return foList;
	}

	public static boolean exists(FileObject f) {
		return ((File) f.object).exists();
	}

	public static String getAbsolutePath(FileObject f) {
		return FileSystemDao.FILE + ((File) f.object).getAbsolutePath();
	}

	public static boolean isDirectory(FileObject f) {
		return ((File) f.object).isDirectory();
	}

	public static InputStream getInputStream(FileObject f) {
		try {
		    return new FileInputStream( (File) f.object /*new File(getAbsolutePath(f))*/);
		} catch (FileNotFoundException e) {
			log.error(Constants.EXCEPTION, e);
			return null;
		}
	}

	public static FileObject get(String string) {
	    if (string.startsWith(FileSystemDao.FILE)) {
		string = string.substring(5);
	    }
		return new FileObject(new File(string));
	}

	public static FileObject getParent(FileObject f) {
		String parent = ((File) f.object).getParent();
		File file = new File(parent);
		return new FileObject(file);
	}
}
