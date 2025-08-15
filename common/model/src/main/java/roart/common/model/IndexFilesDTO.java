package roart.common.model;

import java.util.Set;

public class IndexFilesDTO {
    private String md5;
    private Boolean indexed;
    private String timeclass;
    private String timeindex;
    private String timestamp;
    private String convertsw;
    private String converttime;
    private String classification;
    private Integer failed;
    private String failedreason;
    private String noindexreason;
    private String timeoutreason;
    private Set<FileLocation> filelocations;
    private String language;
    private String isbn;
    private String created;
    private String checked;
    private Integer size;
    private Integer convertsize;
    private String mimetype;
    private Integer version;

    public IndexFilesDTO() {
    }
    
    public IndexFilesDTO(String md5) {
        this.md5 = md5;
    }
    
    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Boolean getIndexed() {
        return indexed;
    }

    public void setIndexed(Boolean indexed) {
        this.indexed = indexed;
    }

    public String getTimeclass() {
        return timeclass;
    }

    public void setTimeclass(String timeclass) {
        this.timeclass = timeclass;
    }

    public String getTimeindex() {
        return timeindex;
    }

    public void setTimeindex(String timeindex) {
        this.timeindex = timeindex;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getConvertsw() {
        return convertsw;
    }

    public void setConvertsw(String convertsw) {
        this.convertsw = convertsw;
    }

    public String getConverttime() {
        return converttime;
    }

    public void setConverttime(String converttime) {
        this.converttime = converttime;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public String getFailedreason() {
        return failedreason;
    }

    public void setFailedreason(String failedreason) {
        this.failedreason = failedreason;
    }

    public String getNoindexreason() {
        return noindexreason;
    }

    public void setNoindexreason(String noindexreason) {
        this.noindexreason = noindexreason;
    }

    public String getTimeoutreason() {
        return timeoutreason;
    }

    public void setTimeoutreason(String timeoutreason) {
        this.timeoutreason = timeoutreason;
    }

    public Set<FileLocation> getFilelocations() {
        return filelocations;
    }

    public void setFilelocations(Set<FileLocation> filelocations) {
        this.filelocations = filelocations;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getConvertsize() {
        return convertsize;
    }

    public void setConvertsize(Integer convertsize) {
        this.convertsize = convertsize;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
