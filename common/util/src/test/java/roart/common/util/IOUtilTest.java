package roart.common.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

public class IOUtilTest {

    @Test
    public void testToByteArray() {
        String s = "hello world";
        ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
        byte[] out = IOUtil.toByteArray(bais, 5);
        assertArrayEquals("hello".getBytes(StandardCharsets.UTF_8), out);
    }

    @Test
    public void testToByteArrayMax() {
        String s = "abc";
        ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
        byte[] out = IOUtil.toByteArrayMax(bais);
        assertEquals(3, out.length);
    }

    @Test
    public void testToByteArray1G() {
        String s = "xyz";
        ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
        byte[] out = IOUtil.toByteArray1G(bais);
        assertEquals(3, out.length);
    }
}
