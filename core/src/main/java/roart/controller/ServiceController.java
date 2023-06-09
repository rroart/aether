package roart.controller;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;

import roart.common.config.MyConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.database.DatabaseLanguagesResult;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.Inmemory;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.common.service.ServiceParam;
import roart.common.service.ServiceResult;
import roart.common.util.FsUtil;
import roart.config.MyXMLConfig;
import roart.content.ClientHandler;
import roart.database.IndexFilesDao;
import roart.eureka.util.EurekaUtil;
import roart.filesystem.FileSystemDao;
import roart.service.ControlService;
import roart.service.SearchService;

@CrossOrigin
@RestController
@SpringBootApplication
//@EnableDiscoveryClient
//@ComponentScan(basePackages = { "roart.eureka.util" })
public class ServiceController implements CommandLineRunner {

        private Logger log = LoggerFactory.getLogger(this.getClass());

        @Lazy
        @Autowired
        public EurekaClient eurekaClient;
        
        //@Lazy
        //@Autowired
        public DiscoveryClient discoveryClient = null;

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
		result.list = ClientHandler.doClient(param);
	    } else {
	        result.list = ClientHandler.doClient(param);
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
            result.list = ClientHandler.doClient(param);
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
            result.list = ClientHandler.doClient(param);
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
            result.list = ClientHandler.doClient(param);
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
            result.list = ClientHandler.doClient(param);
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
            result.list = ClientHandler.doClient(param);
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
            result.list = ClientHandler.doClient(param);
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
            result.list = ClientHandler.doClient(param);
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
            result.list = ClientHandler.doClient(param);
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
            result.list = ClientHandler.doClient(param);
	    } else {
		result.list = ClientHandler.doClient(param);
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
            result.list = ClientHandler.doClient(param);
	    } else {
		result.list = ClientHandler.doClient(param);
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
            result.list = ClientHandler.doClient(param);
	    } else {
		result.list = ClientHandler.doClient(param);
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
            result.list = ClientHandler.doClient(param);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.DBCHECK,
            method = RequestMethod.POST)
    public ServiceResult dbCheck(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            result.list = ClientHandler.doClient(param);
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
            result.list = ClientHandler.doClient(param);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.DBCLEAR,
            method = RequestMethod.POST)
    public ServiceResult getDbClear(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            ClientHandler.doClient(param);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.DBDROP,
            method = RequestMethod.POST)
    public ServiceResult getDbDrop(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            ClientHandler.doClient(param);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.INDEXCLEAN,
            method = RequestMethod.POST)
    public ServiceResult getIndexClean(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            ClientHandler.doClient(param);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.INDEXDELETE,
            method = RequestMethod.POST)
    public ServiceResult getIndexDelete(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            ClientHandler.doClient(param);
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

    @RequestMapping(value = "/" + EurekaConstants.DOWNLOAD + "/{id}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE,
            method = RequestMethod.GET)
    public @ResponseBody byte[] getDownload(@PathVariable String id)
            throws Exception {
        InputStream result = null;
        try {
            String md5 = id;
            IndexFiles index = IndexFilesDao.getByMd5(md5);
            FileLocation fl = index.getaFilelocation();
            FileObject f = FsUtil.getFileObject(fl);
            Inmemory inmemory = InmemoryFactory.get(MyConfig.conf.getInmemoryServer(), MyConfig.conf.getInmemoryHazelcast(), MyConfig.conf.getInmemoryRedis());
            InmemoryMessage message = FileSystemDao.readFile(f);
            result = inmemory.getInputStream(message);
            inmemory.delete(message);
            //result = FileSystemDao.getInputStream(f);
            // delete inmem
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            // TODO fix
            //result.error = e.getMessage();
        }
        return IOUtils.toByteArray(result);
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
            EurekaUtil.discoveryClient = discoveryClient;
            EurekaUtil.eurekaClient = eurekaClient;
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
