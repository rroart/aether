package roart.beans.session.training;

import roart.beans.session.training.Dao;
import roart.beans.session.training.FileDao;

import javax.servlet.http.*;
import java.util.Vector;
import java.util.Enumeration;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;

import java.util.Iterator;

import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Main {
    private Log log = LogFactory.getLog(this.getClass());

    private static Dao mydao = new FileDao();

    public String[] getTitles(TreeMap<String, Integer> mysums, String type) {
	List<Unit> myunits = mydao.getunits(mysums, type);
	List<String> titles = new ArrayList<String>();
	for(int i=0;i<myunits.size();i++) {
	    //String title = myunits.get(i).getTitle();
	    //titles.add(title);
	}
	String[] s = new String[titles.size()];
	for(int i=0;i<titles.size();i++) {
	    s[i] = titles.get(i);
	}
	return s;
	//return bubble_sort(s);
    }

    public String[] getYears(String type) {
	TreeMap<String, Integer> mysums = new TreeMap<String, Integer>();
	List<Unit> units = mydao.getunits(mysums, type);
	List<String> years = new ArrayList<String>();
	    log.info("ye ");
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
    
    public List<Unit> searchtitle(TreeMap<String, Integer> mysums, String type, int index) {
	List<Unit> myunits = mydao.getunits(mysums, type);
	List<Unit> titles = new ArrayList<Unit>();
	titles.add(myunits.get(index));
	return titles;
	//return bubble_sort_title(titles, type);
    }

    public List<Unit> searchtitle(TreeMap<String, Integer> mysums, String type, String letter) {
	List<Unit> myunits = mydao.getunits(mysums, type);
	List<Unit> titles = new ArrayList<Unit>();
	for (int i = 0; i < myunits.size(); i++) {
	    //	    if(myunits.get(i).getTitle().indexOf(letter) == 0) {
	    //		titles.add(myunits.get(i));
	    //}
	}
	return titles;
	//return bubble_sort_title(titles, type);
    }

    public List<Unit> searchtitle(TreeMap<String, Integer> mysums, String type) {
	List<Unit> myunits = mydao.getunits(mysums, type);
	return myunits;
	//return bubble_sort_title(titles, type);
    }

    public List<Unit> searchyear(TreeMap<String, Integer> mysums,String type, String year) {
	List<Unit> units = mydao.getunits(mysums, type);
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
