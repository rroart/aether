package roart.dir;

import java.util.Map;
import java.io.*;
import roart.content.*;
import roart.search.*;
import roart.model.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Traverse {

    private static Log log = LogFactory.getLog("Traverse");

    public static Set<String> doList (String dirname, Map<String, HashSet<String>> dirset) throws Exception {
	Set<String> retset = new HashSet<String>();
	HashSet<String> md5set = new HashSet<String>();
	File dir = new File(dirname);
	File listDir[] = dir.listFiles();
	//log.info("dir " + dirname);
	//log.info("listDir " + listDir.length);
	for (int i = 0; i < listDir.length; i++) {
	    String filename = listDir[i].getAbsolutePath();
	    if (filename.length() > 250) {
		log.info("Too large filesize " + filename);
		continue;
	    }
	    //log.info("file " + filename);
	    if (listDir[i].isDirectory()) {
		//log.info("isdir " + filename);
		retset.addAll(doList(filename, dirset));
	    } else {
		//log.info("retset " + filename);
		retset.add(filename);
		//Reader reader = new ParsingReader(parser, stream, ...);
		Files files = Files.ensureExistence(filename);
		//files.setTouched(Boolean.TRUE);
		if (files.getMd5() == null) {
		    try {
			FileInputStream fis = new FileInputStream( new File(filename));
			String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex( fis );
			files.setMd5(md5);
			log.info("adding md5 file " + filename);
		    } catch (Exception e) {
			log.info("Error: " + e.getMessage());
			log.error("Exception", e);
		    }
		}
		md5set.add(files.getMd5());
	    }
	}
	dirset.put(dirname, md5set);
	//log.info("retsize " + retset.size());
	return retset;
    }

    public static List<String> index() throws Exception {
	List<String> retlist = new ArrayList<String>();
	log.info("here");
	List<Files> files = Files.getAll();
	List<Index> indexes = Index.getAll();
	log.info("sizes " + files.size() + " " + indexes.size());
	Map<String, String> filesMapMd5 = new HashMap<String, String>();
	Map<String, String> filesMapFilename = new HashMap<String, String>();
	for (Files file : files) {
	    String filename = file.getFilename();
	    String md5 = file.getFilename();
	    if (md5 == null) {
		retlist.add("No md5 " + filename);
		continue;
	    }
	    filesMapMd5.put(md5, filename);
	    filesMapFilename.put(filename, md5);
	}
	Map<String, Boolean> indexMap = new HashMap<String, Boolean>();
	for (Index index : indexes) {
	    indexMap.put(index.getMd5(), index.getIndexed());
	}
	for (String md5 : filesMapMd5.keySet()) {
	    indexsingle(retlist, md5, indexMap, filesMapMd5);
	}
	return retlist;
    }

    public static List<String> index(String add) throws Exception {
	List<String> retlist = new ArrayList<String>();
	Set<String> md5set = new HashSet<String>();
	String dirname = add;
	File dir = new File(dirname);
	if (!dir.exists()) {
	    retlist.add("File " + add + " does not exist");
	    return retlist;
	}
	File listDir[];
	if (dir.isDirectory()) {
	    listDir = dir.listFiles();
	} else {
	    listDir = new File[1];
	    listDir[0] = dir;
	}
	//log.info("dir " + dirname);
	log.info("listDir " + listDir.length);
	for (int i = 0; i < listDir.length; i++) {
	    String filename = listDir[i].getAbsolutePath();
	    log.info("file " + filename);
	    if (listDir[i].isDirectory()) {
		//log.info("isdir " + filename);
		retlist.addAll(index(filename));
	    } else {
		//log.info("retset " + filename);
		//Reader reader = new ParsingReader(parser, stream, ...);
		Files files = Files.getByFilename(filename);
		//files.setTouched(Boolean.TRUE);
		if (files == null || files.getMd5() == null) {
		    continue;
		}
		retlist.add(filename);
		String md5 = files.getMd5();
		if (md5 == null) {
		    continue;
		}
		Index index = Index.getByMd5(md5);
		if (index == null) {
		    continue;
		}

		Map<String, String> filesMapMd5 = new HashMap<String, String>();
		filesMapMd5.put(files.getMd5(), files.getFilename());

		Map<String, Boolean> indexMap = new HashMap<String, Boolean>();
		indexMap.put(index.getMd5(), index.getIndexed());

		indexsingle(retlist, md5, indexMap, filesMapMd5);
	    }
	    log.info("file " + filename);
	}
	return retlist;
    }

    private static void indexsingle(List<String> retlist, String md5, Map<String, Boolean> indexMap, Map<String, String> filesMapMd5) throws Exception {
	    if (md5 == null) {
		log.info("md5 should not be null");
		return;
	    }
	    Boolean indexed = indexMap.get(md5);
	    if (indexed != null) {
		if (indexed.booleanValue()) {
		    return;
		}
	    }
	    
	    String filename = filesMapMd5.get(md5);
	    Index index = Index.ensureExistence(md5);
	    //InputStream stream = null;
	    int size = 0;
	    size = doTika(filename, filename, md5, index, retlist);
	    int limit = mylimit(filename);
	    log.info("sizes " + size + " " + limit);
	    if (size <= limit) {
		boolean retry = false;
		String lowercase = filename.toLowerCase();
		if (false) {
		    String[] env = { filename, "/tmp/t.txt" };
		    String output = execute("/usr/bin/djvutxt", env);
		    retry = true;
		}
		// epub 2nd try
		if (lowercase.endsWith(".mobi") || lowercase.endsWith(".pdb") || lowercase.endsWith(".epub") || lowercase.endsWith(".lit") || lowercase.endsWith(".djvu") || lowercase.endsWith(".djv") || lowercase.endsWith(".dj")) {
		    String[] env = { filename, "/tmp/t.txt" };
		    String output = execute("/usr/bin/ebook-convert", env);
		    retry = true;
		}
		// pdf 2nd try
		if (lowercase.endsWith(".pdf")) {
		    String[] env = { filename, "/tmp/t.txt" };
		    String output = execute("/usr/bin/pdftotext", env);
		    retry = true;
		}
		File txt = new File("/tmp/t.txt");
		if (retry && txt.exists()) {
		    size = doTika(filename, "/tmp/t.txt", md5, index, retlist);
		}
	    }
	    File txt = new File("/tmp/t.txt");
	    if (txt.exists()) {
		txt.delete();
	    }
    }

    public static List<String> notindexed() throws Exception {
	List<String> retlist = new ArrayList<String>();
	List<Files> files = Files.getAll();
	List<Index> indexes = Index.getAll();
	log.info("sizes " + files.size() + " " + indexes.size());
	Map<String, String> filesMapMd5 = new HashMap<String, String>();
	Map<String, String> filesMapFilename = new HashMap<String, String>();
	for (Files file : files) {
	    String filename = file.getFilename();
	    String md5 = file.getMd5();
	    if (md5 == null) {
		retlist.add("No md5 " + filename);
		continue;
	    }
	    filesMapMd5.put(md5, filename);
	    filesMapFilename.put(filename, md5);
	}
	for (Index index : indexes) {
	    Boolean indexed = index.getIndexed();
	    if (indexed != null) {
		if (indexed.booleanValue()) {
		    continue;
		}
	    }
	    String md5 = index.getMd5();
	    String filename = filesMapMd5.get(md5);
	    retlist.add(filename);
	}
	return retlist;
    }

    private static int doTika(String dbfilename, String filename, String md5, Index index, List<String> retlist) {
	int size = 0;
	try {
	    TikaHandler tika = new TikaHandler();
	    OutputStream outputStream = tika.process(filename);
	    InputStream inputStream =new ByteArrayInputStream(((ByteArrayOutputStream) outputStream).toByteArray());
	    size = ((ByteArrayOutputStream)outputStream).size();
	    log.info("size1 " + size);
	    int limit = mylimit(dbfilename);
	    if (size > limit) {
		size = SearchLucene.indexme("all", md5, inputStream);
		index.setIndexed(Boolean.TRUE);
		retlist.add("Indexed " + dbfilename + " " + md5 + " " + size);
	    } else {
		log.info("Too small " + filename + " " + md5 + " " + size + " " + limit);
		retlist.add("Too small " + dbfilename + " " + md5 + " " + size);
	    }
	    log.info("size2 " + size);
	    inputStream.close();
	    outputStream.close();
	} catch (Exception e) {
	    log.error("Exception", e);
	} finally {
	    log.info("bla");
	    //stream.close();            // close the stream
	}
	return size;
    }

    private static String execute(String filename, String[] arg) {
	String res = null;
        Process proc = null;
        try {
	    //filename = "/tmp/t.sh";
	    //proc = Runtime.getRuntime().exec(filename + " \"" + arg[0] + "\" " + arg[1]);
	    String[] cmdarray = new String[3];
	    cmdarray[0] = filename;
	    cmdarray[1] = arg[0];
	    cmdarray[2] = arg[1];
	    proc = Runtime.getRuntime().exec(cmdarray);
	    log.info("proc " + proc);
            if (proc != null) {
		proc.waitFor();
	    }
	    StringBuilder buffer = new StringBuilder();
	    BufferedInputStream br = new BufferedInputStream(proc.getInputStream());
	    while (br.available() != 0) {
		buffer.append((char) br.read());
	    }
	    res = buffer.toString().trim();
	    log.info("output " + res);
        } catch (Exception e) {
	    log.info("Exception" + e);
	    log.error("Exception", e);
        }
	return res;
    }

    private static int mylimit(String filename) {
	String lowercase = filename.toLowerCase();
	if (lowercase.endsWith(".pdf")) {
	    return 4096;
	}
	if (lowercase.endsWith(".djvu") || lowercase.endsWith(".djv")) {
	    return 4096;
	}
	if (lowercase.endsWith(".mp3")) {
	    return 16;
	}
	return 4096;
    }
    
}