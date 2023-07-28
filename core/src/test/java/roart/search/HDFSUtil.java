package roart.search;

import java.io.IOException;
import java.util.EnumSet;
import org.apache.hadoop.fs.CreateFlag;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Options;
import org.junit.jupiter.api.BeforeAll;

import roart.common.config.NodeConfig;

import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;

import org.apache.hadoop.fs.FSDataOutputStream;

public class HDFSUtil {

    private static Configuration configuration;
    private static FileContext  fc;
    private static NodeConfig nodeConf;
    
    //@BeforeAll
    public static void ini() throws Exception {
        configuration = new Configuration();
        configuration.set("fs.default.name", "hdfs://192.168.0.100/");
        fc = FileContext.getFileContext(configuration);
    }
    
    public static void write(String fileName, String content) throws Exception {
        Path path = new Path(fileName);
        FSDataOutputStream fin = fc.create(path,
                EnumSet.of(CreateFlag.CREATE, CreateFlag.OVERWRITE), new Options.CreateOpts[] {});
        fin.writeUTF(content);
        fin.close();
    }

    public static void mkdir(String dir) throws Exception {
        Path path = new Path(dir);
        fc.mkdir(path, new FsPermission(FsPermission.DEFAULT_UMASK), false);
    }

    public static void rm(String dir) throws Exception {
        Path path = new Path(dir);
        fc.delete(path, false);
    }
}
