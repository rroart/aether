package roart.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LocalFSUtil {

    public static void write(String fileName, String content) throws IOException {
        Files.write(Paths.get(fileName), content.getBytes());
    }

    public static void mkdir(String dir) {
        new File(dir).mkdirs();
    }

    public static void rm(String dir) {
        new File(dir).delete();
    }

}
