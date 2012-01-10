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

    public static HashSet<String> doList (String dirname, HashMap<String, HashSet<String>> dirset) throws Exception {
	HashSet<String> retset = new HashSet<String>();
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
		File plainFile = new File(filename);
		Files files = Files.ensureExistence(filename);
		//files.setTouched(new Boolean(true));
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
	HashMap<String, String> filesMapMd5 = new HashMap<String, String>();
	HashMap<String, String> filesMapFilename = new HashMap<String, String>();
	for (Files file : files) {
	    filesMapMd5.put(file.getMd5(), file.getFilename());
	    filesMapFilename.put(file.getFilename(), file.getMd5());
	}
	HashMap<String, Boolean> indexMap = new HashMap<String, Boolean>();
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
	HashSet<String> md5set = new HashSet<String>();
	String dirname = add;
	File dir = new File(dirname);
	File listDir[] = dir.listFiles();
	//log.info("dir " + dirname);
	//log.info("listDir " + listDir.length);
	for (int i = 0; i < listDir.length; i++) {
	    String filename = listDir[i].getAbsolutePath();
	    //log.info("file " + filename);
	    if (listDir[i].isDirectory()) {
		//log.info("isdir " + filename);
		retlist.addAll(index(filename));
	    } else {
		//log.info("retset " + filename);
		//Reader reader = new ParsingReader(parser, stream, ...);
		File plainFile = new File(filename);
		Files files = Files.getByFilename(filename);
		//files.setTouched(new Boolean(true));
		if (files == null || files.getMd5() == null) {
		    continue;
		}
		retlist.add(filename);
		String md5 = files.getMd5();
		Index index = Index.getByMd5(md5);
		if (index == null) {
		    continue;
		}

		HashMap<String, String> filesMapMd5 = new HashMap<String, String>();
		filesMapMd5.put(files.getMd5(), files.getFilename());

		HashMap<String, Boolean> indexMap = new HashMap<String, Boolean>();
		indexMap.put(index.getMd5(), index.getIndexed());

		indexsingle(retlist, md5, indexMap, filesMapMd5);
	    }
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
	    log.info("indexing " + filename);
	    Index index = Index.ensureExistence(md5);
	    //InputStream stream = null;
	    int size = 0;
	    size = doTika(filename, filename, md5, index, retlist);
	    int limit = mylimit(filename);
	    if (false && size <= limit) {
		String lowercase = filename.toLowerCase();
		if (lowercase.endsWith(".djvu") || lowercase.endsWith(".djv")) {
		    String[] env = { filename };
		    String output = execute("/usr/bin/djvutxt", env);
		}
		if (lowercase.endsWith(".epub")) {
		    String[] env = { filename, "/tmp/t.txt" };
		    String output = execute("/usr/bin/ebook-convert", env);
		}
		if (lowercase.endsWith(".pdf")) {
		    String[] env = { filename, "/tmp/t.txt" };
		    String output = execute("/usr/bin/pdftotext", env);
		}
		size = doTika(filename, "/tmp/t.txt", md5, index, retlist);
	    }
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
		index.setIndexed(new Boolean(true));
	    } else {
		log.info("Too small " + filename + " " + md5 + " " + size + " " + limit);
		retlist.add(dbfilename + " " + md5 + " " + size);
	    }
	    log.info("size2 " + size);
	    inputStream.close();
	    outputStream.close();
	} catch (Exception e) {
	    log.info(e);
	    log.error("Exception", e);
	} finally {
	    log.info("bla");
	    //stream.close();            // close the stream
	}
	return size;
    }

    private static String execute(String filename, String[] env) {
        Process proc = null;
        try {
	    proc = Runtime.getRuntime().exec(filename, env);
            if (proc != null) proc.waitFor();
        } catch (Exception e) {
	    log.info("Exception" + e);
	    log.error("Exception", e);
        }
	/*
	StringBuilder buffer = new StringBuilder();
	BufferedInputStream br = new BufferedInputStream(proc.getInputStream());
	while (br.available() != 0) {
	    buffer.append((char) br.read());
	}
	String res = buffer.toString().trim();
	return res;
	*/
	return null;
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