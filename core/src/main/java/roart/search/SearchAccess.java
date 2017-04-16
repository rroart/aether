package roart.search;

import roart.model.ResultItem;
import roart.model.SearchDisplay;
import roart.service.ControlService;
import roart.util.EurekaConstants;
import roart.util.EurekaUtil;
import roart.common.searchengine.SearchEngineConstructorParam;
import roart.common.searchengine.SearchEngineConstructorResult;
import roart.common.searchengine.SearchEngineDeleteParam;
import roart.common.searchengine.SearchEngineDeleteResult;
import roart.common.searchengine.SearchEngineIndexParam;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineParam;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.common.searchengine.SearchResult;
import roart.common.searchengine.SearchEngineResult;
import roart.config.MyConfig;
import roart.database.IndexFilesDao;
import roart.dir.Traverse;
import roart.model.FileLocation;
import roart.model.IndexFiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

//@EnableEurekaClient
//@EnableDiscoveryClient
public abstract class SearchAccess {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    //@Autowired
    private DiscoveryClient discoveryClient;

    public abstract String getAppName();
    
    public String constructor() {
        SearchEngineConstructorParam param = new SearchEngineConstructorParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        SearchEngineConstructorResult result = EurekaUtil.sendMe(SearchEngineConstructorResult.class, param, getAppName(), EurekaConstants.CONSTRUCTOR);
        return result.error;
    }
    
    public String destructor() {
        SearchEngineConstructorParam param = new SearchEngineConstructorParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        SearchEngineConstructorResult result = EurekaUtil.sendMe(SearchEngineConstructorResult.class, param, getAppName(), EurekaConstants.DESTRUCTOR);
        return result.error;
    }
    
    public int indexme(String type, String md5, InputStream inputStream, String dbfilename, Metadata metadata, String lang, String content, String classification, IndexFiles index) {
        Metadata md = metadata;
        String[] str = new String[md.names().length];
        int i = 0;
        for (String name : md.names()) {
            String value = md.get(name);
            str[i++] = value;
        }
        SearchEngineIndexParam param = new SearchEngineIndexParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.type = type;
        param.md5 = md5;
        param.dbfilename = dbfilename;
        param.metadata = str;
        param.lang = lang;
        param.content = content;
        param.classification = classification;
        
        SearchEngineIndexResult result = EurekaUtil.sendMe(SearchEngineIndexResult.class, param, getAppName(), EurekaConstants.INDEX);

        if (result == null) {
        	return -1;
        }
        
        if (result.size == -1) {
        	index.setNoindexreason(result.noindexreason);
        }
        
        return result.size;
    }

    public ResultItem[] searchme(String str, String searchtype, SearchDisplay display) {
        SearchEngineSearchParam param = new SearchEngineSearchParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.str = str;
        param.searchtype = searchtype;
        
        SearchEngineSearchResult result = EurekaUtil.sendMe(SearchEngineSearchResult.class, param, getAppName(), EurekaConstants.SEARCH);
    	return getResultItems(display, result);
    }

	private ResultItem[] getResultItems(SearchDisplay display, SearchEngineSearchResult result) {
		SearchResult[] results = result.results;
    	ResultItem[] strarr = new ResultItem[results.length + 1];
    	strarr[0] = IndexFiles.getHeaderSearch(display);
    	try {
    		int i = 1;
    		for (SearchResult res : results) {
    			String md5 = res.md5;
    			IndexFiles indexmd5 = IndexFilesDao.getByMd5(md5);

    			String filename = indexmd5.getFilelocation();
    			log.info(i + ". " + md5 + " : " + filename + " : " + res.score);
    			FileLocation maybeFl = Traverse.getExistingLocalFilelocationMaybe(indexmd5);
    			strarr[i] = IndexFiles.getSearchResultItem(indexmd5, res.lang, res.score, res.highlights, display, res.metadata, ControlService.nodename, maybeFl);
    			i++;
    		}
    	} catch (Exception e) {
    		log.error(roart.util.Constants.EXCEPTION, e);
    	}
    	return strarr;
	}

    public ResultItem[] searchsimilar(String id, String searchtype, SearchDisplay display) {
        SearchEngineSearchParam param = new SearchEngineSearchParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.str = id;
        param.searchtype = searchtype;
        
        SearchEngineSearchResult result = EurekaUtil.sendMe(SearchEngineSearchResult.class, param, getAppName(), EurekaConstants.SEARCHMLT);
       	return getResultItems(display, result);
    }

    /**
     * Delete the entry with given id
     * 
     * @param str md5 id
     */
    
    public void delete(String str) {
        SearchEngineDeleteParam param = new SearchEngineDeleteParam();
        param.nodename = ControlService.nodename;
        param.conf = MyConfig.conf;
        param.delete = str;
        
        SearchEngineDeleteResult result = EurekaUtil.sendMe(SearchEngineDeleteResult.class, param, getAppName(), EurekaConstants.DELETE);

    }
    
}

