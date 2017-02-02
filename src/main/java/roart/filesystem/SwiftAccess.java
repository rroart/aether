package roart.filesystem;

import java.io.InputStream;
import java.util.List;

import roart.model.FileObject;

public class SwiftAccess extends FileSystemAccess {

	@Override
	public List<FileObject> listFiles(FileObject f) {
	    return Swift.listFiles(f);
	}

	@Override
	public boolean exists(FileObject f) {
	    return Swift.exists(f);
	}

	@Override
	    public String getAbsolutePath(FileObject f) {
	    return Swift.getAbsolutePath(f);
	}

	@Override
	public boolean isDirectory(FileObject f) {
		return Swift.isDirectory(f);
	}

	@Override
	public InputStream getInputStream(FileObject f) {
		return Swift.getInputStream(f);
	}

	@Override
	public FileObject getParent(FileObject f) {
		return Swift.getParent(f);
	}

	@Override
	public FileObject get(String string) {
		return Swift.get(string);
	}

}
