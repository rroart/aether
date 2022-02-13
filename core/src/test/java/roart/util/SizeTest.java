package roart.util;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.io.IOUtils;
import org.junit.jupiter.api.Test;

public class SizeTest {

    @Test
    public void testbig() {
        Exception exception = assertThrows(NegativeArraySizeException.class, () -> big());
    }
    
    public void big() throws IOException {
        byte[] data = new byte[Integer.MAX_VALUE + 1];
        String string = new String(data);
        InputStream is = new ByteArrayInputStream(data); 
        byte[] bytes = IOUtils.toByteArray(is);
    }
}
