package roart.common.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

public class ResultItem {

    private List<Object> items = new ArrayList<Object>();
    public ResultItem() {
    }
    public ResultItem(String s) {
	add(s);
    }
    public void add(String s) {
	items.add(s);
    }
    public List<Object> get() {
	return items;
    }
    @JsonIgnore
    public Object[] getarr() {
	Object[] strarr = new Object[items.size()];
	for(int i = 0; i < items.size(); i++) {
	    strarr[i] = items.get(i);
	}
	return strarr;
    }
    public List<Object> getItems() {
        return items;
    }
    public void setItems(List<Object> items) {
        this.items = items;
    }
    
    @Override
    public String toString() {
        return "" + items;
    }
}
