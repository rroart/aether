package roart.search;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.searchengine.SearchEngineConstructorParam;
import roart.common.searchengine.SearchEngineConstructorResult;
import roart.common.searchengine.SearchEngineDeleteParam;
import roart.common.searchengine.SearchEngineDeleteResult;
import roart.common.searchengine.SearchEngineIndexParam;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineParam;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.common.inmemory.common.Inmemory;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.util.InmemoryUtil;
import roart.common.util.IOUtil;
import roart.common.util.JsonUtil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
//@EnableAutoConfiguration
@SpringBootApplication
@EnableDiscoveryClient
public abstract class SearchEngineAbstractController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static Map<String, SearchEngineAbstractSearcher> searchMap = new HashMap();

    protected abstract SearchEngineAbstractSearcher createSearcher(String configname, String configid, NodeConfig nodeConf);

    private SearchEngineAbstractSearcher getSearch(SearchEngineParam param) {
        SearchEngineAbstractSearcher search = searchMap.get(param.configid);
        if (search == null) {
            NodeConfig nodeConf = null;
            if (param.conf == null) {
                Inmemory inmemory = InmemoryFactory.get(param.iserver, param.configname, param.iconnection);
                try (InputStream contentStream = inmemory.getInputStream(param.iconf)) {
                    if (InmemoryUtil.validate(param.iconf.getMd5(), contentStream)) {
                        String content = InmemoryUtil.convertWithCharset(IOUtil.toByteArray1G(inmemory.getInputStream(param.iconf)));
                        nodeConf = JsonUtil.convertnostrip(content, NodeConfig.class);
                    }
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
            } else {
                nodeConf = param.conf;
            }
            search = createSearcher(param.configname, null, nodeConf);
            searchMap.put(param.configid, search);
        }
        return search;
    }

    @RequestMapping(value = "/" + EurekaConstants.CONSTRUCTOR,
            method = RequestMethod.POST)
    public SearchEngineConstructorResult processConstructor(@RequestBody SearchEngineConstructorParam param)
            throws Exception {
        String error = null;
        try {
            SearchEngineAbstractSearcher search = getSearch(param);
        } catch (Exception e) {
            log.error(roart.common.constants.Constants.EXCEPTION, e);
            error = e.getMessage();
        }
        SearchEngineConstructorResult result = new SearchEngineConstructorResult();
        result.error = error;
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.DESTRUCTOR,
            method = RequestMethod.POST)
    public SearchEngineConstructorResult processDestructor(@RequestBody SearchEngineConstructorParam param)
            throws Exception {
        SearchEngineAbstractSearcher search = searchMap.remove(param.configid);
        String error = null;
        if (search != null) {
            try {
                search.destroy();
            } catch (Exception e) {
                log.error(roart.common.constants.Constants.EXCEPTION, e);
                error = e.getMessage();
            }
        } else {
            error = "did not exist";
        }
        SearchEngineConstructorResult result = new SearchEngineConstructorResult();
        result.error = error;
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.CLEAR,
            method = RequestMethod.POST)
    public SearchEngineConstructorResult processClear(@RequestBody SearchEngineConstructorParam param)
            throws Exception {
        SearchEngineAbstractSearcher search = getSearch(param);
        String error = null;
        if (search != null) {
            try {
                search.clear(param);
            } catch (Exception e) {
                log.error(roart.common.constants.Constants.EXCEPTION, e);
                error = e.getMessage();
            }
        } else {
            error = "did not exist";
        }
        SearchEngineConstructorResult result = new SearchEngineConstructorResult();
        result.error = error;
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.DROP,
            method = RequestMethod.POST)
    public SearchEngineConstructorResult processDrop(@RequestBody SearchEngineConstructorParam param)
            throws Exception {
        SearchEngineAbstractSearcher search = getSearch(param);
        String error = null;
        if (search != null) {
            try {
                search.drop(param);
            } catch (Exception e) {
                log.error(roart.common.constants.Constants.EXCEPTION, e);
                error = e.getMessage();
            }
        } else {
            error = "did not exist";
        }
        SearchEngineConstructorResult result = new SearchEngineConstructorResult();
        result.error = error;
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.DELETE,
            method = RequestMethod.POST)
    public SearchEngineDeleteResult processDelete(@RequestBody SearchEngineDeleteParam param)
            throws Exception {
        SearchEngineAbstractSearcher search = getSearch(param);
        SearchEngineDeleteResult ret = search.deleteme(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.INDEX,
            method = RequestMethod.POST)
    public SearchEngineIndexResult processIndex(@RequestBody SearchEngineIndexParam param)
            throws Exception {
        SearchEngineAbstractSearcher search = getSearch(param);
        SearchEngineIndexResult ret = search.indexme(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.SEARCH,
            method = RequestMethod.POST)
    public SearchEngineSearchResult processSearch(@RequestBody SearchEngineSearchParam param)
            throws Exception {
        SearchEngineAbstractSearcher search = getSearch(param);
        SearchEngineSearchResult ret = search.searchme(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.SEARCHMLT,
            method = RequestMethod.POST)
    public SearchEngineSearchResult processSearchSimilar(@RequestBody SearchEngineSearchParam param)
            throws Exception {
        SearchEngineAbstractSearcher search = getSearch(param);
        SearchEngineSearchResult ret = search.searchmlt(param);
        return ret;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SearchEngineAbstractController.class, args);
    }

    public abstract String getQueueName();
    
}
