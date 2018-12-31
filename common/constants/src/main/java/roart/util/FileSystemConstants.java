package roart.util;

public class FileSystemConstants {

    public enum FileSystemType { LOCAL, SWIFT, HDFS };
    
    public static final String LOCAL = "local:";
    public static final String FILE = "file:";
    public static final int FILELEN = 5;
    public static final String HDFS = "hdfs:";
    public static final int HDFSLEN = 5;
    public static final String SWIFT = "swift:";
    public static final int SWIFTLEN = 6;
    public static final String FILESLASH = "file://";
    public static final int FILESLASHLEN = 7;
    public static final String HDFSSLASH = "hdfs://";
    public static final int HDFSSLASHLEN = 7;
    public static final String SWIFTSLASH = "swift://";
    public static final int SWIFTSLASHLEN = 8;
    public static final String DOUBLESLASH = "//";
    public static final int DOUBLESLASHLEN = 2;
}
