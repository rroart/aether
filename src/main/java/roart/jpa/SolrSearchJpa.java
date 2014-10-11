package roart.jpa;

import roart.model.ResultItem;
import roart.model.SearchDisplay;
import roart.model.IndexFiles;

import java.util.List;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SolrSearchJpa extends SearchJpa {

    private Log log = LogFactory.getLog(this.getClass());

    public int indexme(String type, String md5, InputStream inputStream, String dbfilename, String metadata, String lang, String content, String classification, List<ResultItem> retlist, IndexFiles index) {
	return SearchSolr.indexme(type, md5, inputStream, dbfilename, metadata, lang, content, classification, retlist, index);
    }

    public ResultItem[] searchme(String str, String searchtype, SearchDisplay display) {
	String type = "all";
	int stype = new Integer(searchtype).intValue();
	return SearchSolr.searchme(str, searchtype, display);
    }

    public ResultItem[] searchsimilar(String md5i) {
	return null;
    }
}

