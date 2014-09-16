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
package roart.beans.session.misc;

import roart.beans.session.misc.Unit;
import roart.beans.session.misc.Dao;
import roart.beans.session.misc.FileDao;

import javax.servlet.http.*;
import java.util.Vector;
import java.util.Enumeration;

import java.util.ArrayList;
import java.util.List;

import java.io.*;

import roart.dao.SearchDao;
//import roart.dao.FilesDao;
import roart.dao.IndexFilesDao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Main {
    private Log log = LogFactory.getLog(this.getClass());

    private static Dao mydao = new FileDao();

    public String[] getYears(String type) {
	List<Unit> units = mydao.getunits(type, !isSecret());
	List<String> years = new ArrayList<String>();
	for(int i=0;i<units.size();i++) {
	    String date = units.get(i).getDate();
	    if (date == null) {
		continue;
	    }
	    String year = date.substring(0,4);
	    boolean found = false;
	    for (int j=0; j<years.size(); j++) {
		if (years.get(j).equals(year)) {
		    found = true;
		}
	    }
	    if (! found) {
		years.add(year);
	    }
	}
	String[] s = new String[years.size()];
	for(int i=0;i<years.size();i++) {
	    s[i] = years.get(i);
	}
	return s;
    }
    
    public String[] getCreators(String type) {
	List<Unit> units = mydao.getunits(type, !isSecret());
	List<String> creators = new ArrayList<String>();
	for(int i=0;i<units.size();i++) {
	    String creator = units.get(i).getCreator();
	    if (creator == null) {
		continue;
	    }
	    boolean found = false;
	    if (type.equals("cd") || creator.indexOf("/") < 0) {
		found = false;
		for (int j=0; j<creators.size(); j++) {
		    if (creator == null || creators.get(j) == null) {
			continue;
		    }
		    if (creators.get(j).equals(creator)) {
			found = true;
		    }
		}
		if (! found) {
		    creators.add(creator);
		}
	    } else {
		String[] crearr = units.get(i).getCreator().split("/");
		for (int k=0; k<crearr.length; k++) {
		    found = false;
		    for (int j=0; j<creators.size(); j++) {
			if (creators.get(j).equals(crearr[k])) {
			    found = true;
			}
		    }
		    if (! found) {
			creators.add(crearr[k]);
		    }
		}
	    }
	}
	String[] s = new String[creators.size()];
	for(int i=0;i<creators.size();i++) {
	    s[i] = creators.get(i);
	}
	return bubble_sort(s);
    }
    
    public String[] getTitles(String type) {
	List<Unit> units = mydao.getunits(type, !isSecret());
	List<String> titles = new ArrayList<String>();
	for(int i=0;i<units.size();i++) {
	    String title = units.get(i).getTitle();
	    boolean found = false;
	    found = false;
	    for (int j=0; j<titles.size(); j++) {
		if (titles.get(j).equals(title)) {
		    found = true;
		}
	    }
	    if (! found) {
		titles.add(title);
	    }
	}
	String[] s = new String[titles.size()];
	for(int i=0;i<titles.size();i++) {
	    s[i] = titles.get(i);
	}
	return bubble_sort(s);
    }
    
    private String[] bubble_sort(String[] arr) {
	for (int i=0; i<arr.length; i++) {
	    for (int j=0; j<i; j++) {
		String s1 = arr[i];
		String s2 = arr[j];
		if (s1 == null || s2 == null) {
		    continue;
		}
		if (s1.compareTo(s2) < 0) {
		    arr[i] = s2;
		    arr[j] = s1;
		}
	    }
	}
	return arr;
    }

    public String[] getCdItems() {
	List<Unit> units = mydao.getcds();
	String[] s = new String[units.size()];
	for(int i=0;i<units.size();i++) {
	    s[i] = units.get(i).getTitle();
	}
	return s;
    }
    
    private List<Unit> bubble_sort_title(List<Unit> arr, String type) {
	for (int i=0; i<arr.size(); i++) {
	    for (int j=0; j<i; j++) {
		Unit u1 = arr.get(i);
		Unit u2 = arr.get(j);
		String s1 = u1.getTitle();
		String s2 = u2.getTitle();
		if (s1.compareTo(s2) < 0) {
		    arr.set(i, u2);
		    arr.set(j, u1);
		}
	    }
	}
	return arr;
    }

    public List<Unit> searchyear(String type, String year) {
	List<Unit> units = mydao.getunits(type, !isSecret());
	List<Unit> years = new ArrayList<Unit>();
	for(int i=0;i<units.size();i++) {
	    if (units.get(i).getDate().substring(0,4).equals(year)) {
		years.add(units.get(i));
	    }
	}
	/*
	String[] s = new String[years.size()];
	for(int i=0;i<years.size();i++) {
	    s[i] = years.get(i);
	}
	return s;
	*/
	return years;
    }
    
    public List<Unit> searchcreator(String type, String creator) {
	List<Unit> units = mydao.getunits(type, !isSecret());
	List<Unit> creators = new ArrayList<Unit>();
	for(int i=0;i<units.size();i++) {
	    if (type.equals("cd") || units.get(i).getCreator().indexOf("/") < 0) {
		if (units.get(i).getCreator().equals(creator)) {
		    creators.add(units.get(i));
		}
	    } else {
		String[] crearr = units.get(i).getCreator().split("/");
		for (int j=0; j<crearr.length; j++) 
		    if (crearr[j].equals(creator)) {
			creators.add(units.get(i));
		    }
	    }
	}
	/*
	String[] s = new String[years.size()];
	for(int i=0;i<years.size();i++) {
	    s[i] = years.get(i);
	}
	return s;
	*/
	return bubble_sort_title(creators, type);
    }

    public static void parseconfig() {
	new roart.jpa.SearchSolr();
	System.out.println("config1 parsed");
	//log.info("config parsed");
	String mydb = roart.util.Prop.getProp().getProperty("mydb");
	String myindex = roart.util.Prop.getProp().getProperty("myindex");
	SearchDao.instance(myindex);
	//FilesDao.instance(mydb);
	IndexFilesDao.instance(mydb);
    }

    public List<String> searchme(String type, String str) {
	parseconfig();
	List strlist = new ArrayList<String>();
	String[] strarr = roart.search.Search.searchme(type, str);
	for (String stri : strarr) {
	    strlist.add(stri);
	}
	return strlist;
    }
    
    public List<String> searchme2(String str, String type) {
	parseconfig();
	List strlist = new ArrayList<String>();
	String[] strarr = roart.search.Search.searchme2(str, type);
	for (String stri : strarr) {
	    strlist.add(stri);
	}
	return strlist;
    }
    
    public List<String> searchsimilar(String md5) {
	List strlist = new ArrayList<String>();
	String[] strarr = roart.search.Search.searchsimilar(md5);
	if (strarr == null) {
	    return strlist;
	}
	for (String stri : strarr) {
	    strlist.add(stri);
	}
	return strlist;
    }
    
    public boolean isSecret() {
	//String dr = System.getProperty("user.showsecret");
	String dr = roart.util.Prop.getProp().getProperty("showsecret");
	log.info("secret " + dr);
	return dr == null;
    }
}
