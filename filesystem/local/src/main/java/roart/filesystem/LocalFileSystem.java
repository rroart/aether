package roart.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import roart.config.NodeConfig;
import roart.model.FileObject;
import roart.util.Constants;
import roart.util.FileSystemConstants;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalFileSystem extends FileSystemOperations {

    private static final Logger log = LoggerFactory.getLogger(LocalFileSystem.class);

    public LocalFileSystem(String nodename, NodeConfig nodeConf) {
    }
    
    @Override
	public FileSystemFileObjectResult listFiles(FileSystemFileObjectParam param) {
	    FileObject f = param.fo;
		List<FileObject> foList = new ArrayList<FileObject>();
		File dir = objectToFile(f);
		File listDir[] = dir.listFiles();
		if (listDir != null) {
		for (File file : listDir) {
			FileObject fo = new FileObject(file);
			foList.add(fo);
		}
		}     
		FileSystemFileObjectResult result = new FileSystemFileObjectResult();
		result.fileObject = (FileObject[]) foList.toArray();
		return result;
	}

    @Override
	public FileSystemBooleanResult exists(FileSystemFileObjectParam param) {
	    FileObject f = param.fo;
	    FileSystemBooleanResult result = new FileSystemBooleanResult();
		result.bool = objectToFile(f).exists();
		return result;
	}

    @Override
	public FileSystemPathResult getAbsolutePath(FileSystemFileObjectParam param) {
	    FileObject f = param.fo;
	    FileSystemPathResult result = new FileSystemPathResult();
		result.path = FileSystemConstants.FILE + objectToFile(f).getAbsolutePath();
		return result;
	}

    @Override
	public FileSystemBooleanResult isDirectory(FileSystemFileObjectParam param) {
        FileObject f = param.fo;
        FileSystemBooleanResult result = new FileSystemBooleanResult();
		result.bool = objectToFile(f).isDirectory();
		return result;
	}

    @Override
	public FileSystemByteResult getInputStream(FileSystemFileObjectParam param) throws Exception {
	    FileObject f = param.fo;
		try {
		    FileSystemByteResult result = new FileSystemByteResult();
		    InputStream is = new FileInputStream( (File) f.object /*new File(getAbsolutePath(f))*/);
		    result.bytes = IOUtils.toByteArray(is);
		    return result;
		} catch (FileNotFoundException e) {
			log.error(Constants.EXCEPTION, e);
			return null;
		}
	}

    @Override
	public FileSystemFileObjectResult get(FileSystemPathParam param) {
	    String string = param.path;
	    if (string.startsWith(FileSystemConstants.FILE)) {
		string = string.substring(5);
	    }
	    FileSystemFileObjectResult result = new FileSystemFileObjectResult();
	    FileObject[] fo = new FileObject[1];
		fo[0] = new FileObject(new File(string));
		result.fileObject = fo;
		return result;
	}

    @Override
	public FileSystemFileObjectResult getParent(FileSystemFileObjectParam param) {
	    FileObject f = param.fo;
		String parent = objectToFile(f).getParent();
		File file = new File(parent);
        FileSystemFileObjectResult result = new FileSystemFileObjectResult();
        FileObject[] fo = new FileObject[1];
		fo[0] = new FileObject(file);
        result.fileObject = fo;
        return result;
	}
    
    @Override
    public FileSystemConstructorResult destroy() {
        return null;
    }

    private File objectToFile(FileObject fo) {
        File result = null;
        if (fo.object instanceof File) {
            result = (File) fo.object;
        }
        if (fo.object instanceof String) {
            result = new File((String) fo.object);
        }
        return result;
    }
    
}
