package roart.filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.IOUtils;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.FileObject;
import roart.service.ControlService;

public abstract class RemoteFileSystemAccess extends FileSystemAccess {

    private static Logger log = LoggerFactory.getLogger(RemoteFileSystemAccess.class);

    public RemoteFileSystemAccess(NodeConfig nodeConf, ControlService controlService) {
        super(nodeConf, controlService);
    }

    // not used
    public String copyFileToTmp(FileObject filename, ControlService controlService){
        int i = filename.object.lastIndexOf("/");
        String fn = "/tmp/hdfs" + filename.object.substring(i + 1);
        log.info("copy to local filenames {} {}",filename, fn);
        FileObject file = new FileSystemDao(nodeConf, controlService).get(filename);
        InputStream in = new FileSystemDao(nodeConf, controlService).getInputStream(file);
        OutputStream out;
        try {
            out = new FileOutputStream(new File(fn));
            IOUtils.copy(in, out);
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            log.error(Constants.EXCEPTION, e);
        } catch (IOException e) {
            log.error(Constants.EXCEPTION, e);
        }
        return fn;
    }

    @Deprecated
    @Override
    public String getLocalFilesystemFile(FileObject filename) {
        String tmpfn = copyFileToTmp(filename, null /*controlService*/);
        String fn = tmpfn;
        return fn;
    }
}
