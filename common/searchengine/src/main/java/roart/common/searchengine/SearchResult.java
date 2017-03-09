package roart.common.searchengine;

import java.util.List;

public class SearchResult {
    public String md5;
    public float score;
    public String lang;
    public String[] highlights;
    public String display;
    public List<String> metadata;
}
