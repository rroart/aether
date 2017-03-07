package roart.search;

import java.util.Map;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import roart.common.searchengine.SearchEngineDeleteParam;
import roart.common.searchengine.SearchEngineDeleteResult;
import roart.common.searchengine.SearchEngineIndexParam;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
//@EnableAutoConfiguration
@SpringBootApplication
@EnableDiscoveryClient
public class LuceneController {

        @Autowired
	private DiscoveryClient discoveryClient;
    
    @RequestMapping(value = "/delete",
		    method = RequestMethod.POST)
		    public SearchEngineDeleteResult processDelete(@RequestBody SearchEngineDeleteParam delete)
	throws Exception {
    	SearchEngineDeleteResult ret = null;
    	return ret;
    }

    @RequestMapping(value = "/index",
		    method = RequestMethod.POST)
		    public SearchEngineIndexResult processIndex(@RequestBody SearchEngineIndexParam index)
	throws Exception {
    	SearchEngineIndexResult ret = null;
    	return ret;
    }

    @RequestMapping(value = "/search",
		    method = RequestMethod.POST)
		    public SearchEngineSearchResult processSearch(@RequestBody SearchEngineSearchParam search)
	throws Exception {
    	SearchEngineSearchResult ret = null;
    	return ret;
    }

    public static void main(String[] args) throws Exception {
	SpringApplication.run(LuceneController.class, args);
    }
}
