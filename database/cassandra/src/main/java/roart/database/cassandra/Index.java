package roart.database.cassandra;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

import roart.common.model.FileLocation;

@CqlName("indexfiles")
@Entity
public class Index {
    @PartitionKey
    private String md5;
    private Boolean indexed;
    private String timeindex;
    private String timestamp;
    private String timeclass;
    private String convertsw;
    private String converttime;
    private String classification;
    private Integer failed;
    private String failedreason;
    private String timeoutreason;
    private String noindexreason;
    private Set<FileLocation> filelocations;
    //private Set<String> filelocation;
    private String language;
    private String isbn;
    private String created;
    private String checked;
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
    public String getTimeclass() {
        return timeclass;
    }
    public void setTimeclass(String timeclass) {
        this.timeclass = timeclass;
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
    public String getTimeoutreason() {
        return timeoutreason;
    }
    public void setTimeoutreason(String timeoutreason) {
        this.timeoutreason = timeoutreason;
    }
    public String getNoindexreason() {
        return noindexreason;
    }
    public void setNoindexreason(String noindexreason) {
        this.noindexreason = noindexreason;
    }
    public Set<FileLocation> getFilelocations() {
        return filelocations;
    }
    public void setFilelocations(Set<FileLocation> filenames) {
        this.filelocations = (filenames);
    }
    public void setFilelocations(List<FileLocation> filenames) {
        this.filelocations = new HashSet<>(filenames);
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
}
