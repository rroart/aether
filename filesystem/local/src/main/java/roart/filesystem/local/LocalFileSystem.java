package roart.filesystem.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.FileSystemConstants;
import roart.common.filesystem.FileSystemBooleanResult;
import roart.common.filesystem.FileSystemByteResult;
import roart.common.filesystem.FileSystemConstructorResult;
import roart.common.filesystem.FileSystemFileObjectParam;
import roart.common.filesystem.FileSystemFileObjectResult;
import roart.common.filesystem.FileSystemMyFileResult;
import roart.common.filesystem.FileSystemPathParam;
import roart.common.filesystem.FileSystemPathResult;
import roart.common.filesystem.MyFile;
import roart.common.model.FileObject;
import roart.filesystem.FileSystemOperations;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalFileSystem extends FileSystemOperations {

    private static final Logger log = LoggerFactory.getLogger(LocalFileSystem.class);

    public LocalFileSystem(String nodename, NodeConfig nodeConf) {
    }

    @Override
    public FileSystemFileObjectResult listFiles(FileSystemFileObjectParam param) {
        FileObject f = param.fo;
        List<FileObject> foList = new ArrayList<>();
        File dir = objectToFile(f);
        File[] listDir = dir.listFiles();
        if (listDir != null) {
            for (File file : listDir) {
                FileObject fo = new FileObject(file, this.getClass().getSimpleName());
                foList.add(fo);
            }
        }     
        FileSystemFileObjectResult result = new FileSystemFileObjectResult();
        result.setFileObject(foList.stream().toArray(FileObject[]::new));
        return result;
    }

    @Override
    public FileSystemMyFileResult listFilesFull(FileSystemFileObjectParam param) throws Exception {
        FileObject f = param.fo;
        Map<String, MyFile> map = new HashMap<>();
        //List<FileObject> foList = new ArrayList<>();
        File dir = objectToFile(f);
        File[] listDir = dir.listFiles();
        if (listDir != null) {
            for (File file : listDir) {
                FileObject[] fo = new FileObject[1];
                fo[0] = new FileObject(file, this.getClass().getSimpleName());
                MyFile my = getMyFile(fo, false);
                map.put(my.absolutePath, my);
            }
        }     
        FileSystemMyFileResult result = new FileSystemMyFileResult();
        result.map = map;
        return result;
    }

    @Override
    public FileSystemBooleanResult exists(FileSystemFileObjectParam param) {
        FileObject f = param.fo;
        FileSystemBooleanResult result = new FileSystemBooleanResult();
        result.bool = objectToFile(f).exists();
        return result;
    }

    @Override
    public FileSystemPathResult getAbsolutePath(FileSystemFileObjectParam param) {
        FileObject f = param.fo;
        FileSystemPathResult result = new FileSystemPathResult();
        result.setPath(FileSystemConstants.FILE + objectToFile(f).getAbsolutePath());
        return result;
    }

    @Override
    public FileSystemBooleanResult isDirectory(FileSystemFileObjectParam param) {
        FileObject f = param.fo;
        FileSystemBooleanResult result = new FileSystemBooleanResult();
        result.bool = objectToFile(f).isDirectory();
        return result;
    }

    @Override
    public FileSystemByteResult getInputStream(FileSystemFileObjectParam param) throws Exception {
        FileSystemByteResult result = new FileSystemByteResult();
        result.bytes = getInputStreamInner(param.fo);
        return result;
    }

    private byte[] getInputStreamInner(FileObject f) throws IOException {
        byte[] bytes;
        try {
            InputStream is = new FileInputStream( objectToFile(f) /*new File(getAbsolutePath(f))*/);
            bytes  = IOUtils.toByteArray(is);
        } catch (FileNotFoundException e) {
            log.error(Constants.EXCEPTION, e);
            return null;
        }
        return bytes;
    }

    @Override
    public FileSystemMyFileResult getWithInputStream(FileSystemPathParam param) throws Exception {
        Map<String, MyFile> map = new HashMap<>();
        for (String filename : param.paths) {
            FileObject[] fo = getInner(filename);
            MyFile my = getMyFile(fo, true);
            map.put(filename, my);
        }
        FileSystemMyFileResult result = new FileSystemMyFileResult();
        result.map = map;
        return result;
    }

    private MyFile getMyFile(FileObject[] fo, boolean withBytes) throws IOException {
        MyFile my = new MyFile();
        my.fileObject = fo;
        if (fo[0] != null) {
            my.exists = objectToFile(fo[0]).exists();
            if (my.exists) {
                my.isDirectory = objectToFile(fo[0]).isDirectory();
                my.absolutePath = FileSystemConstants.FILE + objectToFile(fo[0]).getAbsolutePath();
                if (withBytes) {
                    my.bytes = getInputStreamInner(fo[0]);
                }
            }
        }
        return my;
    }

    @Override
    public FileSystemFileObjectResult get(FileSystemPathParam param) {
        FileObject[] fo = getInner(param.path);
        FileSystemFileObjectResult result = new FileSystemFileObjectResult();
        result.setFileObject(fo);
        return result;
    }

    private FileObject[] getInner(String string) {
        if (string.startsWith(FileSystemConstants.FILE)) {
            string = string.substring(5);
        }
        FileObject[] fo = new FileObject[1];
        fo[0] = new FileObject(string, this.getClass().getSimpleName());
        return fo;
    }

    @Override
    public FileSystemFileObjectResult getParent(FileSystemFileObjectParam param) {
        FileObject f = param.fo;
        String parent = objectToFile(f).getParent();
        File file = new File(parent);
        FileSystemFileObjectResult result = new FileSystemFileObjectResult();
        FileObject[] fo = new FileObject[1];
        fo[0] = new FileObject(file, this.getClass().getSimpleName());
        result.setFileObject(fo);
        return result;
    }

    @Override
    public FileSystemConstructorResult destroy() {
        return null;
    }

    private File objectToFile(FileObject fo) {
        File result = null;
        if (fo.object instanceof File) {
            result = (File) fo.object;
        }
        if (fo.object instanceof String) {
            result = new File((String) fo.object);
        }
        return result;
    }

}
