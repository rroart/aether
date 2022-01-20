package roart.common.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class IOUtil {
    public static byte[] toByteArray(InputStream is) throws IOException {
        return IOUtils.toByteArray(is, Integer.MAX_VALUE);
    }
}
