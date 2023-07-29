package roart.filesystem;

import roart.common.config.NodeConfig;
import roart.common.constants.FileSystemConstants.FileSystemType;
import roart.common.util.FsUtil;
import roart.service.ControlService;

@Deprecated
public class FileSystemFactory {
    @Deprecated
    public static FileSystemAccess getFileSystem(String filename, NodeConfig nodeConf, ControlService controlService) {
        FileSystemType fs = FsUtil.getFileSystemType(filename);
        switch (fs) {
        case LOCAL:
            return new LocalFileSystemAccess(nodeConf, controlService);
        case HDFS:
            return new HDFSAccess(nodeConf, controlService);
        case SWIFT:
            return new SwiftAccess(nodeConf, controlService);
        case S3:
            return new S3Access(nodeConf, controlService);
        }
        return null;
    }
}
