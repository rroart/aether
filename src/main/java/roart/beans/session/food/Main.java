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
package roart.beans.session.food;

import roart.beans.session.food.Dao;
import roart.beans.session.food.FileDao;

import javax.servlet.http.*;
import java.util.Vector;
import java.util.Enumeration;

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
	List<String[]> myunits = mydao.getunits(type);
	List<String> titles = new ArrayList<String>();
	for(int i=0;i<myunits.size();i++) {
	    String title = myunits.get(i)[0];
	    titles.add(title);
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
		if (s1.compareTo(s2) < 0) {
		    arr[i] = s2;
		    arr[j] = s1;
		}
	    }
	}
	return arr;
    }

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

    private List<String[]> bubble_sort_weight(List<String[]> arr, List<Long> weight) {
        for (int i=0; i<arr.size(); i++) {
            for (int j=0; j<i; j++) {
                String[] u1 = arr.get(i);
                String[] u2 = arr.get(j);
                Long l1 = weight.get(i);
                Long l2 = weight.get(j);
                if (l1 < l2) {
                    arr.set(i, u2);
                    arr.set(j, u1);
                    weight.set(i, l2);
                    weight.set(j, l1);
                }
            }
        }
        for (int i=0; i<arr.size(); i++) {
	    arr.get(i)[0] = "" + weight.get(i) + " " + arr.get(i)[0];
	}
        return arr;
    }

    public List<String[]> searchtitle(String type, String title) {
	List<String[]> myunits = mydao.getunits(type);
	List<String[]> titles = new ArrayList<String[]>();
	for(int i=0;i<myunits.size();i++) {
	    if (myunits.get(i)[0].equals(title)) {
		titles.add(myunits.get(i));
	    }
	}
	return bubble_sort_title(titles, type);
    }

    private int rec_cst = 10;
    private int syn_cst = 8;
    private int equ_cst = 5;
    
    // search in a file called type.txt (recipis)
    // stuff is what to search for, space-separated

    public List<String[]> searchstuff(String type, String stuff) {
	List<Long> weight = new ArrayList<Long>();
	List<String[]> myunits = mydao.getunits(type);
	// equivalents
	List<String[]> myequ = mydao.getunits("ekvivalent");
	// synonyms
	List<String[]> mysyn = mydao.getunits("synonym");
	List<String[]> titles = new ArrayList<String[]>();
	// get ingredients to search for
	String[] queries = stuff.split(" ");
	String queries2 = "";
	// search only one level back, I think
	for(int g=0;g<2;g++) {
	    // iterate through list one
	for(int h=0;h<queries.length;h++) {
	    String query=queries[h];
	/*
	List<String> queries = new ArrayList<String>();
	for(int h=0;h<queries2.length;h++) {
	    String query=queries2[h];
	    queries.add(query);
	}
	Iterator<String> query_it = queries.iterator();
	while(query_it.hasNext()) {
	    String query = query_it.next();
	*/
	    // search the recipis
	    for(int i=0;i<myunits.size();i++) {
		String[] rec = myunits.get(i);
		// search a recipi for ingredient, skip recipi name (start 1)
		if (arrayfound(rec, query, 1)) {
		    int pos = searchrec(titles, rec);
		    if (pos >= 0) {
			weight.set(pos, weight.get(pos) + new Long(rec_cst));
			log.info("1 "+rec[0]+" "+weight.get(pos));
		    } else {
			titles.add(rec);
			weight.add(new Long(rec_cst));
			queries2 = queries2 + " " + rec[0];
			log.info("2 "+rec[0]);
		    }
		}
		String[] synarr = arrayfound(mysyn, query, 0);
		if (synarr != null) {
		    if (arrayfound(rec, synarr, query)) {
		    int pos = searchrec(titles, rec);
		    if (pos >= 0) {
			weight.set(pos, weight.get(pos) + new Long(syn_cst));
			log.info("3 "+rec[0]+" "+weight.get(pos));
		    } else {
			titles.add(rec);
			weight.add(new Long(syn_cst));
			queries2 = queries2 + " " + rec[0];
			log.info("4 "+rec[0]);
		    }
		    }
		}
		String[] equarr = arrayfound(myequ, query, 0);
		if (equarr != null) {
		    if (arrayfound(rec, equarr, query)) {
		    int pos = searchrec(titles, rec);
		    if (pos >= 0) {
			weight.set(pos, weight.get(pos) + new Long(equ_cst));
			log.info("5 "+rec[0]+" "+weight.get(pos));
		    } else {
			titles.add(rec);
			weight.add(new Long(equ_cst));
			queries2 = queries2 + " " + rec[0];
			log.info("6 "+rec[0]);
		    }
		    }
		}
	    }

	}
	queries2 = queries2.substring(1);
	queries = queries2.split(" ");
	log.info(queries2);
	}
	return bubble_sort_weight(titles, weight);
    }

    int searchrec(List<String[]> arrlist, String[] array) {
	for(int j=0;j<arrlist.size();j++) {
	    if (arrlist.get(j) == array) {
		return j;
	    }
	}
	return -1;
    }
    
    // search for string query in array, start at certain place
    // at 1 for recipis, to skip name

    boolean arrayfound(String[] array, String query, int start) {
	for(int j=start;j<array.length;j++) {
	    if (array[j].equals(query)) {
		return true;
	    }
	}
	return false;
    }
    
    boolean arrayfound(String[] array, String[] array2, String query) {
	for(int i=1;i<array.length;i++) {
	    for(int j=0;j<array2.length;j++) {
		if (array2[j].equals(query)) {
		    continue;
		}
		if (array2[j].equals(array[i])) {
		    return true;
		}
	    }
	}
	return false;
    }
    
    String[] arrayfound(List<String[]> array, String query, int start) {
	for(int j=start;j<array.size();j++) {
	    String[] strarray=array.get(j);
	    if (arrayfound(strarray, query, start)) {
		return strarray;
	    }
	}
	return null;
    }
    
    public List<String[]> getvocalbulary() {
	List<String[]> myunits = mydao.getunits("oppskrift");
	List<String[]> myequ = mydao.getunits("ekvivalent");
	List<String[]> mysyn = mydao.getunits("synonym");
	List<String[]> words = new ArrayList<String[]>();
	List<List> biglist = new ArrayList<List>();
	biglist.add(myunits);
	biglist.add(myequ);
	biglist.add(mysyn);
	for(int h=0;h<biglist.size();h++) {
	    List list = biglist.get(h);
	    for(int i=0;i<list.size();i++) {
		String[] unit = (String []) list.get(i);
		for(int j=0;j<unit.length;j++) {
		    if (arrayfound(words, unit[j], 0) == null) {
			String[] str = { unit[j] };
			words.add(str);
		    }
		}
	    }
	}
	return bubble_sort_title(words, "oppskrift");
    }

}

/*
  rutet
  kjott 297 -

 */
