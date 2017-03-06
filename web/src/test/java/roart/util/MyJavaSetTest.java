package roart.util;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class MyJavaSetTest {

    @Test
    public void testAdd() {
        MyJavaSet<Integer> s = new MyJavaSet<Integer>();
        s.add(42);
        assertEquals(s.size(), 1);
    }
    
    @Test
    public void testRemove() {
    }
    
    @Test
    public void testGetAll() {
    }

    @Test
    public void testSize() {
        MyJavaSet<Integer> s = new MyJavaSet<Integer>();
        s.add(42);
        assertEquals(s.size(), 1);
    }
    
}

