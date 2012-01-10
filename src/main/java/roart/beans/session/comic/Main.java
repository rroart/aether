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
package roart.beans.session.comic;

import roart.beans.session.comic.Dao;
import roart.beans.session.comic.FileDao;

import javax.servlet.http.*;
import java.util.Vector;
import java.util.Enumeration;

import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;

import java.util.Iterator;

import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Main {
    private Log log = LogFactory.getLog(this.getClass());

    private static Dao mydao = new FileDao();

    public String[] getTitles(String type) {
	List<Unit> myunits = mydao.getunits(type);
	List<String> titles = new ArrayList<String>();
	for(int i=0;i<myunits.size();i++) {
	    String title = myunits.get(i).getTitle();
	    titles.add(title);
	}
	String[] s = new String[titles.size()];
	for(int i=0;i<titles.size();i++) {
	    s[i] = titles.get(i);
	}
	return s;
	//return bubble_sort(s);
    }

    public List<Unit> searchtitle(String type, int index) {
	List<Unit> myunits = mydao.getunits(type);
	List<Unit> titles = new ArrayList<Unit>();
	titles.add(myunits.get(index));
	return titles;
	//return bubble_sort_title(titles, type);
    }

    public List<Unit> searchtitle(String type, String letter) {
	List<Unit> myunits = mydao.getunits(type);
	List<Unit> titles = new ArrayList<Unit>();
	for (int i = 0; i < myunits.size(); i++) {
	    if(myunits.get(i).getTitle().indexOf(letter) == 0) {
		titles.add(myunits.get(i));
	    }
	}
	return titles;
	//return bubble_sort_title(titles, type);
    }

    public List<Unit> searchtitle(String type) {
	List<Unit> myunits = mydao.getunits(type);
	return myunits;
	//return bubble_sort_title(titles, type);
    }

    public String[] getYears(String com) {
	String[] years = { "10", "11" };
	return years;
    }

    public List<UnitBuy> searchyear(TreeMap<String, Integer> mysums, String type, String year) {
	List<UnitBuy> myunits = mydao.getunitsBuy(mysums, type, year);
	return myunits;
    }

    /*    
    private String[] bubble_sort(String[] arr) {
	for (int i=0; i<arr.length; i++) {
	    for (int j=0; j<i; j++) {
		String s1 = arr[i];
		String s2 = arr[j];
		if (s1.compareTo(s2) < 0) {
		    arr[i] = s2;
		    arr[j] = s1;
		}
	    }
	}
	return arr;
    }
    */

    /*
    private List<String[]> bubble_sort_title(List<String[]> arr, String type) {
        for (int i=0; i<arr.size(); i++) {
            for (int j=0; j<i; j++) {
                String[] u1 = arr.get(i);
                String[] u2 = arr.get(j);
                String s1 = u1[0];
                String s2 = u2[0];
                if (s1.compareTo(s2) < 0) {
                    arr.set(i, u2);
                    arr.set(j, u1);
                }
            }
        }
        return arr;
    }
    */

}

/*
  rutet
  kjott 297 -

 */