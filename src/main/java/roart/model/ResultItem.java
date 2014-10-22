package roart.model;

import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultItem {

    private static final Logger log = LoggerFactory.getLogger(ResultItem.class);

    private List<String> items = new ArrayList<String>();
    public ResultItem() {
    }
    public ResultItem(String s) {
	add(s);
    }
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
