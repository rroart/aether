package roart.filesystem;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import roart.common.config.MyConfig;
import roart.common.constants.EurekaConstants;
import roart.common.filesystem.FileSystemBooleanResult;
import roart.common.filesystem.FileSystemByteResult;
import roart.common.filesystem.FileSystemConstructorParam;
import roart.common.filesystem.FileSystemConstructorResult;
import roart.common.filesystem.FileSystemFileObjectParam;
import roart.common.filesystem.FileSystemFileObjectResult;
import roart.common.filesystem.MyFile;
import roart.common.filesystem.FileSystemMyFileResult;
import roart.common.filesystem.FileSystemPathParam;
import roart.common.filesystem.FileSystemPathResult;
import roart.common.model.FileObject;
import roart.eureka.util.EurekaUtil;
import roart.service.ControlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemAccess {

    private String url;
    
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public String getAppName() { return null; }

    public String constructor(String url) {
        this.url = url;
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
        FileSystemConstructorResult result = EurekaUtil.sendMe(FileSystemConstructorResult.class, url, param, EurekaConstants.DESTRUCTOR);
        return result.error;
    }
    public List<FileObject> listFiles(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.fo = f;
        FileSystemFileObjectResult result = EurekaUtil.sendMe(FileSystemFileObjectResult.class, url, param, EurekaConstants.LISTFILES);
        return Arrays.asList(result.getFileObject());

    }

    public List<MyFile> listFilesFull(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.fo = f;
        FileSystemMyFileResult result = EurekaUtil.sendMe(FileSystemMyFileResult.class, url, param, EurekaConstants.LISTFILESFULL);
        return new ArrayList<>(result.map.values());

    }

    public boolean exists(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.fo = f;
        FileSystemBooleanResult result = EurekaUtil.sendMe(FileSystemBooleanResult.class, url, param, EurekaConstants.EXIST);
        return result.bool;

    }

    public String getAbsolutePath(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.fo = f;
        FileSystemPathResult result = EurekaUtil.sendMe(FileSystemPathResult.class, url, param, EurekaConstants.GETABSOLUTEPATH);
        return result.getPath();

    }

    public boolean isDirectory(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.fo = f;
        FileSystemBooleanResult result = EurekaUtil.sendMe(FileSystemBooleanResult.class, url, param, EurekaConstants.ISDIRECTORY);
        return result.bool;

    }

    public InputStream getInputStream(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.fo = f;
        FileSystemByteResult result = EurekaUtil.sendMe(FileSystemByteResult.class, url, param, EurekaConstants.GETINPUTSTREAM);
        return new ByteArrayInputStream(result.bytes);

    }

    public Map<String, MyFile> getWithInputStream(Set<String> filenames) {
        FileSystemPathParam param = new FileSystemPathParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.paths = filenames;
        FileSystemMyFileResult result = EurekaUtil.sendMe(FileSystemMyFileResult.class, url, param, EurekaConstants.GETWITHINPUTSTREAM);
        return result.map;

    }

    public FileObject getParent(FileObject f) {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.fo = f;
        FileSystemFileObjectResult result = EurekaUtil.sendMe(FileSystemFileObjectResult.class, url, param, EurekaConstants.GETPARENT);
        return result.getFileObject()[0];

    }

    public FileObject get(String string) {
        FileSystemPathParam param = new FileSystemPathParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.path = string;
        FileSystemFileObjectResult result = EurekaUtil.sendMe(FileSystemFileObjectResult.class, url, param, EurekaConstants.GET);
        return result.getFileObject()[0];

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
