package roart.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.config.MyConfig;
import roart.config.MyPropertyConfig;
import roart.service.ControlService;
import roart.service.SearchService;
import roart.service.ServiceParam;
import roart.service.ServiceResult;
import roart.util.EurekaConstants;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceController {

        private Logger log = LoggerFactory.getLogger(this.getClass());

        private ControlService instance;
        private SearchService instance2;
        
        private ControlService getInstance() {
                if (instance == null) {
                        instance = new ControlService();
                }
                return instance;
        }
        
        private SearchService getInstance2() {
                if (instance2 == null) {
                        instance2 = new SearchService();
                }
                return instance2;
        }
        
        @RequestMapping(value = "/" + EurekaConstants.SETCONFIG,
                        method = RequestMethod.POST)
        public ServiceResult configDb(@RequestBody ServiceParam param)
                        throws Exception {
                ServiceResult result = new ServiceResult();
                try {
                    // TODO fix
                        //getInstance().config(param.config);
                } catch (Exception e) {
                        log.error(roart.util.Constants.EXCEPTION, e);
                        result.error = e.getMessage();
                }
                return result;
        }

    @RequestMapping(value = "/" + EurekaConstants.GETCONFIG,
            method = RequestMethod.POST)
    public ServiceResult getConfig(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            result.config = MyConfig.conf;
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.TRAVERSE,
            method = RequestMethod.POST)
    public ServiceResult getTraverse(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
	    if (param.add == null) {
		instance.traverse();
	    } else {
		instance.traverse(param.add);
	    }
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.OVERLAPPING,
            method = RequestMethod.POST)
    public ServiceResult getOverlapping(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
	    instance.overlapping();
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.INDEXSUFFIX,
            method = RequestMethod.POST)
    public ServiceResult getIndexSuffix(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            instance.indexsuffix(param.suffix, param.reindex);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.INDEX,
            method = RequestMethod.POST)
    public ServiceResult getIndex(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            instance.index(param.add, param.reindex);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.REINDEXDATELOWER,
            method = RequestMethod.POST)
    public ServiceResult getReIndexDateLower(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            instance.reindexdatelower(param.lowerdate, param.reindex);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.REINDEXDATEHIGHER,
            method = RequestMethod.POST)
    public ServiceResult getReIndexDateHigher(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            instance.reindexdatehigher(param.higherdate, param.reindex);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.REINDEXLANGUAGE,
            method = RequestMethod.POST)
    public ServiceResult getReIndexLanguage(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            instance.reindexlanguage(param.lang);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.CLEANUPFS,
            method = RequestMethod.POST)
    public ServiceResult getCleanupFS(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            instance.cleanupfs(param.dirname);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.MEMORYUSAGE,
            method = RequestMethod.POST)
    public ServiceResult getMemoryusage(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            instance.memoryusage();
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.NOTINDEXED,
            method = RequestMethod.POST)
    public ServiceResult getNotIndexed(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            instance.notindexed();
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.FILESYSTEMLUCENENEW,
            method = RequestMethod.POST)
    public ServiceResult getFilesystemLuceneNew(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
	    if (param.add == null) {
            instance.filesystemlucenenew();
	    } else {
		instance.filesystemlucenenew(param.add, param.md5checknew);
	    }
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.DBINDEX,
            method = RequestMethod.POST)
    public ServiceResult getDbIndex(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
	    if (param.add == null) {
            instance.filesystemlucenenew();
	    } else {
		instance.dbindex(param.md5);
	    }
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.DBSEARCH,
            method = RequestMethod.POST)
    public ServiceResult getDbSearch(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
	    if (param.add == null) {
            instance.filesystemlucenenew();
	    } else {
		instance.dbsearch(param.md5);
	    }
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.CONSISTENTCLEAN,
            method = RequestMethod.POST)
    public ServiceResult getConsistentClean(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            instance.consistentclean(param.clean);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.DELETEPATHDB,
            method = RequestMethod.POST)
    public ServiceResult getDeletePathDb(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            instance.deletepathdb(param.path);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.SEARCHENGINE,
            method = RequestMethod.POST)
    public ServiceResult getSearchEngine(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            instance.searchengine(param.name);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.DATABASE,
            method = RequestMethod.POST)
    public ServiceResult getDatabase(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            instance.database(param.name);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.FILESYSTEM,
            method = RequestMethod.POST)
    public ServiceResult getFileSystem(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            instance.filesystem(param.name);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.MACHINELEARNING,
            method = RequestMethod.POST)
    public ServiceResult getMachineLearning(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            instance.machinelearning(param.name);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.SEARCH,
            method = RequestMethod.POST)
    public SearchEngineSearchResult getSearch(@RequestBody SearchEngineSearchParam param)
            throws Exception {
        SearchEngineSearchResult result = new SearchEngineSearchResult();
        try {
            instance2.searchme(param.str, param.searchtype);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            // TODO fix
            //result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.SEARCHMLT,
            method = RequestMethod.POST)
    public SearchEngineSearchResult getSearchSimilar(@RequestBody SearchEngineSearchParam param)
            throws Exception {
        SearchEngineSearchResult result = new SearchEngineSearchResult();
        try {
            instance2.searchsimilar(param.str);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            // TODO fix
            //result.error = e.getMessage();
        }
        return result;
    }

    // TODO move this
    private static void doConfig() {
        MyConfig conf = MyPropertyConfig.instance();
        conf.config();
        
        //ControlService.lock = MyLockFactory.create();
        
        ControlService maininst = new ControlService();
        maininst.startThreads();

        System.out.println("config done");
        //log.info("config done");
 
    }
    
        public static void main(String[] args) throws Exception {
            doConfig();
                SpringApplication.run(ServiceController.class, args);
        }

}
