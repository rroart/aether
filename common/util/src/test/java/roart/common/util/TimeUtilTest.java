package roart.common.util;

import org.junit.jupiter.api.Test;

public class TimeUtilTest {

    @Test
    public void testSleepZero() {
        // should return immediately
        TimeUtil.sleep(0);
    }
}
