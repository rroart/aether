package roart.filesystem;

import java.io.InputStream;
import java.util.List;

import roart.model.FileObject;

public class HDFSAccess extends FileSystemAccess {

	@Override
	public List<FileObject> listFiles(FileObject f) {
	    return HDFS.listFiles(f);
	}

	@Override
	public boolean exists(FileObject f) {
	    return HDFS.exists(f);
	}

	@Override
	    public String getAbsolutePath(FileObject f) {
	    return HDFS.getAbsolutePath(f);
	}

	@Override
	public boolean isDirectory(FileObject f) {
		return HDFS.isDirectory(f);
	}

	@Override
	public InputStream getInputStream(FileObject f) {
		return HDFS.getInputStream(f);
	}

	@Override
	public FileObject getParent(FileObject f) {
		return HDFS.getParent(f);
	}

	@Override
	public FileObject get(String string) {
		return HDFS.get(string);
	}

}
