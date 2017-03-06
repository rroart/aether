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
import roart.config.MyConfig;
import roart.model.FileObject;
import roart.util.Constants;

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

public class Swift {

	private static final Logger log = LoggerFactory.getLogger(Swift.class);
	
	private AccountConfig config;
	
	private static Account account;
	
	public Swift() {
		String url = MyConfig.conf.swifturl;
		String username = MyConfig.conf.swiftuser;
		String password = MyConfig.conf.swiftkey;
		if (url != null) {
			config = new AccountConfig();
		    config.setUsername( username);
		    config.setPassword(password);
		    config.setAuthUrl(url);
		    config.setAuthenticationMethod(AuthenticationMethod.BASIC);
		    account = new AccountFactory(config).createAccount();			
		}
	}
	
	public static List<FileObject> listFiles(FileObject f) {
		List<FileObject> foList = new ArrayList<FileObject>();
		DirectoryOrObject mydir = (DirectoryOrObject) f.object;
		try {
			String containerName = MyConfig.conf.swiftcontainer;
			Container container = account.getContainer(containerName);
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
			return foList;
		} catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
			return null;
		}
	}

	public static boolean exists(FileObject f) {
		DirectoryOrObject path = (DirectoryOrObject) f.object;
		try {
			DirectoryOrObject mydir = (DirectoryOrObject) f.object;
			String dirName = mydir.getName();
			String containerName = MyConfig.conf.swiftcontainer;
			Container container = account.getContainer(containerName);
			StoredObject so = container.getObject(dirName);
			// note that a directory does not exist, only files
			if (so.exists()) {
				return true;
			}
			Collection<DirectoryOrObject> list = container.listDirectory(dirName, '/', null, 1);
			return !list.isEmpty();
		} catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
			return false;
		}
	}

	public static String getAbsolutePath(FileObject f) {
		DirectoryOrObject path = (DirectoryOrObject) f.object;
		String p = path.getName();
		return FileSystemDao.SWIFT + p;
	}

	public static boolean isDirectory(FileObject f) {
		try {
			DirectoryOrObject doo = (DirectoryOrObject) f.object;
			return doo.isDirectory();
		} catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
			return false;
		}
	}

	public static InputStream getInputStream(FileObject f) {
		try {
			DirectoryOrObject doo = (DirectoryOrObject) f.object;
			StoredObject so = doo.getAsObject();
			return so.downloadObjectAsInputStream();
	} catch (Exception e) {
			log.error(Constants.EXCEPTION, e);
			return null;
		}
	}

	public static FileObject getParent(FileObject f) {
		DirectoryOrObject doo = (DirectoryOrObject) f.object;
		String name = doo.getName();
		File fi = new File(name);
		String parent = fi.getParent();
		DirectoryOrObject pardoo = new Directory(parent, '/');
		return new FileObject(pardoo);
	}

	public static FileObject get(String string) {
	    if (string.startsWith(FileSystemDao.SWIFT)) {
	    	string = string.substring(FileSystemDao.SWIFTLEN);
	    }
	    // Joss directories don't start with /
	    if (string.startsWith("/")) {
	    	string = string.substring(1);
	    }
		String containerName = MyConfig.conf.swiftcontainer;
		Container container = account.getContainer(containerName);
		StoredObject so = container.getObject(string);
		FileObject fo;
		// if it exists, it is a file and not a dir
		if (so.exists()) {
			fo = new FileObject(so);
		} else {
			fo = new FileObject(new Directory(string, '/'));
		}
		return fo;
	}

}
