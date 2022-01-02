package roart.util;

import java.util.Random;

public class MyLocalLockTestUtil {

    private static Random rand = new Random();
    
    public static String getId() {
        String ids = "abcdefghij";
        int idx = rand.nextInt(ids.length());
        return ids.substring(idx, idx + 1);
    }
}
