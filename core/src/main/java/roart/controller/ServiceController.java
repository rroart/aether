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
import roart.config.MyXMLConfig;
import roart.database.DatabaseLanguagesResult;
import roart.service.ControlService;
import roart.service.SearchService;
import roart.service.ServiceParam;
import roart.service.ServiceResult;
import roart.util.EurekaConstants;
import roart.util.EurekaUtil;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceController implements CommandLineRunner {

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

    @RequestMapping(value = "/" + EurekaConstants.GETLANGUAGES,
            method = RequestMethod.POST)
    public DatabaseLanguagesResult getLanguages(@RequestBody ServiceParam param)
            throws Exception {
        DatabaseLanguagesResult result = new DatabaseLanguagesResult();
        try {
            result.languages = getInstance().getLanguages();
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            //result.error = e.getMessage();
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
		result.list = getInstance().traverse(param);
	    } else {
	        result.list = getInstance().traverse(param);
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
            result.list = getInstance().overlapping(param);
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
            result.list = getInstance().indexsuffix(param);
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
            result.list = getInstance().index(param);
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
            result.list = getInstance().reindexdatelower(param);
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
            result.list = getInstance().reindexdatehigher(param);
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
            result.list = getInstance().reindexlanguage(param);
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
            result.list = getInstance().cleanupfs(param.dirname);
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
            result.list = getInstance().memoryusage(param);
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
            result.list = getInstance().notindexed(param);
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
            result.list = getInstance().filesystemlucenenew(param);
	    } else {
		result.list = getInstance().filesystemlucenenew(param);
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
            result.list = getInstance().filesystemlucenenew(param);
	    } else {
		result.list = getInstance().dbindex(param);
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
            result.list = getInstance().filesystemlucenenew(param);
	    } else {
		result.list = getInstance().dbsearch(param);
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
            result.list = getInstance().consistentclean(param);
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
            result.list = getInstance().deletepathdb(param);
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
            result.list = getInstance().searchengine(param);
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
            result.list = getInstance().database(param.name);
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
            result.list = getInstance().filesystem(param.name);
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
            result.list = getInstance().machinelearning(param.name);
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
            result.list = getInstance2().searchme(param);
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
            result.list = getInstance2().searchsimilar(param);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            // TODO fix
            //result.error = e.getMessage();
        }
        return result;
    }

    // TODO move this
    private static void doConfig(String configFile) {
        MyXMLConfig conf = MyXMLConfig.instance(configFile);
        conf.config();
        
        //ControlService.lock = MyLockFactory.create();
        
        ControlService maininst = new ControlService();
        maininst.startThreads();

        System.out.println("config done");
        //log.info("config done");
 
    }
    
        public static void main(String[] args) throws Exception {
                SpringApplication.run(ServiceController.class, args);
        }

        @Override
        public void run(String... args) throws InterruptedException {
            EurekaUtil.initEurekaClient();
            String configFile = null;
            if (args != null) {
                configFile = args[0];
            }
            doConfig(configFile);
        }
}
