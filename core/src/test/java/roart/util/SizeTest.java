package roart.util;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

public class SizeTest {

    public final int SIZE = 1024 * 1024 * 1024;
    
    @Test
    public void testbig() {
        Exception exception = assertThrows(NegativeArraySizeException.class, () -> big());
    }
    
    public void big() throws IOException {
        byte[] data = new byte[Integer.MAX_VALUE + 1];
        InputStream is = new ByteArrayInputStream(data); 
        byte[] bytes = IOUtils.toByteArray(is);
    }
    
    //@Test
    public void big2() throws IOException {
        byte[] data = new byte[Integer.MAX_VALUE + 1];
        InputStream is = new ByteArrayInputStream(data); 
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, "UTF-8");
        writer.toString();
    }
    
   //@Test
   public void big3() throws FileNotFoundException {
        //byte[] data = new byte[Integer.MAX_VALUE + 1];
        //InputStream is = new ByteArrayInputStream(data); 
        InputStream is = new FileInputStream("/dev/zero");
        String text = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))
                  .lines()
                  .collect(Collectors.joining("\n"));
    }

    @Test
    public void big4() throws IOException {
        byte[] data = new byte[Integer.MAX_VALUE - 10];
        InputStream is = new ByteArrayInputStream(data); 
       String text = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        
    }
    
    @Test
    public void big5() throws IOException {
        byte[] data = new byte[Integer.MAX_VALUE - 10];
        InputStream is = new ByteArrayInputStream(data); 
       String text = new String(data);
        
    }
    //@Test
    public void big6() throws IOException {
        byte[] data = new byte[Integer.MAX_VALUE - 10];
        
    }
    @Test
    public void big7() throws IOException {
        //byte[] data = new byte[Integer.MAX_VALUE - 10];
        //byte[] data = new byte[Integer.MAX_VALUE - 10];
        //InputStream is = new ByteArrayInputStream(data); 
        InputStream is = new FileInputStream("/tmp/core.out");
        //is.
        byte[] i = is.readNBytes(Integer.MAX_VALUE - 8);
        System.out.println(i.length);
    }
    @Test
    public void big8() throws IOException {
        //byte[] data = new byte[Integer.MAX_VALUE - 10];
        //byte[] data = new byte[Integer.MAX_VALUE - 10];
        //InputStream is = new ByteArrayInputStream(data); 
        InputStream is = new FileInputStream("/dev/zero");
        //is.
        byte[] i = is.readNBytes(Integer.MAX_VALUE - 8);
        System.out.println(i.length);
    }
    
    @Test
    public void big9() throws IOException {
        List<byte[]> source = new ArrayList<>();
        source.add(new byte[SIZE]);
        source.add(new byte[SIZE]);
        source.add(new byte[SIZE]);
        source.add(new byte[SIZE]);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for (byte[] line : source) {
          baos.write(line);
        }
        byte[] bytes = baos.toByteArray();
    }
    
    @Test
    public void big10() throws IOException {
        InputStream f = new FileInputStream("/tmp/hbase.log");
        String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex( f );
        System.out.println(md5);
        byte[] s = f.readAllBytes();
        System.out.println(s.length);
        byte[] s2 = f.readAllBytes();
        System.out.println(s2.length);
    }
    @Test
    public void big11() throws IOException {
        byte[] data = new byte[SIZE];
        InputStream is = new ByteArrayInputStream(data); 
        List<byte[]> source = new ArrayList<>();
        while (true) {
            byte[] b = is.readNBytes(1024 * 1024 * 200);
            if (b.length == 0) {
                break;
            }
            System.out.println(b.length);
            source.add(b);
        }        
    }
    @Test
    public void big12() throws IOException {
        byte[] data = new byte[SIZE];
        byte[] data2 = new byte[SIZE];
        byte[] data3 = new byte[SIZE];
        byte[] data4 = new byte[SIZE];
        InputStream is = new ByteArrayInputStream(data); 
        InputStream is2 = new ByteArrayInputStream(data2); 
        InputStream is3 = new ByteArrayInputStream(data3); 
        InputStream is4 = new ByteArrayInputStream(data4); 
        SequenceInputStream sis = new SequenceInputStream(is, is2);
        SequenceInputStream sis2 = new SequenceInputStream(sis, is3);
        SequenceInputStream sis3 = new SequenceInputStream(sis2, is4);
        boolean doRead = true;
        while (doRead) {
            byte[] bytes = sis3.readNBytes(SIZE);
            System.out.println(bytes.length);
            doRead = bytes.length == SIZE;
        }
    }
}
