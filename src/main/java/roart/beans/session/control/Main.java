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
import java.util.HashSet;
import java.util.HashMap;

import java.util.Iterator;

import java.io.*;

import roart.dir.Traverse;

import roart.model.Files;
import roart.model.Index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Main {
    private Log log = LogFactory.getLog(this.getClass());

    public List<String> traverse() {
	List<String> retList = new ArrayList<String>();
	TreeMap<Integer, String> sortlist = new TreeMap<Integer, String>();
	HashMap<String, HashSet<String>> dirset = new HashMap<String, HashSet<String>>();
	try {
	    HashSet<String> fileset = new HashSet<String>();
	    //HashSet<String> filesetnew = new HashSet<String>();
	    List<Files> files = Files.getAll();
	    log.info("size " + files.size());
	    for (Files file : files) {
		//log.info("size2 " + file.getFilename());
		fileset.add(file.getFilename());
	    }
	    files.clear();
	    //HashSet<String> md5set = new HashSet<String>();
	    String[] dirlist = { "/home/roart/usr/music", "/home/roart/usr/video", "/home/roart/usr/abook", "/home/roart/usr/books"  };

	    HashSet<String> filesetnew = new HashSet<String>();

	    for (int i = 0; i < dirlist.length; i ++) {
		HashSet<String> filesetnew2 = Traverse.doList(dirlist[i], dirset);
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
	    roart.model.HibernateUtil.commit();
	    roart.model.HibernateUtil.currentSession().close();
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
		if (ratio > 9) {
		    set1.addAll(set4);
		    Integer intI = new Integer(ratio);
		    sortlist.put(intI, keyList.get(i) + " : " + keyList.get(j) + " " + set1);
		}
	    }
	}
	for (Integer intI : sortlist.keySet()) {
	    retList.add("" + intI.intValue() + " : " + sortlist.get(intI));
	}
	return retList;
    }

    public List<String> index() {
	List retlist = null;
	try {
	    retlist = Traverse.index();

	    HashSet<String> filesindexset = new HashSet<String>();
	    List<Files> files = Files.getAll();
	    log.info("size " + files.size());
	    for (Files file : files) {
		//log.info("size2 " + file.getFilename());
		filesindexset.add(file.getMd5());
	    }

	    HashSet<String> indexset = new HashSet<String>();
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
	    roart.model.HibernateUtil.commit();
	    roart.model.HibernateUtil.currentSession().close();
	} catch (Exception e) {
		log.info(e);
		log.error("Exception", e);
	}
	return retlist;
    }

    public List<String> cleanup() {
	try {
	    return roart.search.SearchLucene.removeDuplicate();
	} catch (Exception e) {
		log.info(e);
		log.error("Exception", e);
	}
	return null;
    }

}

/*
  rutet
  kjott 297 -

 */
