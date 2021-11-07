package roart.util;

import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import static org.junit.jupiter.api.Assertions.*;

public class ISBNUtilTest {
    /*
     * 978-92-95055-02-5
     * 
     * 0-00-000000-0
     * 0-000-00000-0
     * 0-0000-0000-0
     * 0-00000-000-0
     * 0-000000-00-0
     * 0-0000000-0-0
     */

    String txt = "Bla bla bla bla bla\n"
            + "ISBN: 978-1-449-31943-4\n"
            + "[LSI]\n"
            + "1344629030\n"
            + "bl bl bl bl.";

    @Test
    public void test1() {
        String result = new ISBNUtil().extract(txt, true);
        assertEquals("978-1-449-31943-4", result);
    }

    @Test
    public void test2() {
        String filled = "bl".repeat(4100);
        String result = new ISBNUtil().extract(filled + txt, true);
        assertEquals("978-1-449-31943-4", result);
    }
}
