package roart.search;

import java.util.HashMap;
import java.util.Map;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.searchengine.SearchEngineConstructorParam;
import roart.common.searchengine.SearchEngineConstructorResult;
import roart.common.searchengine.SearchEngineDeleteParam;
import roart.common.searchengine.SearchEngineDeleteResult;
import roart.common.searchengine.SearchEngineIndexParam;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;

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

	protected abstract SearchEngineAbstractSearcher createSearcher(String nodename, NodeConfig nodeConf);

	private SearchEngineAbstractSearcher getSearch(String configname, String configid, NodeConfig nodeConf) {
		SearchEngineAbstractSearcher search = searchMap.get(configid);
		if (search == null) {
			search = createSearcher(configname, nodeConf);
			searchMap.put(configid, search);
		}
		return search;
	}

	@RequestMapping(value = "/" + EurekaConstants.CONSTRUCTOR,
			method = RequestMethod.POST)
	public SearchEngineConstructorResult processConstructor(@RequestBody SearchEngineConstructorParam param)
			throws Exception {
		String error = null;
		try {
			SearchEngineAbstractSearcher search = getSearch(param.configname, param.configid, param.conf);
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
		SearchEngineAbstractSearcher search = searchMap.remove(param.configname);
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
            SearchEngineAbstractSearcher search = getSearch(param.configname, param.configid, param.conf);
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
            SearchEngineAbstractSearcher search = getSearch(param.configname, param.configid, param.conf);
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
		SearchEngineAbstractSearcher search = getSearch(param.configname, param.configid, param.conf);
		SearchEngineDeleteResult ret = search.deleteme(param);
		return ret;
	}

	@RequestMapping(value = "/" + EurekaConstants.INDEX,
			method = RequestMethod.POST)
	public SearchEngineIndexResult processIndex(@RequestBody SearchEngineIndexParam param)
			throws Exception {
		SearchEngineAbstractSearcher search = getSearch(param.configname, param.configid, param.conf);
		SearchEngineIndexResult ret = search.indexme(param);
		return ret;
	}

	@RequestMapping(value = "/" + EurekaConstants.SEARCH,
			method = RequestMethod.POST)
	public SearchEngineSearchResult processSearch(@RequestBody SearchEngineSearchParam param)
			throws Exception {
		SearchEngineAbstractSearcher search = getSearch(param.configname, param.configid, param.conf);
		SearchEngineSearchResult ret = search.searchme(param);
		return ret;
	}

	@RequestMapping(value = "/" + EurekaConstants.SEARCHMLT,
			method = RequestMethod.POST)
	public SearchEngineSearchResult processSearchSimilar(@RequestBody SearchEngineSearchParam param)
			throws Exception {
		SearchEngineAbstractSearcher search = getSearch(param.configname, param.configid, param.conf);
		SearchEngineSearchResult ret = search.searchmlt(param);
		return ret;
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SearchEngineAbstractController.class, args);
	}
}
