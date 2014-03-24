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

import roart.beans.session.misc.Dao;

import javax.servlet.http.*;
import java.util.Vector;
import java.util.Enumeration;

import java.io.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileDao implements Dao {
    private Log log = LogFactory.getLog(this.getClass());
    public List<Unit> getunits(String type, boolean showsecret) {
	String filename = type;
	List<Unit> retlist = new ArrayList<Unit>();
	try {
	    String datadir = roart.util.Prop.getProp().getProperty("datadir");
	    FileInputStream fstream = new FileInputStream(datadir+type+".txt");
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine = null;
	    while ((strLine = br.readLine()) != null) {
		log.info (strLine);
		//tmpwrite("0 "+strLine);
		String[] strarr = strLine.split(" ");
		//tmpwrite("1 "+strarr.length);
		Unit myunit = new Unit();

		int date_i = 0;
		int counttype_i = 1;
		int price_i = 2;
		int creator_i = 3;
		int isbn_i = 0;

		String count = "1";
		String typename = type;
		String price = "0";
		String isbn = "0";
		String date = null;

		if (type.equals("cd") || type.equals("dvd")) {
		    typename = strarr[counttype_i].replaceAll("[0-9]*","");
		    count = strarr[counttype_i].replaceAll("[a-z]*","");
		    if (count == null || count.length() == 0) {
			count = "1";
		    }
		}
		if (type.equals("vhs")) {
		    creator_i = 1;
		}
		if (type.equals("tape")) {
		    creator_i = 2;
		}
		if (type.equals("book") || type.equals("booku") || type.equals("book0")) {
		    price_i = 1;
		    isbn_i = 2;
		    creator_i = 3;
		    isbn = strarr[isbn_i];
		}
		if (type.equals("book0gen")) {
		    creator_i = 0;
		    price_i = 0;
		}
		if (!type.equals("vhs") && !type.equals("tape")) {
		    price = strarr[price_i];
		}

		date = strarr[date_i];

		if (type.equals("book") || type.equals("booku") || type.equals("book0") || type.equals("book0gen")) {
		    if (date.substring(0,1).equals("x")) {
			if (!showsecret) {
			    continue;
			} else {
			    date = date.substring(1);
			}
		    } else {
			if (showsecret) {
			    price = "0";
			}
		    }
		}

		myunit.setDate(date);
		myunit.setCount(count);
		myunit.setType(typename);
		myunit.setPrice(price);
		myunit.setCreator(strarr[creator_i]);
		myunit.setIsbn(isbn);
		int index = strLine.indexOf(strarr[creator_i]) + strarr[creator_i].length() + 1;
		log.info("index " + index + " " + strarr[creator_i]);
		if (index != -1) {
		myunit.setTitle(strLine.substring(index));
		} else {
		    log.info("Error index " + index + " " + strarr[creator_i]);
		}
		retlist.add(myunit);
	    }
	    in.close();
	} catch (Exception e) {
	    log.info("Error3: " + e.getMessage());
	    log.error("Exception", e);
	    Unit myunit = new Unit();
	    myunit.setTitle("1"+e);
	    retlist.add(myunit);
	}

	if (type.equals("dvd")) {
	    retlist.addAll(getunits("vhs"));
	    retlist = bubble_sort(retlist);
	}

	if (type.equals("cd")) {
	    retlist.addAll(getunits("tape"));
	    retlist = bubble_sort(retlist);
	}

	return retlist;
    }

    public List<Unit> getunits(String type) {
	return getunits(type, false);
    }

    private List<Unit> bubble_sort(List<Unit> arr) {
	for (int i=0; i<arr.size(); i++) {
	    for (int j=0; j<i; j++) {
		Unit u1 = arr.get(i);
		Unit u2 = arr.get(j);
		String s1 = u1.getDate();
		String s2 = u2.getDate();
		if (s1 == null || s2 == null) {
		    continue;
		}
		if (s1.compareTo(s2) < 0) {
		    arr.set(i, u2);
		    arr.set(j, u1);
		}
	    }
	}
	return arr;
    }

    public List<Unit> getcds() {
	return null;
    }

    public List<Unit> getdvds() {
	return null;
    }

    public List<Unit> getbooks() {
	return null;
    }

}
