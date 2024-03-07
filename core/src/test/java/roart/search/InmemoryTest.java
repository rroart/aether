package roart.search;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import roart.common.util.JsonUtil;

public class InmemoryTest {

    @Test
    public void test() {
        assertEquals(3, send(string(21)));
        assertEquals(2, send(string(20)));
        assertEquals(2, send(string(11)));
        assertEquals(1, send(string(10)));
        assertEquals(1, send(string(1)));
        assertEquals(1, send(string(0)));
    }
    
    private String string(int n) {
        return " ".repeat(n);
    }
    
    public int send(Object data) {
        InputStream inputStream;
        if (data instanceof InputStream) {
            inputStream = (InputStream) data;
        } else {
            inputStream = getInputStream(data);
        }
        try {
            int limit = getLimit();
            int count = 0;
            boolean doRead = true;
            while (doRead) {
                byte[] bytes = inputStream.readNBytes(limit);
                if (bytes.length > 0) {
                    System.out.println(bytes.length);
                }
                doRead = bytes.length == limit;
                if (bytes.length > 0) {
                    count++;
                }
            }
            if (count == 0) {
                count = 1;
            }
            inputStream.close();
            System.out.println(count);
            System.out.println("end");
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    private int getLimit() {
        return 10;
    }

    private InputStream getInputStream(Object data) {
        String string;
        if (data instanceof String) {
            string = (String) data;
        } else {
            ObjectMapper mapper = new ObjectMapper();
            string = JsonUtil.convert(data, mapper);
        }
        if (string == null) {
            string = "";
        }
        return new ByteArrayInputStream(string.getBytes());
    }

}
