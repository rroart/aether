package roart.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import roart.service.ControlService;

public class FileLocation {
	private Log log = LogFactory.getLog(this.getClass());

	private String node;
	private String filename;

    public FileLocation(String node, String filename) {
	if (node == null) {
	    node = "localhost";
	}
	this.node = node;
	this.filename = filename;
    }

    public FileLocation(String filename) {
    	String file = filename;
    	if (filename.startsWith("file://")) {
    	    file = file.substring(7);
    	    int split = file.indexOf("/");
    	    this.node = file.substring(0, split);
    	    this.filename = file.substring(split);
    	} else {
	    this.node = "localhost";
	    this.filename = filename;
	}
    }

        public String getNode() {
	    return node;
	}

	public void setNode(String node) {
	    this.node = node;
	}

        public String getFilename() {
	    return filename;
	}

	public void setFilename(String filename) {
	    this.filename = filename;
	}

    public String toString() {
	if (node == null || node.length() == 0) {
	    return filename;
	}
	return "file://" + node + filename;
    }

    public String toPrintString() {
	if (node == null) {
	    return filename;
	}
	return node + ":" + filename;
    }

    public String getNodeNoLocalhost() {
    	String node = getNode();
    	if (node != null && node.equals("localhost")) {
    		return null;
    	}
    return node;
}

    public boolean isLocal() {
    	if (node == null) {
    		return true;
    	}
    	if (node.equals("localhost")) {
    		return true;
    	}
    	return ControlService.nodename.equals(node);
    }

        @Override
        public int hashCode() {
	    String str = toString();
	    if (str == null) {
		return 0;
	    }
	    return str.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
	    if (this == obj) {
		return true;
	    }
	    String str = toString();
	    if (str == null) {
		return false;
	    }
	    return str.equals(obj.toString());
	}
    
}
