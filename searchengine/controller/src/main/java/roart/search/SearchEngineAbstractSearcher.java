package roart.search;

import roart.common.searchengine.SearchEngineConstructorResult;
import roart.common.searchengine.SearchEngineDeleteParam;
import roart.common.searchengine.SearchEngineDeleteResult;
import roart.common.searchengine.SearchEngineIndexParam;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;

public abstract class SearchEngineAbstractSearcher {
    
    public abstract SearchEngineConstructorResult destroy() throws Exception;
	
    public abstract SearchEngineIndexResult indexme(SearchEngineIndexParam index);
    
    public abstract SearchEngineSearchResult searchme(SearchEngineSearchParam search);
    
    public abstract SearchEngineSearchResult searchmlt(SearchEngineSearchParam search);

    public abstract SearchEngineDeleteResult deleteme(SearchEngineDeleteParam delete);

}
