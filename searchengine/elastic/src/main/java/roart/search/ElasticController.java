package roart.search;

import java.util.Map;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import roart.common.searchengine.SearchEngineConstructorParam;
import roart.common.searchengine.SearchEngineDeleteParam;
import roart.common.searchengine.SearchEngineDeleteResult;
import roart.common.searchengine.SearchEngineIndexParam;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.util.EurekaConstants;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
//@EnableAutoConfiguration
@SpringBootApplication
@EnableDiscoveryClient
public class ElasticController {

        @Autowired
	private DiscoveryClient discoveryClient;
    
    @RequestMapping(value = "/" + EurekaConstants.CONSTRUCTOR,
		    method = RequestMethod.POST)
		    public void processConstructor(@RequestBody SearchEngineConstructorParam constructor)
	throws Exception {
    	new SearchElastic(constructor);
    }

    @RequestMapping(value = "/" + EurekaConstants.DELETE,
		    method = RequestMethod.POST)
		    public SearchEngineDeleteResult processDelete(@RequestBody SearchEngineDeleteParam delete)
	throws Exception {
    	SearchEngineDeleteResult ret = SearchElastic.deleteme(delete);
    	return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.INDEX,
		    method = RequestMethod.POST)
		    public SearchEngineIndexResult processIndex(@RequestBody SearchEngineIndexParam index)
	throws Exception {
    	SearchEngineIndexResult ret = SearchElastic.indexme(index);
    	return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.SEARCH,
		    method = RequestMethod.POST)
		    public SearchEngineSearchResult processSearch(@RequestBody SearchEngineSearchParam search)
	throws Exception {
    	SearchEngineSearchResult ret = SearchElastic.searchme(search);;
    	return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.SEARCHMLT,
		    method = RequestMethod.POST)
		    public SearchEngineSearchResult processSearchSimilar(@RequestBody SearchEngineSearchParam search)
	throws Exception {
    	SearchEngineSearchResult ret = SearchElastic.searchmlt(search);
    	return ret;
    }

    public static void main(String[] args) throws Exception {
	SpringApplication.run(ElasticController.class, args);
    }
}
