package roart.filesystem;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import roart.config.MyConfig;
import roart.model.FileObject;
import roart.service.ControlService;
import roart.util.EurekaConstants;
import roart.util.EurekaUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.discovery.DiscoveryClient;

public class FileSystemAccess {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private DiscoveryClient discoveryClient;

    public String getAppName() { return null; }

    public String constructor(String url) {
        FileSystemConstructorParam param = new FileSystemConstructorParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        //FileSystemConstructorResult result = EurekaUtil.sendMe(FileSystemConstructorResult.class, param, getAppName(), EurekaConstants.CONSTRUCTOR);
        FileSystemConstructorResult result = EurekaUtil.sendMe(FileSystemConstructorResult.class, url, param, EurekaConstants.CONSTRUCTOR);
        return result.error;
    }

    public String destructor() {
        FileSystemConstructorParam param = new FileSystemConstructorParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        FileSystemConstructorResult result = EurekaUtil.sendMe(FileSystemConstructorResult.class, param, getAppName(), EurekaConstants.DESTRUCTOR);
        return result.error;
    }
    public List<FileObject> listFiles(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.fo = f;
        FileSystemFileObjectResult result = EurekaUtil.sendMe(FileSystemFileObjectResult.class, param, getAppName(), EurekaConstants.LISTFILES);
        return Arrays.asList(result.fileObject);

    }

    public boolean exists(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.fo = f;
        FileSystemBooleanResult result = EurekaUtil.sendMe(FileSystemBooleanResult.class, param, getAppName(), EurekaConstants.EXIST);
        return result.bool;

    }

    public String getAbsolutePath(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.fo = f;
        FileSystemPathResult result = EurekaUtil.sendMe(FileSystemPathResult.class, param, getAppName(), EurekaConstants.GETABSOLUTEPATH);
        return result.path;

    }

    public boolean isDirectory(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.fo = f;
        FileSystemBooleanResult result = EurekaUtil.sendMe(FileSystemBooleanResult.class, param, getAppName(), EurekaConstants.ISDIRECTORY);
        return result.bool;

    }

    public InputStream getInputStream(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.fo = f;
        FileSystemByteResult result = EurekaUtil.sendMe(FileSystemByteResult.class, param, getAppName(), EurekaConstants.GETINPUTSTREAM);
        return new ByteArrayInputStream(result.bytes);

    }

    public FileObject getParent(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.fo = f;
        FileSystemFileObjectResult result = EurekaUtil.sendMe(FileSystemFileObjectResult.class, param, getAppName(), EurekaConstants.GETPARENT);
        return result.fileObject[0];

    }

    public FileObject get(String string) {
        FileSystemPathParam param = new FileSystemPathParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.path = string;
        FileSystemFileObjectResult result = EurekaUtil.sendMe(FileSystemFileObjectResult.class, param, getAppName(), EurekaConstants.GET);
        return result.fileObject[0];

    }

    public String getLocalFilesystemFile(String filename) {
        FileObject file = FileSystemDao.get(filename);  
        String fn = FileSystemDao.getAbsolutePath(file);
        if (fn.charAt(4) == ':') {
            fn = fn.substring(5);
        }
        return fn;
    }
}
