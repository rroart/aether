package roart.search;

import org.junit.jupiter.api.Test;

import roart.testdata.TestFiles;

public class AnotherIT {

    @Test
    public void myTest() throws Exception {
        LocalFSUtil.mkdir("/tmp/ae");
        LocalFSUtil.mkdir("/tmp/ae/d");
        LocalFSUtil.write("/tmp/ae/f1.txt", TestFiles.file1content);
        LocalFSUtil.write("/tmp/ae/f2.txt", TestFiles.file2content);
        LocalFSUtil.write("/tmp/ae/d/f3.txt", TestFiles.file1content);
        Object object;
        object = new Util(new Sender()).traverse("/tmp/ae");
        System.out.println(object);
        object = new Util(new Sender()).index("/tmp/ae", false);
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
    public void myTest2() throws Exception {
        HDFSUtil.ini();
        HDFSUtil.mkdir("/htmp");
        HDFSUtil.mkdir("/htmp/ae");
        HDFSUtil.mkdir("/htmp/ae/d");
        HDFSUtil.write("/htmp/ae/f1.txt", TestFiles.file1content);
        HDFSUtil.write("/htmp/ae/f2.txt", TestFiles.file2content);
        HDFSUtil.write("/htmp/ae/d/f3.txt", TestFiles.file1content);
        Object object;
        object = new Util(new Sender()).traverse(":hdfs::/htmp/ae");
        System.out.println(object);
        object = new Util(new Sender()).index(":hdfs::/htmp/ae", false);
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
}
