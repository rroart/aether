package roart.database;

import java.util.Set;

public class DatabaseMd5Param extends DatabaseParam {
    private String md5;
    
    private Set<String> md5s;

    public DatabaseMd5Param() {
        super();
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Set<String> getMd5s() {
        return md5s;
    }

    public void setMd5s(Set<String> md5s) {
        this.md5s = md5s;
    }
    
}
