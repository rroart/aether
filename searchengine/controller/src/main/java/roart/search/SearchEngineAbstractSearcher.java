package roart.search;

import roart.common.config.NodeConfig;
import roart.common.searchengine.SearchEngineConstructorParam;
import roart.common.searchengine.SearchEngineConstructorResult;
import roart.common.searchengine.SearchEngineDeleteParam;
import roart.common.searchengine.SearchEngineDeleteResult;
import roart.common.searchengine.SearchEngineIndexParam;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;

public abstract class SearchEngineAbstractSearcher {
    
    protected String configname;
    protected String configid;
    protected NodeConfig nodeConf;

    public SearchEngineAbstractSearcher(String configname, String configid, NodeConfig nodeConf) {
        this.configname = configname;
        this.configid = configid;
        this.nodeConf = nodeConf;
    }

    public abstract SearchEngineConstructorResult destroy() throws Exception;
	
    public abstract SearchEngineConstructorResult clear(SearchEngineConstructorParam param) throws Exception;
    
    public abstract SearchEngineConstructorResult drop(SearchEngineConstructorParam param) throws Exception;
    
    public abstract SearchEngineIndexResult indexme(SearchEngineIndexParam index);
    
    public abstract SearchEngineSearchResult searchme(SearchEngineSearchParam search);
    
    public abstract SearchEngineSearchResult searchmlt(SearchEngineSearchParam search);

    public abstract SearchEngineDeleteResult deleteme(SearchEngineDeleteParam delete);

}
