package roart.filesystem;

import java.io.InputStream;
import java.util.List;

import roart.model.FileObject;

public abstract class FileSystemAccess {

    public abstract List<FileObject> listFiles(FileObject f);

    public abstract boolean exists(FileObject f);

    public abstract String getAbsolutePath(FileObject f);

	public abstract boolean isDirectory(FileObject f);

	public abstract InputStream getInputStream(FileObject f);

	public abstract FileObject getParent(FileObject f);

	public abstract FileObject get(String string);
	
}
