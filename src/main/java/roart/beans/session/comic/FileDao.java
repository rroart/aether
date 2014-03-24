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

import javax.servlet.http.*;
import java.util.Vector;
import java.util.Enumeration;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileDao implements Dao {
    private Log log = LogFactory.getLog(this.getClass());
    public List<Unit> getunits(String type) /*throws Exception*/ {
	String filename = type;
	List<Unit> retlist = new ArrayList<Unit>();
	try {
	    String datadir = roart.util.Prop.getProp().getProperty("datadir");
	    FileInputStream fstream = new FileInputStream(datadir+type+".txt");
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    Unit myunit = null;
	    String year = null;
	    String price = null;
	    String remark = null;
	    String count = null;
	    String strLine = null;
	    while ((strLine = br.readLine()) != null) {
		log.info (strLine);
		//tmpwrite("0 "+strLine);
		//String[] strarr = strLine.split(" ");
		//tmpwrite("1 "+strarr.length);
		if (strLine == null || strLine.length() == 0) {
		    if (remark != null) {
			myunit.addContent(remark);
		    }
		    // sum price
		    count = myunit.getCount();
		    int cnt = Integer.valueOf(count);
		    int prc = Integer.valueOf(myunit.getPrice());
		    myunit.addContent("Price " + prc + " count " + cnt + " total " + (prc*cnt));
		    // sum content
		    myunit.addContent(myunit.showContentsum());
		    // show missing
		    continue;
		}

		if (strLine.length() >= 2 && strLine.substring(1,2).equals(":")) {
		    String data = strLine.substring(2);
		    if (strLine.substring(0,1).equals("t")) {
			myunit = new Unit();
			myunit.setTitle(data);
			retlist.add(myunit);
			year = null;
			price = null;
			remark = null;
			count = null;
		    }
		    if (strLine.substring(0,1).equals("y")) {
			year = data;
		    }
		    if (strLine.substring(0,1).equals("p")) {
			price = data;
			myunit.setPrice(price);
		    }
		    if (strLine.substring(0,1).equals("r")) {
			remark = data;
		    }
		    if (strLine.substring(0,1).equals("l")) {
			log.info("bla");
		    }
		} else {
		    if (strLine.equals("?")) {
			log.info("bla");
			//myunit.addContent("?");
		    } else {
			myunit.addContentNum(strLine.replace(',',' '), year);
			if (year != null) {
			    year = "" + (1 + Integer.valueOf(year));
			}
		    }
		}
		//retlist.add(strarr);
	    }
	    in.close();
	} catch (Exception e) {
	    log.info("Error3: " + e + e.getMessage());
	    String[] myString = ("1"+e).split(" ");
	    //throw(e);
	    log.error("Exception", e);
	    //retlist.add(myString);
	}

	return retlist;
    }

    public List<UnitBuy> getunitsBuy(TreeMap<String, Integer> mysums, String type, String year) /*throws Exception*/ {
	String filename = type;
	List<UnitBuy> retlist = new ArrayList<UnitBuy>();
	String[] years = { "10", "11" };
	/*for (String year : years)*/ {
	try {
	    String datadir = roart.util.Prop.getProp().getProperty("datadir");
	    FileInputStream fstream = new FileInputStream(datadir+type+year+".txt");
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    UnitBuy myunit = null;
	    String price = null;
	    String remark = null;
	    String count = null;
	    String strLine = null;
	    while ((strLine = br.readLine()) != null) {
		log.info (strLine);
		//tmpwrite("0 "+strLine);
		//String[] strarr = strLine.split(" ");
		//tmpwrite("1 "+strarr.length);
		if (strLine == null || strLine.length() == 0) {
		    //if (remark != null) {
		    //myunit.addContent(remark);
		    //}
		    // sum price
		    //count = myunit.getCount();
		    //int cnt = ((new Integer(count)).intValue());
		    //int prc = ((new Integer(myunit.getPrice())).intValue());
		    //myunit.addContent("Price " + prc + " count " + cnt + " total " + (prc*cnt));
		    // sum content
		    //myunit.addContent(myunit.showContentsum());
		    // show missing
		    continue;
		}

		if (strLine.length() >= 2 && strLine.substring(0,1).equals(":")) {
		    String data2 = strLine.substring(1);
		    myunit.setData2(data2.substring(0));
		    String[] strarr = data2.split(" ");
		    for (int i=0; i<strarr.length; i+=2) {
			String titleShort = strarr[i];
			count = strarr[i+1];
			Integer sum = mysums.get(titleShort);
			if (sum == null) {
			    sum = new Integer(0);
			}
			sum += new Integer(count);
			mysums.put(titleShort, sum);
		    }
		} else {
		    myunit = new UnitBuy();
		    String[] strarr = strLine.split(" ");
		    myunit.setDate("20"+year+strarr[0]);
		    myunit.setPrice(strarr[1]);
		    int index = strLine.indexOf(strarr[1]) + strarr[1].length() + 1;
		    log.info("iik " + index + " " + strLine.indexOf(strarr[1]) + " " + strarr[1].length());
		    myunit.setData1(strLine.substring(index));
		    retlist.add(myunit);
		}
	    }
	    in.close();
	} catch (Exception e) {
	    log.info("Error3: " + e + e.getMessage());
	    String[] myString = ("1"+e).split(" ");
	    //throw(e);
	    log.error("Exception", e);
	    //retlist.add(myString);
	}
        }
	return retlist;
    }

    private List<String[]> bubble_sort(List<String[]> arr) {
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

    public List<String[]> getcds() {
	return null;
    }

    public List<String[]> getdvds() {
	return null;
    }

    public List<String[]> getbooks() {
	return null;
    }

}
