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
package roart.beans.session.training;

import roart.beans.session.training.Dao;

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
    public List<Unit> getunits(TreeMap<String, Integer> mysums, String type) /*throws Exception*/ {
	String filename = type;
	List<Unit> retlist = new ArrayList<Unit>();
	try {
	    String datadir = roart.util.Prop.getProp().getProperty("datadir");
	    FileInputStream fstream = new FileInputStream(datadir+type+".txt");
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    Unit myunit = null;
	    String year = null;
	    int week = 0;
	    String count = null;
	    String strLine = null;
	    boolean ski = true;
	    boolean jog = false;
	    while ((strLine = br.readLine()) != null) {
		log.info (strLine);
		//tmpwrite("0 "+strLine);
		//String[] strarr = strLine.split(" ");
		//tmpwrite("1 "+strarr.length);

		if (strLine.length() >= 2 && strLine.substring(1,2).equals(":")) {
		    String data = strLine.substring(2);
		    if (strLine.substring(0,1).equals("w")) {
			week = new Integer(data);
			week = week - 1;
		    }
		    if (strLine.substring(0,1).equals("y")) {
			year = data;
		    }
		    if (strLine.substring(0,1).equals("s")) {
			ski = true;
			jog = false;
		    }
		    if (strLine.substring(0,1).equals("j")) {
			ski = false;
			jog = true;
		    }
		    if (strLine.substring(0,1).equals("l")) {
			log.info("bla");
		    }
		} else {
		    week = week + 1;
		    myunit = new Unit();
		    myunit.setDate(year+week);
		    myunit.setData(strLine);
		    retlist.add(myunit);
		    String[] strarr = strLine.split(" ");
		    for (int i=0; i<strarr.length; i++) {
			String titleShort = strarr[i];
			count = "1"; //strarr[i+1];
			Integer sum = mysums.get(titleShort);
			if (sum == null) {
			    sum = new Integer(0);
			}
			sum += new Integer(count);
			mysums.put(titleShort, sum);
		    }
		}
		//retlist.add(strarr);
	    }
	    in.close();
	} catch (Exception e) {
	    log.info("Error3: " + e + e.getMessage());
	    String[] myString = ("1"+e).split(" ");
	    //throw(e);
	    e.printStackTrace();
	    //retlist.add(myString);
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

    static int i = 1000;
    private void tmpwrite(String str) {
    FileOutputStream fos; 
    DataOutputStream dos;

    try {

	File file= new File("/tmp/tt"+i);
	i++;
	fos = new FileOutputStream(file);
	dos=new DataOutputStream(fos);
	dos.writeChars(str);
	fos.close();
    } catch (Exception e) {
	e.printStackTrace();
    }
    }

}
