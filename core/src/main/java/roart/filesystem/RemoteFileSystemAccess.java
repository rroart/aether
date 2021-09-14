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

import roart.common.constants.Constants;
import roart.common.model.FileObject;
import roart.content.TikaHandler;

public class RemoteFileSystemAccess extends FileSystemAccess {

    private static Logger log = LoggerFactory.getLogger(RemoteFileSystemAccess.class);

    public static String copyFileToTmp(String filename){
        int i = filename.lastIndexOf("/");
        String fn = "/tmp/hdfs" + filename.substring(i + 1);
        log.info("copy to local filenames {} {}",filename, fn);
        FileObject file = FileSystemDao.get(filename);
        InputStream in = FileSystemDao.getInputStream(file);
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

    @Override
    public String getLocalFilesystemFile(String filename) {
        String tmpfn = copyFileToTmp(filename);
        String fn = tmpfn;
        return fn;
    }
}
