/*
 * Copyright 2004-2005 Sun Microsystems, Inc.  All rights reserved.
 * Use is subject to license terms.
 */

/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * [Additional notices, if required by prior licensing conditions]
 *
 */ 
package roart.beans.session.control;

import javax.servlet.http.*;
import java.util.Vector;
import java.util.Enumeration;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import java.io.*;

import roart.dir.Traverse;

import roart.model.Files;
import roart.model.Index;
import roart.queue.Queues;
import roart.thread.IndexRunner;
import roart.thread.OtherRunner;
import roart.thread.TikaRunner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Main {
    private Log log = LogFactory.getLog(this.getClass());

    public List<String> traverse(String add) throws Exception {
	Map<String, HashSet<String>> dirset = new HashMap<String, HashSet<String>>();
	Set<String> filesetnew2 = Traverse.doList(add, null, dirset, null, false);    
	roart.model.HibernateUtil.commit();
	log.info("Hibernate commit");
	//roart.model.HibernateUtil.currentSession().close();
	return new ArrayList<String>(filesetnew2);
    }

    public List<String> traverse() throws Exception {
	Set<String> filesetnew = new HashSet<String>();
	List<String> retList = filesystem(filesetnew, null);
	roart.model.HibernateUtil.commit();
	log.info("Hibernate commit");
	return retList;
    }

    String[] dirlist = null;
    String[] dirlistnot = null;

    private void parseconfig() {
	String dirliststr = roart.util.Prop.getProp().getProperty("dirlist");
	String dirlistnotstr = roart.util.Prop.getProp().getProperty("dirlistnot");
	System.out.println(dirlist);
	System.out.println(dirlistnot);
	dirlist = dirliststr.split(",");
	dirlistnot = dirlistnotstr.split(",");
    }

    private List<String> filesystem(Set<String> filesetnew, Set<String> newset) {
	List<String> retList = new ArrayList<String>();

	Map<Integer, Set<String>> sortlist = new TreeMap<Integer, Set<String>>();
	Map<String, HashSet<String>> dirset = new HashMap<String, HashSet<String>>();
	try {
	    Set<String> fileset = new HashSet<String>();
	    //Set<String> filesetnew = new HashSet<String>();
	    List<Files> files = Files.getAll();
	    log.info("size " + files.size());
	    for (Files file : files) {
		//log.info("size2 " + file.getFilename());
		fileset.add(file.getFilename());
	    }
	    files.clear();
	    //Set<String> md5set = new HashSet<String>();
	    parseconfig();

	    for (int i = 0; i < dirlist.length; i ++) {
		Set<String> filesetnew2 = Traverse.doList(dirlist[i], newset, dirset, dirlistnot, false);
		filesetnew.addAll(filesetnew2);
	    }
	    //roart.model.HibernateUtil.currentSession().flush();
	    //	    for (String filename : dirset.keySet()) {
	    //	log.info("size2 " + filename);
	    //	filesetnew.add(filename);
	    //}
	    log.info("sizenew " + filesetnew.size());
	    //fileset.removeAll(filesetnew);
	    for (String filename : filesetnew) {
		//log.info("size2 " + filename);
		fileset.remove(filename);
	    }
	    log.info("sizeafter " + fileset.size());
	    for (String filename : fileset) {
		log.info("removing " + filename);
		Files file = Files.getByFilename(filename);
		roart.model.HibernateUtil.currentSession().delete(file);
	    }
	    //roart.model.HibernateUtil.commit();
	    //log.info("Hibernate commit");
	    //roart.model.HibernateUtil.currentSession().close();
	} catch (Exception e) {
		log.info(e);
		log.error("Exception", e);
	}
	return retList;
    }

    public List<String> overlapping() {
	List<String> retList = new ArrayList<String>();

	Set<String> filesetnew = new HashSet<String>();
	Map<Integer, Set<String>> sortlist = new TreeMap<Integer, Set<String>>();
	Map<Integer, Set<String>> sortlist2 = new TreeMap<Integer, Set<String>>();
	Map<String, HashSet<String>> dirset = new HashMap<String, HashSet<String>>();
	Map<String, HashSet<String>> fileset = new HashMap<String, HashSet<String>>();
	try {
	    Set<String> filesetnew2 = Traverse.doList2(dirset, fileset);
	    filesetnew.addAll(filesetnew2);
	} catch (Exception e) {
		log.info(e);
		log.error("Exception", e);
	}

	List<String> keyList = new ArrayList<String>(dirset.keySet());
	for (int i = 0; i < keyList.size(); i++ ) {
	    for (int j = i+1; j < keyList.size(); j++ ) {
		HashSet<String> set1 = (HashSet<String>) dirset.get(keyList.get(i)).clone();
		HashSet<String> set2 = (HashSet<String>) dirset.get(keyList.get(j)).clone();
		HashSet<String> set3 = (HashSet<String>) dirset.get(keyList.get(i)).clone();
		HashSet<String> set4 = (HashSet<String>) dirset.get(keyList.get(j)).clone();
		int size0 = set1.size() + set2.size();
		set1.retainAll(set2);
		set4.retainAll(set3);
		int size = set1.size() + set4.size();
		if (size0 == 0) {
		    size0 = 1000000;
		}
		int ratio = (int) (100*size/size0);
		if (ratio > 50 && size > 4) {
		    set1.addAll(set4);
		    Integer intI = new Integer(ratio);
		    String sizestr = "" + size;
		    sizestr = "      ".substring(sizestr.length()) + sizestr;
		    String str = sizestr + " : " + keyList.get(i) + " : " + keyList.get(j); // + " " + set1;
		    Set<String> strSet = sortlist.get(intI);
		    if (strSet == null) {
			strSet = new TreeSet<String>();
		    }
		    strSet.add(str);
		    sortlist.put(intI, strSet);
		}
	    }
	}
	for (Integer intI : sortlist.keySet()) {
	    for (String str : sortlist.get(intI)) {
		retList.add("" + intI.intValue() + " : " + str);
	    }
	}
	for (int i = 0; i < keyList.size(); i++ ) {
	    int fileexist = 0;
	    String dirname = keyList.get(i);
	    Set<String> dirs = dirset.get(dirname);
	    int dirsize = dirs.size();
	    for (String md5 : dirs) {
		Set<String> files = fileset.get(md5);
		if (files != null && files.size() >= 2) {
		    fileexist++;
		}
	    }
	    int ratio = (int) (100*fileexist/dirsize);
	    // overlapping?
	    if (ratio > 50 && dirsize > 4) {
		Integer intI = new Integer(ratio);
		String sizestr = "" + dirsize;
		sizestr = "      ".substring(sizestr.length()) + sizestr;
		String str = sizestr + " : " + dirname;
		Set<String> strSet = sortlist2.get(intI);
		if (strSet == null) {
		    strSet = new TreeSet<String>();
		}
		strSet.add(str);
		sortlist2.put(intI, strSet);
	    }
	}
	for (Integer intI : sortlist2.keySet()) {
	    for (String str : sortlist2.get(intI)) {
		retList.add("" + intI.intValue() + " : " + str);
	    }
	}
	return retList;
    }

    public List<String> index(String suffix) throws Exception {
    	startThreads();
	List retlist = null;
	try {
	    retlist = Traverse.index(suffix);

	    Set<String> filesindexset = new HashSet<String>();
	    List<Files> files = Files.getAll();
	    log.info("size " + files.size());
	    for (Files file : files) {
		//log.info("size2 " + file.getFilename());
		filesindexset.add(file.getMd5());
	    }

	    Set<String> indexset = new HashSet<String>();
	    List<Index> indexes = Index.getAll();
	    log.info("size " + indexes.size());
	    for (Index index : indexes) {
		//log.info("size2 " + index.getMd5());
		indexset.add(index.getMd5());
	    }

	    log.info("sizei1 " + indexset.size());
	    log.info("sizei2 " + filesindexset.size());
	    indexset.removeAll(filesindexset);
	    log.info("sizeafter " + indexset.size());
	    for (String md5 : indexset) {
		if (md5 == null) {
		    log.info("md5 should not be null");
		    continue;
		}
		log.info("removing " + md5);
		Index index = Index.getByMd5(md5);
		retlist.add("Deleted " + md5);
		//roart.model.HibernateUtil.currentSession().delete(index);
		//roart.search.SearchLucene.deleteme(md5);
	    }

	    //roart.model.HibernateUtil.currentSession().flush();
	    //roart.model.HibernateUtil.currentSession().close();
	} catch (Exception e) {
		log.info(e);
		log.error("Exception", e);
	}
	//while (tikaWorker.isAlive())  {
	//TimeUnit.SECONDS.sleep(1);
	//}
	while ((Queues.queueSize() + Queues.runSize()) > 0) {
		TimeUnit.SECONDS.sleep(60);
		Queues.queueStat();
	}
	for (String ret : Queues.tikaTimeoutQueue) {
		retlist.add("timeout tika " + ret);
	}
	Queues.resetTikaTimeoutQueue();
    roart.model.HibernateUtil.commit();
	log.info("Hibernate commit");
	
	return retlist;
    }

    public List<String> index(String add, boolean reindex) throws Exception {
    	startThreads();
	List retlist = lucene(add, reindex);
	while ((Queues.queueSize() + Queues.runSize()) > 0) {
		TimeUnit.SECONDS.sleep(60);
		Queues.queueStat();
	}
	for (String ret : Queues.tikaTimeoutQueue) {
		retlist.add("timeout tika " + ret);
	}

	Queues.resetTikaTimeoutQueue();
	roart.model.HibernateUtil.commit();
	log.info("Hibernate commit");	

	return retlist;
    }

    public List<String> indexdate(String date, boolean reindex) throws Exception {
    	startThreads();
	List retlist = lucenedate(date, reindex);
	while ((Queues.queueSize() + Queues.runSize()) > 0) {
		TimeUnit.SECONDS.sleep(60);
		Queues.queueStat();
	}
	for (String ret : Queues.tikaTimeoutQueue) {
		retlist.add("timeout tika " + ret);
	}

	Queues.resetTikaTimeoutQueue();
	roart.model.HibernateUtil.commit();
	log.info("Hibernate commit");	

	return retlist;
    }

    private List<String> lucene(String add, boolean reindex) throws Exception {
	List retlist = null;
	try {
	    retlist = Traverse.index(add, reindex);
	    //roart.model.HibernateUtil.currentSession().close();
	} catch (Exception e) {
	    log.info(e);
	    log.error("Exception", e);
	}
	//while (tikaWorker.isAlive())  {
	//TimeUnit.SECONDS.sleep(1);
	//}
	return retlist;
    }

    private List<String> lucenedate(String date, boolean reindex) throws Exception {
	List retlist = null;
	try {
	    retlist = Traverse.reindexdate(date);
	    //roart.model.HibernateUtil.currentSession().close();
	} catch (Exception e) {
	    log.info(e);
	    log.error("Exception", e);
	}
	//while (tikaWorker.isAlive())  {
	//TimeUnit.SECONDS.sleep(1);
	//}
	return retlist;
    }

    // outdated, did run once, had a bug which made duplicates
    public List<String> cleanup() {
	List<String> retlist = new ArrayList<String>();
	try {
	    return roart.search.SearchLucene.removeDuplicate();
	} catch (Exception e) {
		log.info(e);
		log.error("Exception", e);
	}
	return retlist;
    }

    // outdated, used once, when bug added filename instead of md5
    public List<String> cleanup2() {
	List<String> retlist = new ArrayList<String>();
	try {
	    return roart.search.SearchLucene.cleanup2();
	} catch (Exception e) {
		log.info(e);
		log.error("Exception", e);
	}
	return retlist;
    }

    // old, probably oudated by overlapping?
    public List<String> cleanupfs(String dirname) {
	//List<String> retlist = new ArrayList<String>();
	Set<String> filesetnew = new HashSet<String>();
	try {
	    String[] dirlist = { dirname };
	    for (int i = 0; i < dirlist.length; i ++) {
		Set<String> filesetnew2 = Traverse.dupdir(dirlist[i]);
		filesetnew.addAll(filesetnew2);
	    }
	} catch (Exception e) {
		log.info(e);
		log.error("Exception", e);
	}
	return new ArrayList<String>(filesetnew);
    }

    public List<String> memoryusage() {
	List<String> retlist = new ArrayList<String>();
	try {
	    Runtime runtime = Runtime.getRuntime();
	    long maxMemory = runtime.maxMemory();
	    long allocatedMemory = runtime.totalMemory();
	    long freeMemory = runtime.freeMemory();
	    java.text.NumberFormat format = java.text.NumberFormat.getInstance();
	    retlist.add("free memory: " + format.format(freeMemory / 1024));
	    retlist.add("allocated memory: " + format.format(allocatedMemory / 1024));
	    retlist.add("max memory: " + format.format(maxMemory / 1024));
	    retlist.add("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024));
	} catch (Exception e) {
		log.info(e);
		log.error("Exception", e);
	}
	return retlist;
    }

    public List<String> notindexed() throws Exception {
	List<String> retlist = null;
	List<String> retlistyes = null;
	try {
	    retlist = Traverse.notindexed();
	    retlistyes = Traverse.indexed();
	    Map<String, Integer> plusretlist = new HashMap<String, Integer>();
	    Map<String, Integer> plusretlistyes = new HashMap<String, Integer>();
	    for(String filename : retlist) {
		if (filename == null) {
		    continue;
		}
		int ind = filename.lastIndexOf(".");
		if (ind == -1 && ind >= filename.length() - 6) {
		    continue;
		}
		String suffix = filename.substring(ind+1);
		Integer i = plusretlist.get(suffix);
		if (i == null) {
		    i = new Integer(0);
		}
		i++;
		plusretlist.put(suffix, i);
	    }
	    for(String filename : retlistyes) {
		if (filename == null) {
		    continue;
		}
		int ind = filename.lastIndexOf(".");
		if (ind == -1 && ind >= filename.length() - 6) {
		    continue;
		}
		String suffix = filename.substring(ind+1);
		Integer i = plusretlistyes.get(suffix);
		if (i == null) {
		    i = new Integer(0);
		}
		i++;
		plusretlistyes.put(suffix, i);
	    }
	    System.out.println("size " + plusretlist.size());
	    System.out.println("sizeyes " + plusretlistyes.size());
	    for(String string : plusretlist.keySet()) {
		retlist.add("Format " + string + " : " + plusretlist.get(string).intValue());
	    }
	    for(String string : plusretlistyes.keySet()) {
		retlist.add("Formatyes " + string + " : " + plusretlistyes.get(string).intValue());
	    }
	} catch (Exception e) {
	    log.info(e);
	    log.error("Exception", e);
	}
	return retlist;
    }

    public List<String> filesystemlucenenew() throws Exception {
	Set<String> filesetnew = new HashSet<String>();
	Set<String> newset = new HashSet<String>();
	List<String> retlist = filesystem(filesetnew, newset);

    	startThreads();
	for (String filename : newset) {
	    //log.info("size2 " + filename);
	    lucene(filename, false);
	}
	Queues.resetTikaTimeoutQueue();
	while ((Queues.queueSize() + Queues.runSize()) > 0) {
		TimeUnit.SECONDS.sleep(60);
		Queues.queueStat();
	}
	for (String ret : Queues.tikaTimeoutQueue) {
		retlist.add("timeout tika " + ret);
	}
	Queues.resetTikaTimeoutQueue();
	roart.model.HibernateUtil.commit();
	log.info("Hibernate commit");
	return retlist;
    }

    // true: new md5 checks
    // false: only new
    public List<String> filesystemlucenenew(String add, boolean newmd5oronlyfile) throws Exception {
	Map<String, HashSet<String>> dirset = new HashMap<String, HashSet<String>>();
	Set<String> newset = new HashSet<String>();
	List<String> retlist = new ArrayList<String>();
	Set<String> retset = Traverse.doList(add, newset, dirset, null, newmd5oronlyfile);

    	startThreads();
	for (String filename : newset) {
	    //log.info("size2 " + filename);
	    lucene(filename, false);
	}
	Queues.resetTikaTimeoutQueue();
	while ((Queues.queueSize() + Queues.runSize()) > 0) {
		TimeUnit.SECONDS.sleep(60);
		Queues.queueStat();
	}
	for (String ret : Queues.tikaTimeoutQueue) {
		retlist.add("timeout tika " + ret);
	}
	Queues.resetTikaTimeoutQueue();
	roart.model.HibernateUtil.commit();
	log.info("Hibernate commit");
	return retlist;
    }

    private static TikaRunner tikaRunnable = null;
    private static Thread tikaWorker = null;
    private static IndexRunner indexRunnable = null;
    private static Thread indexWorker = null;
    private static OtherRunner otherRunnable = null;
    private static Thread otherWorker = null;
   
    private void startThreads() {
    	if (tikaRunnable == null) {
    	tikaRunnable = new TikaRunner();
    	tikaWorker = new Thread(tikaRunnable);
    	tikaWorker.setName("TikaWorker");
    	tikaWorker.start();
    	}
    	if (indexRunnable == null) {
    	indexRunnable = new IndexRunner();
    	indexWorker = new Thread(indexRunnable);
    	indexWorker.setName("IndexWorker");
    	indexWorker.start();
    	}
    	if (otherRunnable == null) {
    	otherRunnable = new OtherRunner();
    	otherWorker = new Thread(otherRunnable);
    	otherWorker.setName("OtherWorker");
    	otherWorker.start();
    	}
    }
    
}

/*
  rutet
  kjott 297 -

 */
