package roart.common.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MathUtilTest {

    @Test
    public void testRound() {
        double v = MathUtil.round(1.23456, 2);
        assertEquals(1.23, v, 0.000001);
    }

    @Test
    public void testRound2() {
        double v = MathUtil.round2(1.2356, 2);
        assertEquals(1.24, v, 0.000001);
    }

    @Test
    public void testRound3() {
        double v = MathUtil.round3(1.2356, 3);
        assertEquals(1.236, v, 0.000001);
    }

    @Test
    public void testRoundObjectArray() {
        Object[] in = new Object[] {"x", 1.23456, 2.71828};
        Object[] out = MathUtil.round(in, 2);
        Object[] expected = new Object[] {"x", 1.23, 2.72};
        assertArrayEquals(expected, out);
    }

    @Test
    public void testRound2ObjectArray() {
        Object[] in = new Object[] {"x", 1.23456, 2.71828};
        Object[] out = MathUtil.round2(in, 2);
        Object[] expected = new Object[] {"x", 1.23, 2.72};
        assertArrayEquals(expected, out);
    }

    @Test
    public void testRound3ObjectArray() {
        Object[] in = new Object[] {"x", 1.23456, 2.71828};
        Object[] out = MathUtil.round3(in, 3);
        Object[] expected = new Object[] {"x", 1.235, 2.718};
        assertArrayEquals(expected, out);
    }
}
