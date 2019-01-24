package roart.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import roart.common.config.MyConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.database.DatabaseLanguagesResult;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.common.service.ServiceParam;
import roart.common.service.ServiceResult;
import roart.config.MyXMLConfig;
import roart.eureka.util.EurekaUtil;
import roart.service.ControlService;
import roart.service.SearchService;

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
                        log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            log.error(Constants.EXCEPTION, e);
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
            if (args != null && args.length > 0) {
            System.out.println("args " + args[0]);
            log.info("args " + args[0]);
            }
            if (args != null && args.length > 0) {
                configFile = args[0];
            }
            try {
                doConfig(configFile);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
}
