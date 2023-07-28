package roart.filesystem;

import roart.common.config.NodeConfig;
import roart.common.constants.FileSystemConstants.FileSystemType;
import roart.common.util.FsUtil;

@Deprecated
public class FileSystemFactory {
    @Deprecated
    public static FileSystemAccess getFileSystem(String filename, NodeConfig nodeConf) {
        FileSystemType fs = FsUtil.getFileSystemType(filename);
        switch (fs) {
        case LOCAL:
            return new LocalFileSystemAccess(nodeConf);
        case HDFS:
            return new HDFSAccess(nodeConf);
        case SWIFT:
            return new SwiftAccess(nodeConf);
        case S3:
            return new S3Access(nodeConf);
        }
        return null;
    }
}
