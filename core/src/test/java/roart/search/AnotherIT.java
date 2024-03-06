package roart.search;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.hadoop.yarn.security.client.TimelineAuthenticationConsts;
import org.junit.jupiter.api.Test;

import com.hazelcast.shaded.org.apache.calcite.avatica.util.TimeUnit;
import com.hazelcast.shaded.org.checkerframework.checker.units.qual.Time;

import roart.testdata.TestFiles;

public class AnotherIT {

    @Test
    public void ordinaryTest() throws Exception {
        LocalFSUtil.rmdir("/tmp/ae");
        LocalFSUtil.mkdir("/tmp/ae");
        LocalFSUtil.mkdir("/tmp/ae/d");
        LocalFSUtil.write("/tmp/ae/f0.pdf", "");
        LocalFSUtil.write("/tmp/ae/f1.txt", TestFiles.file1content);
        LocalFSUtil.write("/tmp/ae/f2.txt", TestFiles.file2content);
        LocalFSUtil.write("/tmp/ae/d/f3.txt", TestFiles.file1content);
        Object object;
        object = new Util(new Sender()).dbclear(null);
        System.out.println(object);
        object = new Util(new Sender()).traverse("/tmp/ae", null);
        System.out.println(object);
        object = new Util(new Sender()).index("/tmp/ae", false, null, null, null, null);
        System.out.println(object);
        LocalFSUtil.rm("/tmp/ae/f1.txt");
        object = new Util(new Sender()).consistentclean(true);
        System.out.println(object);
        object = new Util(new Sender()).dbcheck(null);
        System.out.println(object);

        LocalFSUtil.rm("/tmp/ae/d/f3.txt");
        object = new Util(new Sender()).consistentclean(true);
        System.out.println(object);
        object = new Util(new Sender()).dbcheck(null);
        System.out.println(object);
    }
    
    @Test
    public void hdfsTest() throws Exception {
        HDFSUtil.ini();
        HDFSUtil.mkdir("/htmp");
        HDFSUtil.mkdir("/htmp/ae");
        HDFSUtil.mkdir("/htmp/ae/d");
        HDFSUtil.write("/htmp/ae/f1.txt", TestFiles.file1content);
        HDFSUtil.write("/htmp/ae/f2.txt", TestFiles.file2content);
        HDFSUtil.write("/htmp/ae/d/f3.txt", TestFiles.file1content);
        Object object;
        object = new Util(new Sender()).dbclear(null);
        System.out.println(object);
        object = new Util(new Sender()).traverse(":hdfs::/htmp/ae", null);
        System.out.println(object);
        object = new Util(new Sender()).index(":hdfs::/htmp/ae", false, null, null, null, null);
        System.out.println(object);
        HDFSUtil.rm("/htmp/ae/f1.txt");
        object = new Util(new Sender()).consistentclean(true);
        System.out.println(object);
        object = new Util(new Sender()).dbcheck(null);
        System.out.println(object);

        HDFSUtil.rm("/htmp/ae/d/f3.txt");
        object = new Util(new Sender()).consistentclean(true);
        System.out.println(object);
        object = new Util(new Sender()).dbcheck(null);
        System.out.println(object);
    }
    
    @Test
    public void traverseAndIndexTest() throws Exception {
        LocalFSUtil.rmdir("/tmp/ae");
        LocalFSUtil.mkdir("/tmp/ae");
        LocalFSUtil.mkdir("/tmp/ae/d");
        LocalFSUtil.write("/tmp/ae/f1.txt", TestFiles.file1content);
        LocalFSUtil.write("/tmp/ae/f2.txt", TestFiles.file2content);
        LocalFSUtil.write("/tmp/ae/d/f3.txt", TestFiles.file1content);
        Object object;
        object = new Util(new Sender()).dbclear(null);
        System.out.println(object);
        object = new Util(new Sender()).filesystemlucenenew("/tmp/ae", false, null);
        System.out.println(object);
        LocalFSUtil.write("/tmp/ae/d/f4.txt", TestFiles.file3content);
        object = new Util(new Sender()).filesystemlucenenew("/tmp/ae", false, null);
        System.out.println(object);
        object = new Util(new Sender()).dbcheck(null);
        System.out.println(object);

        LocalFSUtil.rm("/tmp/ae/d/f3.txt");
        object = new Util(new Sender()).consistentclean(true);
        System.out.println(object);
        object = new Util(new Sender()).dbcheck(null);
        System.out.println(object);
    }
    
