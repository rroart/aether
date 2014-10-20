package roart.jpa;

import java.io.InputStream;
import java.util.List;

import roart.model.FileObject;

public class LocalFileSystemJpa extends FileSystemJpa {

	@Override
	public List<FileObject> listFiles(FileObject f) {
		// TODO Auto-generated method stub
	    return LocalFileSystem.listFiles(f);
	}

	@Override
	public boolean exists(FileObject f) {
		// TODO Auto-generated method stub
	    return LocalFileSystem.exists(f);
	}

	@Override
	    public String getAbsolutePath(FileObject f) {
		// TODO Auto-generated method stub
	    return LocalFileSystem.getAbsolutePath(f);
	}

	@Override
	public boolean isDirectory(FileObject f) {
		// TODO Auto-generated method stub
		return LocalFileSystem.isDirectory(f);
	}

	@Override
	public InputStream getInputStream(FileObject f) {
		// TODO Auto-generated method stub
		return LocalFileSystem.getInputStream(f);
	}

	@Override
	public FileObject getParent(FileObject f) {
		// TODO Auto-generated method stub
		return LocalFileSystem.getParent(f);
	}

	@Override
	public FileObject get(String string) {
		// TODO Auto-generated method stub
		return LocalFileSystem.get(string);
	}

}
