package roart.common.model;

public class Files {

    private String filename;
    
    private String md5;

    private Integer version;
    
    public Files() {
        // for jackson
    }
    
    public Files(String file, String md5) {
        super();
        this.filename = file;
        this.md5 = md5;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String file) {
        this.filename = file;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
        
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object other) {
        return filename.equals(((Files) other).filename) && md5.equals(((Files) other).md5);
    }
    
    @Override
    public String toString() {
        return filename + ":" + md5;
    }
}
