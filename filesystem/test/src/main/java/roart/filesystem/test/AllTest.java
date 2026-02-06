package roart.filesystem.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.junit.jupiter.api.Test;

import roart.common.config.ConfigConstants;
import roart.common.config.NodeConfig;
import roart.common.config.MyXMLConfig;
import roart.common.filesystem.FileSystemFileObjectParam;
import roart.common.filesystem.FileSystemFileObjectResult;
import roart.common.filesystem.FileSystemMyFileResult;
import roart.common.filesystem.FileSystemPathParam;
import roart.common.filesystem.FileSystemPathResult;
import roart.common.filesystem.FileSystemByteResult;
import roart.common.filesystem.FileSystemBooleanResult;
import roart.common.filesystem.FileSystemStringResult;
import roart.common.model.FileObject;
import roart.common.model.Location;
import roart.filesystem.FileSystemOperations;

public class AllTest {

    // load node config
    protected NodeConfig nodeConf = MyXMLConfig.getConfigInstance(getConfigfile());

    // copilot generated test that exercises all FileSystemOperations methods on a provided FileSystemOperations instance. It creates temporary files and directories for testing, and uses an in-process ZooKeeper server for any operations that require ZK interaction (like readFile).
    // needed fixes to build and run, but should be a good starting point for testing any FileSystemOperations implementation.
    
    public void allTest(FileSystemOperations fs) throws Exception {

        // start an in-process ZooKeeper for curator so readFile's zk operations succeed
        try (TestingServer server = new TestingServer(true)) {
            ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
            CuratorFramework curatorClient = CuratorFrameworkFactory.newClient(server.getConnectString(), retryPolicy);
            curatorClient.start();

            // create temporary directory and files for testing
            Path tempDir = Files.createTempDirectory("fs-test-");
            File dir = tempDir.toFile();
            dir.deleteOnExit();

            File subdir = new File(dir, "sub");
            subdir.mkdirs();
            subdir.deleteOnExit();

            File file1 = new File(dir, "file1.txt");
            try (FileOutputStream fos = new FileOutputStream(file1)) {
                fos.write("hello world".getBytes());
            }
            file1.deleteOnExit();

            File file2 = new File(subdir, "file2.txt");
            try (FileOutputStream fos = new FileOutputStream(file2)) {
                fos.write("child file".getBytes());
            }
            file2.deleteOnExit();

            // prepare FileObjects
            Location loc = new Location("localnode", "local");
            FileObject foDir = new FileObject(loc, dir.getAbsolutePath());
            FileObject foFile1 = new FileObject(loc, file1.getAbsolutePath());
            FileObject foFile2 = new FileObject(loc, file2.getAbsolutePath());

            // exists
            FileSystemBooleanResult existsRes = fs.exists(new FileSystemFileObjectParam(foFile1));
            Assertions.assertTrue(existsRes.bool, "file1 should exist");

            FileSystemBooleanResult existsDirRes = fs.exists(new FileSystemFileObjectParam(foDir));
            assertTrue(existsDirRes.bool, "dir should exist");

            // isDirectory
            FileSystemBooleanResult isDir = fs.isDirectory(new FileSystemFileObjectParam(foDir));
            assertTrue(isDir.bool, "should be directory");
            FileSystemBooleanResult isFile = fs.isDirectory(new FileSystemFileObjectParam(foFile1));
            assertFalse(isFile.bool, "should not be directory");

            // getAbsolutePath
            FileSystemPathResult abs = fs.getAbsolutePath(new FileSystemFileObjectParam(foFile1));
            assertEquals(file1.getAbsolutePath(), abs.getPath());

            // getParent
            FileSystemFileObjectResult parentRes = fs.getParent(new FileSystemFileObjectParam(foFile1));
            assertNotNull(parentRes.getFileObject());
            assertEquals(dir.getAbsolutePath(), ((FileObject) parentRes.getFileObject()[0]).object);

            // listFiles on dir
            FileSystemFileObjectResult list = fs.listFiles(new FileSystemFileObjectParam(foDir));
            assertNotNull(list.getFileObject());
            boolean foundFile1 = Arrays.stream(list.getFileObject()).anyMatch(o -> ((FileObject)o).object.equals(file1.getAbsolutePath()));
            assertTrue(foundFile1, "listFiles should include file1");

            // listFilesFull on dir
            FileSystemMyFileResult listFull = fs.listFilesFull(new FileSystemFileObjectParam(foDir));
            assertNotNull(listFull.map);
            // should include file1 and subdir+file2 via listing
            boolean containsFile1Key = listFull.map.keySet().stream().anyMatch(k -> k.equals(file1.getAbsolutePath()));
            assertTrue(containsFile1Key, "listFilesFull map should contain file1");

            // get (returns FileObject[] echo)
            FileSystemFileObjectResult getRes = fs.get(new FileSystemPathParam(foFile1));
            assertNotNull(getRes.getFileObject());
            assertEquals(file1.getAbsolutePath(), ((FileObject)getRes.getFileObject()[0]).object);

            // getWithInputStream for multiple paths
            Set<FileObject> paths = Set.of(foFile1, foFile2 );
            FileSystemPathParam pathParam = new FileSystemPathParam();
            pathParam.paths = paths;
            FileSystemMyFileResult withStream = fs.getWithInputStream(pathParam, true);
            assertNotNull(withStream.map);
            assertTrue(withStream.map.containsKey(file1.getAbsolutePath()));
            assertTrue(withStream.map.containsKey(file2.getAbsolutePath()));

            // getInputStream/getBytes
            FileSystemByteResult bytesRes = fs.getInputStream(new FileSystemFileObjectParam(foFile1));
            assertNotNull(bytesRes.bytes);
            assertEquals("hello world", new String(bytesRes.bytes));

            // getMd5 single file
            Map<String, String> md5OfFile1 = fs.getMd5(new FileSystemFileObjectParam(foFile1)).map;
            assertNotNull(md5OfFile1);

            // getMd5 via param map
            FileSystemFileObjectParam md5Param = new FileSystemFileObjectParam();
            md5Param.fos = Set.of( foFile1, foFile2 );
            FileSystemStringResult md5Map = fs.getMd5(md5Param);
            assertNotNull(md5Map.map);
            assertTrue(md5Map.map.containsKey(file1.getAbsolutePath()));

            // readFile (exercises inmemory send + zk write). This uses curator backed by TestingServer above.
            FileSystemFileObjectParam readParam = new FileSystemFileObjectParam();
            readParam.fos = Set.of( foFile1 );
            // readFile may interact with InmemoryFactory; ensure it doesn't throw
            try {
                fs.readFile(readParam);
            } catch (Exception e) {
                fail("readFile threw an exception: " + e.getMessage());
            }

            // cleanup
            curatorClient.close();
            // delete files and dir
            file2.delete();
            file1.delete();
            subdir.delete();
            dir.delete();
        }
    }

    private String getConfigfile() {
        String myConfigFile = System.getProperty("config");
        if (myConfigFile == null) {
            myConfigFile = ConfigConstants.CONFIGFILE;
        }
        return myConfigFile;
    }

}