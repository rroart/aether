package roart.common.database;

import java.util.Map;

public class DatabaseMd5Result extends DatabaseResult {
    String[] md5;

    Map<String, String> md5Map;
    
    public String[] getMd5() {
        return md5;
    }

    public void setMd5(String[] md5) {
        this.md5 = md5;
    }

    public Map<String, String> getMd5Map() {
        return md5Map;
    }

    public void setMd5Map(Map<String, String> md5Map) {
        this.md5Map = md5Map;
    }

}
