package roart.jpa;

import roart.model.ResultItem;
import roart.model.SearchDisplay;

import java.util.List;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class SearchJpa {

    private Log log = LogFactory.getLog(this.getClass());

    public abstract int indexme(String type, String md5, InputStream inputStream, String dbfilename, String metadata, String lang, String content, String classification, List<ResultItem> retlist);

    public abstract void indexme(String type);

    public abstract ResultItem[] searchme(String type, String str);

    public abstract ResultItem[] searchme2(String str, String searchtype, SearchDisplay display);

}

