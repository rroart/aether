package roart.common.config;

public class Converter {
    private String name;
    
    private Integer timeout;
    
    private String[] mimetypes;
    
    private String[] suffixes;
    
    public Converter() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String[] getMimetypes() {
        return mimetypes;
    }

    public void setMimetypes(String[] mimetypes) {
        this.mimetypes = mimetypes;
    }

    public String[] getSuffixes() {
        return suffixes;
    }

    public void setSuffixes(String[] suffixes) {
        this.suffixes = suffixes;
    }
    
    
}
