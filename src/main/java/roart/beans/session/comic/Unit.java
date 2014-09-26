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

import javax.servlet.http.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Unit {
    private Log log = LogFactory.getLog(this.getClass());
    private String date;
    private String price;
    private int count;
    private String type;
    private ArrayList<String> content;
    private ArrayList<String> contentsum;
    private String title;

    public void unit(String date, String price, String count, String type, String content, String title) {
	setDate(date);
	setPrice(price);
	setCount(count);
	setType(type);
	addContent(content);
	setTitle(title);
    }

    public String getDate() {
	return date;
    }

    public void setDate(String value) {
	date = value;
    }

    public String getPrice() {
	return price;
    }

    public void setPrice(String value) {
	price = value;
    }

    public String getCount() {
	return ""+count;
    }

    public void setCount(String value) {
	count = Integer.valueOf(value);
    }

    public String getType() {
	return type;
    }

    public void setType(String value) {
	type = value;
    }

    public ArrayList<String> getContent() {
	return content;
    }

    public void addContent(String value) {
	//content = value;
	if (content == null) {
	    content = new ArrayList<String>();
	}
	content.add(value);
    }

    private Integer getstart(String val) {
	if (val.substring(0,1).equals("-")) {
	    return new Integer(val);
	}
	if (val.indexOf("-") >= 0) {
	    String[] tmp_arr = val.split("-");
	    return new Integer(tmp_arr[0]);
	} else {
	    return new Integer(val);
	}
    }

    private Integer getstop(String val) {
	if (val.substring(0,1).equals("-")) {
	    return null;
	}
	if (val.indexOf("-") >= 0) {
	    String[] tmp_arr = val.split("-");
	    return new Integer(tmp_arr[1]);
	} else {
	    return null;
	}
    }

    private int getcount(String val) {
	Integer start = getstart(val);
	Integer stop = getstop(val);
	if (stop == null) {
	    return 1;
	} else {
	    return 1 + (stop.intValue() - start.intValue());
	}
    }

    public void addContentNum(String value, String year) {
	//content = value;
	String yearprefix = "  ";
	if (year != null) {
	    yearprefix = "  " + year + " : ";
	}
	if (content == null) {
	    content = new ArrayList<String>();
	    //content.add(yearprefix + (value.split(" "))[0]);
	    //contentsum = new ArrayList<String>();
	    //contentsum.add((value.split(" "))[0]);
	    //count += getcount((value.split(" "))[0]);
	    //return;
	    // num count
	}
	//log.info(value);
	content.add(yearprefix + value.replace(',',' '));
	String[] val_arr = null;
	val_arr = value.split(" ");
	for (int i = 0; i < val_arr.length; i++) {
	    String val = val_arr[i];
	    //log.info(val);
	    Integer start = getstart(val);
	    Integer stop = getstop(val);
	    //log.info("v1 "+i+" "+val);
	    count += getcount(val);
	    if (contentsum == null) {
		contentsum = new ArrayList<String>();
		contentsum.add(val);
		continue;
	    }
	    // remember to note overlaps
	    for (int j = 0; j < contentsum.size(); j++) {
		String sumval = contentsum.get(j);
		Integer sumstart = getstart(sumval);
		Integer sumstop = getstop(sumval);
		//log.info("v2 "+j+" "+contentsum.size()+" "+sumval);
		if (Integer.valueOf(start) < Integer.valueOf(sumstart)) {
		    boolean update = false;
		    boolean add = false;
		    contentsum.add(j, ""+start);
		    break;
		}
		//log.info(" " + j + " " + contentsum.size() + " " + start + " " + stop + " " + sumstart + " " + sumstop);
		// if ((j + 1) == contentsum.size())
		{
		    //log.info(" " + (j+1) + " " + contentsum.size() + " " + ((j + 1) == contentsum.size()));
		    boolean cont = false;
		    if (sumstop == null) {
			cont = (start.intValue() == (sumstart.intValue() + 1));
		    } else {
			cont = (start.intValue() == (sumstop.intValue() + 1));
		    }
		    if (cont) {
			//log.info("comp "+((new Integer(start)).intValue()+" "+((new Integer(sumstop)).intValue() + 1))+" "+((new Integer(start)).intValue() == ((new Integer(sumstop)).intValue() + 1)));
			if (stop == null) {
			    contentsum.set(j, sumstart+"-"+start);
			    break;
			} else {
			    contentsum.set(j, sumstart+"-"+stop);
			    break;
			}
		    }
		    // else
		    Integer nextstart = null;
		    if ((j + 1) < contentsum.size()) {
			nextstart = getstart (contentsum.get(j+1));
		    }
		    //log.info("" + (start.intValue()+ " "+ sumstart.intValue() + " " + (start.intValue() > sumstart.intValue())));
		    if (start.intValue() > sumstart.intValue() && (nextstart == null || start.intValue() < nextstart.intValue()))
		    {
			if (stop == null) {
			    contentsum.add(j+1, ""+start);
			    break;
			} else {
			    contentsum.add(j+1, ""+start+"-"+stop);
			    break;
			}
		    }
		}
	    }
	}
    }

    public ArrayList<String> getContentsum() {
	return contentsum;
    }

    public String showContentsum() {
	if (contentsum == null)
	    return "";
	String ret = "Total ";
	for (int i = 0; i < contentsum.size(); i++) {
	    ret = ret + contentsum.get(i);
	    ret = ret + ", ";
	}
	return ret;
    }

    public void setContentsum(String value) {
	//content = value;
	contentsum.add(value.replace(',',' '));
	
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String value) {
	title = value;
    }

}
