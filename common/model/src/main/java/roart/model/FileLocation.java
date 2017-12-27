package roart.model;

import java.util.HashSet;
import java.util.Set;

import roart.util.FileSystemConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileLocation {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	private String node;
	private String filename;
	
	public FileLocation() {
	}
	
    public FileLocation(String mynode, String filename) {
	if (mynode == null || mynode.length() == 0) {
	    log.error("No nodename, no correcting");
	}
	this.node = mynode;
	this.filename = filename;
    }

    public FileLocation(String filename, String csNodename, String dummy) {
    	String file = filename;
    	String prefix = "";
    	// TODO redo this if system. make it oo.
    	if (filename.startsWith(FileSystemConstants.FILESLASH) || filename.startsWith(FileSystemConstants.HDFSSLASH) || filename.startsWith(FileSystemConstants.SWIFTSLASH)) {
    		int split;
    		if (filename.startsWith(FileSystemConstants.SWIFT)) {
    	   		prefix = file.substring(0, FileSystemConstants.SWIFTLEN); // no double slash
        	    file = file.substring(FileSystemConstants.SWIFTSLASHLEN);
        	    split = file.indexOf("/");
        	    this.node = file.substring(0, split);
    		} else {
   		prefix = file.substring(0, FileSystemConstants.FILELEN); // no double slash
    	    file = file.substring(FileSystemConstants.FILESLASHLEN);
    	    split = file.indexOf("/");
    	    this.node = file.substring(0, split);
    		}
	    if (this.node == null || this.node.length() == 0) {
		log.error("No nodename " + filename + " , " + file);
	    }
    	    this.filename = prefix + file.substring(split);
    	} else {
	    this.node = csNodename;
        if (this.node == null || this.node.length() == 0) {
        log.error("No nodename " + filename + " , " + file);
        }
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

    @Override
    public String toString() {
	if (node == null || node.length() == 0) {
	    log.error("No nodename");
	    return filename;
	}
	// TODO make OO version
	if (filename.startsWith(FileSystemConstants.FILE) || filename.startsWith(FileSystemConstants.HDFS) || filename.startsWith(FileSystemConstants.SWIFT)) {
		if (filename.startsWith(FileSystemConstants.SWIFT)) {
			String prefix = filename.substring(0, FileSystemConstants.SWIFTLEN);
			return prefix + "//" + node + filename.substring(FileSystemConstants.SWIFTLEN);				
		} else {
		String prefix = filename.substring(0, FileSystemConstants.FILELEN);
		return prefix + "//" + node + filename.substring(FileSystemConstants.FILELEN);
		}
	} else {
		return FileSystemConstants.FILESLASH + node + filename;
	}
    }

    public String toPrintString() {
	if (node == null || node.length() == 0) {
	    log.error("No nodename");
	    return filename;
	}
	return node + ":" + filename;
    }

    public String getNodeNoLocalhost(String nodename) {
    	String mynode = getNode();
    	if (mynode != null && mynode.equals(nodename)) {
    		return null;
    	}
    return mynode;
}

    public boolean isLocal(String nodename) {
    	if (node == null || node.length() == 0) {
	    log.error("No nodename");
    		return true;
    	}
    	return nodename.equals(node);
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

		public static Set<String> getFilelocationsToString(Set<FileLocation> files) {
			Set<String> set = new HashSet<String>();
			for (FileLocation fl : files) {
				set.add(fl.toString());
			}
			return set;
		}
    
		public static Set<FileLocation> getFilelocations(Set<String> files, String nodename) {
			Set<FileLocation> set = new HashSet<FileLocation>();
			for (String fl : files) {
				set.add(new FileLocation(fl, nodename, null));
			}
			return set;
		}
    
}
