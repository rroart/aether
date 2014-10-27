package roart.jpa;

import roart.model.ResultItem;
import roart.model.SearchDisplay;
import roart.model.IndexFiles;

import java.util.List;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneSearchJpa extends SearchJpa {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public int indexme(String type, String md5, InputStream inputStream, String dbfilename, String metadata, String lang, String content, String classification, List<ResultItem> retlist, IndexFiles index) {
	return SearchLucene.indexme(type, md5, inputStream, dbfilename, metadata, lang, content, classification, retlist, index);
    }

    public ResultItem[] searchme(String str, String searchtype, SearchDisplay display) {
	String type = "all";
	int stype = new Integer(searchtype).intValue();
	return SearchLucene.searchme(str, searchtype, display);
    }

    public ResultItem[] searchsimilar(String md5i) {
	return null;
    }
}

