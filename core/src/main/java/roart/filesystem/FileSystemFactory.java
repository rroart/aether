package roart.filesystem;

import roart.util.FsUtil;
import roart.util.FileSystemConstants.FileSystemType;

public class FileSystemFactory {
    public static FileSystemAccess getFileSystem(String filename) {
        FileSystemType fs = FsUtil.getFileSystemType(filename);
        switch (fs) {
        case LOCAL:
            return new LocalFileSystemAccess();
        case HDFS:
            return new HDFSAccess();
        case SWIFT:
            return new SwiftAccess();
        }
        return null;
    }
}
