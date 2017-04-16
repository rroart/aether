package roart.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import roart.config.ConfigConstants;
import roart.config.NodeConfig;
import roart.model.FileObject;
import roart.util.Constants;
import roart.util.FileSystemConstants;

import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.AuthenticationMethod;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.Directory;
import org.javaswift.joss.model.DirectoryOrObject;
import org.javaswift.joss.model.StoredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Swift extends FileSystemOperations {

	private static final Logger log = LoggerFactory.getLogger(Swift.class);
	
	private SwiftConfig conf;
	
	public Swift(String nodename, NodeConfig nodeConf) {
	    conf = new SwiftConfig();
		String url = nodeConf.swifturl;
		String username = nodeConf.swiftuser;
		String password = nodeConf.swiftkey;
		if (url != null) {
		    AccountConfig config;
			config = new AccountConfig();
		    config.setUsername( username);
		    config.setPassword(password);
		    config.setAuthUrl(url);
		    config.setAuthenticationMethod(AuthenticationMethod.BASIC);
		    Account account = new AccountFactory(config).createAccount();
		    conf.account = account;
		}
	}
	
    @Override
	public FileSystemFileObjectResult listFiles(FileSystemFileObjectParam param) {
	    FileObject f = param.fo;
		List<FileObject> foList = new ArrayList<FileObject>();
		DirectoryOrObject mydir = (DirectoryOrObject) f.object;
		try {
			String containerName = param.conf.swiftcontainer;
			Container container = conf.account.getContainer(containerName);
			if (mydir.isObject()) {
				foList.add(f);
			} else {
				Directory dir = mydir.getAsDirectory();
				Collection<DirectoryOrObject> list = container.listDirectory(dir);
				for (DirectoryOrObject doo : list) {
					FileObject fo = new FileObject(doo);
					foList.add(fo);
				}
			}
	        FileSystemFileObjectResult result = new FileSystemFileObjectResult();
	        result.fileObject = (FileObject[]) foList.toArray();
	        return result;
		} catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
			return null;
		}
	}

    @Override
	public FileSystemBooleanResult exists(FileSystemFileObjectParam param) {
	    FileObject f = param.fo;
		DirectoryOrObject path = (DirectoryOrObject) f.object;
		boolean exist;
		try {
			DirectoryOrObject mydir = (DirectoryOrObject) f.object;
			String dirName = mydir.getName();
			String containerName = param.conf.swiftcontainer;
			Container container = conf.account.getContainer(containerName);
			StoredObject so = container.getObject(dirName);
			// note that a directory does not exist, only files
			if (so.exists()) {
				exist = true;
			} else {
			    Collection<DirectoryOrObject> list = container.listDirectory(dirName, '/', null, 1);
			    exist = !list.isEmpty();
			}
		} catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
			exist = false;
		}
        FileSystemBooleanResult result = new FileSystemBooleanResult();
        result.bool = exist;
        return result;
	}

    @Override
	public FileSystemPathResult getAbsolutePath(FileSystemFileObjectParam param) {
	    FileObject f = param.fo;
		DirectoryOrObject path = (DirectoryOrObject) f.object;
		String p = path.getName();
        FileSystemPathResult result = new FileSystemPathResult();
        result.path = FileSystemConstants.SWIFT + p;
        return result;
	}

    @Override
	public FileSystemBooleanResult isDirectory(FileSystemFileObjectParam param) {
	    FileObject f = param.fo;
	    boolean isDirectory;
	    try {
			DirectoryOrObject doo = (DirectoryOrObject) f.object;
			isDirectory = doo.isDirectory();
		} catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
			isDirectory = false;
		}
        FileSystemBooleanResult result = new FileSystemBooleanResult();
        result.bool = isDirectory;
        return result;	    
	}

    @Override
	public FileSystemByteResult getInputStream(FileSystemFileObjectParam param) {
	    FileObject f = param.fo;
		try {
			DirectoryOrObject doo = (DirectoryOrObject) f.object;
			StoredObject so = doo.getAsObject();
            FileSystemByteResult result = new FileSystemByteResult();
            result.bytes = so.downloadObject();
            return result;
	} catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
			return null;
		}
	}

    @Override
	public FileSystemFileObjectResult getParent(FileSystemFileObjectParam param) {
	    FileObject f = param.fo;
		DirectoryOrObject doo = (DirectoryOrObject) f.object;
		String name = doo.getName();
		File fi = new File(name);
		String parent = fi.getParent();
		DirectoryOrObject pardoo = new Directory(parent, '/');
        FileSystemFileObjectResult result = new FileSystemFileObjectResult();
        FileObject[] fo = new FileObject[1];
        fo[0] = new FileObject(pardoo);
        result.fileObject = fo;
        return result;
	}

    @Override
	public FileSystemFileObjectResult get(FileSystemPathParam param) {
	    String string = param.path;
	    if (string.startsWith(FileSystemConstants.SWIFT)) {
	    	string = string.substring(FileSystemConstants.SWIFTLEN);
	    }
	    // Joss directories don't start with /
	    if (string.startsWith("/")) {
	    	string = string.substring(1);
	    }
		String containerName = param.conf.swiftcontainer;
		Container container = conf.account.getContainer(containerName);
		StoredObject so = container.getObject(string);
		FileObject fo;
		// if it exists, it is a file and not a dir
		if (so.exists()) {
			fo = new FileObject(so);
		} else {
			fo = new FileObject(new Directory(string, '/'));
		}
        FileSystemFileObjectResult result = new FileSystemFileObjectResult();
        FileObject[] fos = new FileObject[1];
        fos[0] = fo;
        result.fileObject = fos;
        return result;
	}

    @Override
    public FileSystemConstructorResult destroy() {
        return null;
    }

}
