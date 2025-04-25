package roart.filesystem;

import roart.common.config.NodeConfig;
import roart.common.constants.FileSystemConstants.FileSystemType;
import roart.common.util.FsUtil;
import roart.service.ControlService;

@Deprecated
public class FileSystemFactory {
    @Deprecated
    public static FileSystemDS getFileSystem(String filename, NodeConfig nodeConf, ControlService controlService) {
        FileSystemType fs = FsUtil.getFileSystemType(filename);
        switch (fs) {
        case LOCAL:
            return new LocalFileSystemDS(nodeConf, controlService);
        case HDFS:
            return new HDFSDS(nodeConf, controlService);
        case SWIFT:
            return new SwiftDS(nodeConf, controlService);
        case S3:
            return new S3DS(nodeConf, controlService);
        }
        return null;
    }
}
