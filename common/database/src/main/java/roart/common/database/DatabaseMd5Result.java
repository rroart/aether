package roart.common.database;

public class DatabaseMd5Result extends DatabaseResult {
    public String[] getMd5() {
        return md5;
    }

    public void setMd5(String[] md5) {
        this.md5 = md5;
    }

    String[] md5;
}
