package roart.filesystem;

import roart.common.constants.FileSystemConstants.FileSystemType;
import roart.common.util.FsUtil;

@Deprecated
public class FileSystemFactory {
    @Deprecated
    public static FileSystemAccess getFileSystem(String filename) {
        FileSystemType fs = FsUtil.getFileSystemType(filename);
        switch (fs) {
        case LOCAL:
            return new LocalFileSystemAccess();
        case HDFS:
            return new HDFSAccess();
        case SWIFT:
            return new SwiftAccess();
        case S3:
            return new S3Access();
        }
        return null;
    }
}
