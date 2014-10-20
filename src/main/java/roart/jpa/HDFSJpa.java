package roart.jpa;

import java.io.InputStream;
import java.util.List;

import roart.model.FileObject;

public class HDFSJpa extends FileSystemJpa {

	@Override
	public List<FileObject> listFiles(FileObject f) {
		// TODO Auto-generated method stub
	    return HDFS.listFiles(f);
	}

	@Override
	public boolean exists(FileObject f) {
		// TODO Auto-generated method stub
	    return HDFS.exists(f);
	}

	@Override
	    public String getAbsolutePath(FileObject f) {
		// TODO Auto-generated method stub
	    return HDFS.getAbsolutePath(f);
	}

	@Override
	public boolean isDirectory(FileObject f) {
		// TODO Auto-generated method stub
		return HDFS.isDirectory(f);
	}

	@Override
	public InputStream getInputStream(FileObject f) {
		// TODO Auto-generated method stub
		return HDFS.getInputStream(f);
	}

	@Override
	public FileObject getParent(FileObject f) {
		// TODO Auto-generated method stub
		return HDFS.getParent(f);
	}

	@Override
	public FileObject get(String string) {
		// TODO Auto-generated method stub
		return HDFS.get(string);
	}

}
