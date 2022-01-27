package roart.common.util;

import org.junit.jupiter.api.Test;

import roart.common.model.FileObject;
import static org.junit.jupiter.api.Assertions.*;

public class FsUtilTest {
    @Test
    public void test1() {
        FileObject i = FsUtil.getFileObject("file:/tmp/here");
        System.out.println(i.toString());
        assertEquals(":::/tmp/here", i.toString());
    }

    @Test
    public void test2() {
        FileObject i = FsUtil.getFileObject(":s3:buck:/xiangqi");
        System.out.println(i.toString());
        assertEquals(":s3:buck:/xiangqi", i.toString());
    }
    
    @Test
    public void test3() {
        FileObject i = FsUtil.getFileObject(":::/tmp/here:txt");
        System.out.println(i.toString());
        assertEquals(":::/tmp/here:txt", i.toString());
    }

    @Test
    public void test4() {
        FileObject i = FsUtil.getFileObject(":s3:buck:/xiangqi:txt");
        System.out.println(i.toString());
        assertEquals(":s3:buck:/xiangqi:txt", i.toString());
    }
}
