package roart.filesystem;

import java.io.InputStream;
import java.util.List;

import roart.model.FileObject;

public class LocalFileSystemAccess extends FileSystemAccess {

	@Override
	public List<FileObject> listFiles(FileObject f) {
	    return LocalFileSystem.listFiles(f);
	}

	@Override
	public boolean exists(FileObject f) {
	    return LocalFileSystem.exists(f);
	}

	@Override
	    public String getAbsolutePath(FileObject f) {
	    return LocalFileSystem.getAbsolutePath(f);
	}

	@Override
	public boolean isDirectory(FileObject f) {
		return LocalFileSystem.isDirectory(f);
	}

	@Override
	public InputStream getInputStream(FileObject f) {
		return LocalFileSystem.getInputStream(f);
	}

	@Override
	public FileObject getParent(FileObject f) {
		return LocalFileSystem.getParent(f);
	}

	@Override
	public FileObject get(String string) {
		return LocalFileSystem.get(string);
	}

}
