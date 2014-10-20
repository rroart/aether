package roart.jpa;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;

import roart.model.FileObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HDFS {

	private static final Logger log = LoggerFactory.getLogger(HDFS.class);
	static Configuration configuration;
	
	public HDFS() {
		configuration = new Configuration();
		String fsdefaultname = roart.util.Prop.getProp().getProperty("hdfsconffs");
		if (fsdefaultname != null) {
		    configuration.set("fs.default.name", fsdefaultname);
		}
	}
	
	public static List<FileObject> listFiles(FileObject f) {
		List<FileObject> foList = new ArrayList<FileObject>();
		FileSystem fs;
		try {
			fs = FileSystem.get(configuration);
		Path dir = (Path) f.object;
		FileStatus[] status = fs.listStatus(dir);
		Path[] listedPaths = FileUtil.stat2Paths(status);
		for (Path path : listedPaths) {
			FileObject fo = new FileObject(path);
			foList.add(fo);
		}
		return foList;
		} catch (IOException e) {
			log.error("Exception", e);
			return null;
		}
	}

	public static boolean exists(FileObject f) {
		Path path = (Path) f.object;
		try {
			FileSystem fs = FileSystem.get(configuration);
			return fs.exists(path);
		} catch (Exception e) {
			log.error("Exception", e);
			return false;
		}
	}

	public static String getAbsolutePath(FileObject f) {
		// TODO Auto-generated method stub
		Path path = (Path) f.object;
		return path.getName();
		/*
		try {
			FileSystem fs = FileSystem.get(configuration);
			FileStatus fstat = fs.getFileStatus(path);
			
		} catch (Exception e) {
			log.error("Exception", e);
			return null;
		}
		*/
	}

	public static boolean isDirectory(FileObject f) {
		// TODO Auto-generated method stub
		Path path = (Path) f.object;
		try {
			FileSystem fs = FileSystem.get(configuration);
			return fs.isDirectory(path);
		} catch (Exception e) {
			log.error("Exception", e);
			return false;
		}
	}

	public static InputStream getInputStream(FileObject f) {
		FileSystem fs;
		try {
			fs = FileSystem.get(configuration);
			return fs.open((Path) f.object);
	} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("Exception", e);
			return null;
		}
	}

	public static FileObject getParent(FileObject f) {
		return new FileObject(((Path) f.object).getParent());
	}

	public static FileObject get(String string) {
		return new FileObject(new Path(string));
	}

}
