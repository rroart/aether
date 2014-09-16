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

public class FileLocation {
	private Log log = LogFactory.getLog(this.getClass());

	private String node;
	private String filename;

    public FileLocation(String node, String filename) {
	this.node = node;
	this.filename = filename;
    }

    public FileLocation(String filename) {
	this.filename = filename;
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
	if (node == null) {
	    return filename;
	}
	return node + ":" + filename;
    }

}