    @Test
    public void moreDupTest() throws Exception {
        LocalFSUtil.rmdir("/tmp/ae");
        LocalFSUtil.mkdir("/tmp/ae");
        LocalFSUtil.mkdir("/tmp/ae/d");
        LocalFSUtil.write("/tmp/ae/f1.txt", TestFiles.file1content);
        LocalFSUtil.write("/tmp/ae/f2.txt", TestFiles.file1content);
        LocalFSUtil.write("/tmp/ae/f3.txt", TestFiles.file1content);
        LocalFSUtil.write("/tmp/ae/f4.txt", TestFiles.file1content);
        LocalFSUtil.write("/tmp/ae/f5.txt", TestFiles.file1content);
        LocalFSUtil.write("/tmp/ae/f6.txt", TestFiles.file2content);
        LocalFSUtil.write("/tmp/ae/d/f1.txt", TestFiles.file1content);
        LocalFSUtil.write("/tmp/ae/d/f2.txt", TestFiles.file1content);
        LocalFSUtil.write("/tmp/ae/d/f3.txt", TestFiles.file1content);
        LocalFSUtil.write("/tmp/ae/d/f4.txt", TestFiles.file1content);
        LocalFSUtil.write("/tmp/ae/d/f5.txt", TestFiles.file1content);
        LocalFSUtil.write("/tmp/ae/d/f6.txt", TestFiles.file1content);
        Object object;
        object = new Util(new Sender()).dbclear(null);
        System.out.println(object);
        object = new Util(new Sender()).traverse("/tmp/ae", null);
        System.out.println(object);
        object = new Util(new Sender()).index("/tmp/ae", false, null, null, null, null);
        System.out.println(object);
        LocalFSUtil.rm("/tmp/ae/f1.txt");
        object = new Util(new Sender()).consistentclean(true);
        System.out.println(object);
        object = new Util(new Sender()).dbcheck(null);
        System.out.println(object);

        LocalFSUtil.rm("/tmp/ae/d/f3.txt");
        object = new Util(new Sender()).consistentclean(true);
        System.out.println(object);
        object = new Util(new Sender()).dbcheck(null);
        System.out.println(object);
    }
    
    @Test
    public void testMd5Changed() throws Exception {
        LocalFSUtil.rmdir("/tmp/ae");
        LocalFSUtil.mkdir("/tmp/ae");
        LocalFSUtil.mkdir("/tmp/ae/d");
        LocalFSUtil.write("/tmp/ae/f1.txt", TestFiles.file1content);
        LocalFSUtil.write("/tmp/ae/f2.txt", TestFiles.file2content);
        LocalFSUtil.write("/tmp/ae/d/f3.txt", TestFiles.file1content);
        Object object;
        object = new Util(new Sender()).dbclear(null);
        System.out.println(object);
        object = new Util(new Sender()).traverse("/tmp/ae", null);
        System.out.println(object);
        object = new Util(new Sender()).index("/tmp/ae", false, null, null, null, null);
        System.out.println(object);
        LocalFSUtil.rm("/tmp/ae/f1.txt");
        LocalFSUtil.write("/tmp/ae/f1.txt", TestFiles.file2content);
        // filesystemlucenenew
        object = new Util(new Sender()).filesystemlucenenew("/tmp/ae", true, null);
        System.out.println(object);
        object = new Util(new Sender()).consistentclean(true);
        System.out.println(object);
        object = new Util(new Sender()).dbcheck(null);
        System.out.println(object);
    }
    
    @Test
    public void testMd5ChangedEmpty() throws Exception {
        LocalFSUtil.rmdir("/tmp/ae");
        LocalFSUtil.mkdir("/tmp/ae");
        LocalFSUtil.mkdir("/tmp/ae/d");
        LocalFSUtil.write("/tmp/ae/f1.txt", TestFiles.file1content);
        LocalFSUtil.write("/tmp/ae/f2.txt", TestFiles.file2content);
        LocalFSUtil.write("/tmp/ae/d/f3.txt", TestFiles.file1content);
        Object object;
        object = new Util(new Sender()).dbclear(null);
        System.out.println(object);
        object = new Util(new Sender()).traverse("/tmp/ae", null);
        System.out.println(object);
        object = new Util(new Sender()).index("/tmp/ae", false, null, null, null, null);
        System.out.println(object);
        LocalFSUtil.rm("/tmp/ae/f2.txt");
        LocalFSUtil.write("/tmp/ae/f2.txt", TestFiles.file1content);
        // filesystemlucenenew
        object = new Util(new Sender()).filesystemlucenenew("/tmp/ae", true, null);
        System.out.println(object);
        object = new Util(new Sender()).dbcheck(null);
        System.out.println(object);
    }
    
    @Test
    public void concurrencyTest() throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(10);
        LocalFSUtil.rmdir("/tmp/ae");
        LocalFSUtil.mkdir("/tmp/ae");
        LocalFSUtil.mkdir("/tmp/ae/d");
        LocalFSUtil.write("/tmp/ae/f1.txt", TestFiles.file1content);
        LocalFSUtil.write("/tmp/ae/f2.txt", TestFiles.file2content);
        LocalFSUtil.write("/tmp/ae/d/f3.txt", TestFiles.file1content);
        Object object;
        object = new Util(new Sender()).dbclear(null);
        System.out.println(object);
        for (int i = 0; i < 3; i++) {
            service.submit(() -> {
                try {
                    Object object2 = new Util(new Sender()).traverse("/tmp/ae", null);
                    System.out.println(object2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        object = new Util(new Sender()).index("/tmp/ae", false, null, null, null, null);
        System.out.println(object);
        LocalFSUtil.rm("/tmp/ae/f1.txt");
        object = new Util(new Sender()).consistentclean(true);
        System.out.println(object);
        object = new Util(new Sender()).dbcheck(null);
        System.out.println(object);

        LocalFSUtil.rm("/tmp/ae/d/f3.txt");
        object = new Util(new Sender()).consistentclean(true);
        System.out.println(object);
        object = new Util(new Sender()).dbcheck(null);
        System.out.println(object);
    }
    
}
