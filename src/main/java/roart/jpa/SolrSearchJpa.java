package roart.jpa;

import roart.model.ResultItem;

import java.util.List;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SolrSearchJpa extends SearchJpa {

    private Log log = LogFactory.getLog(this.getClass());

    public int indexme(String type, String md5, InputStream inputStream, String dbfilename, String metadata, String lang, String content, String classification, List<ResultItem> retlist) {
	return SearchSolr.indexme(type, md5, inputStream, dbfilename, metadata, lang, content, classification, retlist);
    }

    public void indexme(String type) {
	SearchSolr.indexme(type);
    }

    public ResultItem[] searchme(String type, String str) {
	return SearchSolr.searchme(type, str);
    }

    public ResultItem[] searchme2(String str, String searchtype) {
	String type = "all";
	int stype = new Integer(searchtype).intValue();
	return SearchSolr.searchme2(str, searchtype);
    }

    public ResultItem[] searchsimilar(String md5i) {
	return null;
    }
}

