package roart.model;

import java.util.List;
import java.util.ArrayList;

public class ResultItem {
    private List<String> items = new ArrayList<String>();
    public void add(String s) {
	items.add(s);
    }
    public List<String> get() {
	return items;
    }
    public String[] getarr() {
	String[] strarr = new String[items.size()];
	for(int i = 0; i < items.size(); i++) {
	    strarr[i] = items.get(i);
	}
	return strarr;
    }
}
