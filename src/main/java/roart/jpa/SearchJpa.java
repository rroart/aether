package roart.jpa;

import roart.model.ResultItem;
import roart.model.SearchDisplay;
import roart.model.IndexFiles;

import java.util.List;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SearchJpa {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public abstract int indexme(String type, String md5, InputStream inputStream, String dbfilename, String metadata, String lang, String content, String classification, List<ResultItem> retlist, IndexFiles index);

    public abstract ResultItem[] searchme(String str, String searchtype, SearchDisplay display);

}

