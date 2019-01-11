package roart.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.constants.FileSystemConstants;
import roart.common.constants.FileSystemConstants.FileSystemType;

public class FsUtil {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public static boolean isRemote(String filename) {
        return filename.startsWith(FileSystemConstants.HDFS) || filename.startsWith(FileSystemConstants.SWIFT);
    }
      
    public static FileSystemType getFileSystemType(String filename) {
        System.out.println("FN "+ filename);
        if (filename.indexOf(':') < 0) {
           return FileSystemType.LOCAL; 
        }
        if (filename.startsWith(FileSystemConstants.LOCAL)) {
            return FileSystemType.LOCAL;
         }
        if (filename.startsWith(FileSystemConstants.FILE)) {
           return FileSystemType.LOCAL;
        }
        if (filename.startsWith(FileSystemConstants.HDFS)) {
           return FileSystemType.HDFS;
        }
        if (filename.startsWith(FileSystemConstants.SWIFT)) {
            return FileSystemType.SWIFT;
        }
        return null;
    }
    
    public static FileSystemType getFilenameType(String filename) {
        System.out.println("FN "+ filename);
        if (filename.indexOf(':') < 0) {
           //return FileSystemType.LOCAL; 
        }
        if (filename.startsWith(FileSystemConstants.FILESLASH)) {
           return FileSystemType.LOCAL;
        }
        if (filename.startsWith(FileSystemConstants.HDFSSLASH)) {
           return FileSystemType.HDFS;
        }
        if (filename.startsWith(FileSystemConstants.SWIFTSLASH)) {
            return FileSystemType.SWIFT;
        }
        return null;
    }
    
    public static String getFsPath(String filesystem) {
        String path = filesystem;
        int index = filesystem.indexOf(':');
        if (index >= 0) {
            path = path.substring(index + 1);
        }
        return path;
    }
    /*
    public void decide() {
        String filename = null;
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
    }
    */
}
