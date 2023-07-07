    package roart.service;
    
    import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.collections.MySet;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.service.ServiceParam;
import roart.common.synchronization.MyLock;
import roart.common.util.FsUtil;
import roart.common.zkutil.ZKMessageUtil;
import roart.content.ClientHandler;
import roart.database.IndexFilesDao;
import roart.filesystem.FileSystemDao;
import roart.queue.Queues;
import roart.search.SearchDao;
import roart.thread.CamelRunner;
import roart.thread.ControlRunner;
import roart.thread.ConvertRunner;
import roart.thread.DbRunner;
import roart.thread.IndexRunner;
import roart.thread.TraverseQueueRunner;
import roart.thread.ZKRunner;
import roart.util.MyAtomicLong;
import roart.util.MyAtomicLongs;
import roart.util.MyCollections;
import roart.util.MyList;
import roart.util.MyLists;
import roart.util.MyLockFactory;
import roart.util.MySets;
import roart.util.TraverseUtil;
    
    public class ControlService {
        private Logger log = LoggerFactory.getLogger(this.getClass());
    
        private IndexFilesDao indexFilesDao = new IndexFilesDao();
        
        public static volatile Integer writelock = new Integer(-1);
    
        private static volatile int mycounter = 0;
        
        public static int getMyCounter() {
            return mycounter++;
        }
        
        public static String getMyId() {
            return nodename + getMyCounter();
        }
        
        public NodeConfig getRemoteConfig() {
            return MyConfig.conf;
            /*
            ServiceParam param = new ServiceParam();
            ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.GETCONFIG);
            MyConfig.conf = result.config;
            */
        }
        
        public void setRemoteConfig() {
            /*
            ServiceParam param = new ServiceParam();
            param.config = getConfig();
            ServiceResult result = EurekaUtil.sendMe(ServiceResult.class, param, getAppName(), EurekaConstants.SETCONFIG);
            return;
            */     
            // TODO fix
        }
        
        private NodeConfig getConfig() {
            return MyConfig.conf;
        }
    
        static public String nodename = "localhost";
        
        public static String configMd5 = "md5";
            
        // old, probably oudated by overlapping?
        public List cleanupfs(String dirname) {
    	//List<String> retlist = new ArrayList<String>();
    	Set<String> filesetnew = new HashSet<String>();
    	try {
    	    String[] dirlist = { dirname };
    	    for (int i = 0; i < dirlist.length; i ++) {
    		Set<String> filesetnew2 = TraverseUtil.dupdir(FsUtil.getFileObject(dirlist[i]));
    		filesetnew.addAll(filesetnew2);
    	    }
    	} catch (Exception e) {
    		log.error(Constants.EXCEPTION, e);
    	}
    	return new ArrayList<String>(filesetnew);
        }
    
        private static ConvertRunner convertRunnable = null;
        public static Thread convertWorker = null;
        private static IndexRunner indexRunnable = null;
        public static Thread indexWorker = null;
        private static DbRunner dbRunnable = null;
        public static Thread dbWorker = null;
        private static ControlRunner controlRunnable = null;
        private static Thread controlWorker = null;
        private static ZKRunner zkRunnable = null;
        public static Thread zkWorker = null;
        private static TraverseQueueRunner traverseQueueRunnable = null;
        public static Thread traverseQueueWorker = null;
        private static CamelRunner camelRunnable = null;
        public static Thread camelWorker = null;
    
        public static CuratorFramework curatorClient = null;
        public void startThreads() {
                if (convertRunnable == null) {
                    startConvertWorker();
                        }
                	if (indexRunnable == null) {
        	startIndexWorker();
        	}
        	if (dbRunnable == null) {
        	startDbWorker();
        	}
        	if (controlRunnable == null) {
        	startControlWorker();
        	}
        	if (MyConfig.conf.getZookeeper() != null && zkRunnable == null) {
        	startZKWorker();
        	}
            if (MyConfig.conf.getZookeeper() != null && MyConfig.conf.wantZookeeperSmall() && traverseQueueRunnable == null) {
            startTraversequeueWorker();
            }
            if (traverseQueueRunnable == null) {
            startTraversequeueWorker();
            }
            startCamel();
            startMem();
        }
    
    	private void startCamel() {
    	    /*
    	        CamelContext context = new DefaultCamelContext();
    	        ConnectionFactory connectionFactory =
    	                new ActiveMQConnectionFactory("vm://localhost");
    	                context.addComponent("jms",
    	                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
    	                try {
    	                    context.addRoutes(new RouteBuilder() {
    	                        public void configure() {
    	                            from("ftp://rider.com/orders")
    	                            .to("jms:incomingDoc");
    	                        }
    	                });
    	            context.start();
    	        } catch (Exception e) {
    	            log.error(Constants.EXCEPTION, e);
    	        }
    */
        }
    
        private void startControlWorker() {
    		controlRunnable = new ControlRunner();
        	controlWorker = new Thread(controlRunnable);
        	controlWorker.setName("ControlWorker");
        	controlWorker.start();
        	log.info("starting control worker");
    	}
    
    	public void startConvertWorker() {
            int timeout = MyConfig.conf.getTikaTimeout();
            ConvertRunner.timeout = timeout;
    
                convertRunnable = new ConvertRunner();
                convertWorker = new Thread(convertRunnable);
                convertWorker.setName("ConvertWorker");
                convertWorker.start();
                log.info("starting convert worker");
        }
    
    	public void startIndexWorker() {
    		indexRunnable = new IndexRunner();
        	indexWorker = new Thread(indexRunnable);
        	indexWorker.setName("IndexWorker");
        	indexWorker.start();
        	log.info("starting index worker");
    	}
    
        public void startMem() {
            MemRunner indexRunnable = new MemRunner();
            indexWorker = new Thread(indexRunnable);
            indexWorker.setName("IndexWorker");
            indexWorker.start();
            log.info("starting index worker");
    }

    	public void startDbWorker() {
    		dbRunnable = new DbRunner();
        	dbWorker = new Thread(dbRunnable);
        	dbWorker.setName("DbWorker");
        	dbWorker.start();
        	log.info("starting db worker");
    	}
    
    	public void startZKWorker() {
    		zkRunnable = new ZKRunner();
        	zkWorker = new Thread(zkRunnable);
        	zkWorker.setName("ZKWorker");
        	zkWorker.start();
        	log.info("starting zk worker");
    	}
    
            public void startTraversequeueWorker() {
            traverseQueueRunnable = new TraverseQueueRunner();
            traverseQueueWorker = new Thread(traverseQueueRunnable);
            traverseQueueWorker.setName("TraverseWorker");
            traverseQueueWorker.start();
            log.info("starting traverse queue worker");
        }
    
        @SuppressWarnings("rawtypes")
    	private List<List> mergeListSet(Set<List> listSet, int size) {
    	List<List> retlistlist = new ArrayList<>();
    	for (int i = 0 ; i < size ; i++ ) {
    	    List<ResultItem> retlist = new ArrayList<>();
    	    retlistlist.add(retlist);
    	}
    	for (List<List> listArray : listSet) {
    	    for (int i = 0 ; i < size ; i++ ) {
    		retlistlist.get(i).addAll(listArray.get(i));
    	    }
    	}
    	return retlistlist;
        }
    
    	public String[] getLanguages() throws Exception {
        return indexFilesDao.getLanguages().stream().toArray(String[]::new);
        }
        
            public List searchengine(ServiceParam param) {
            	//MyXMLConfig property = (MyXMLConfig) MyXMLConfig.getConfigInstance();
            	//property.configIndexing();
            	return null;
            }
            
            public List machinelearning(String learning) {
            	//MyXMLConfig property = (MyXMLConfig) MyXMLConfig.getConfigInstance();
            	//property.configClassify();   
            	return null;
            }
    
            public List database(String db) {
            	//MyXMLConfig property = (MyXMLConfig) MyXMLConfig.getConfigInstance();
            	return null;
            	// TODO fix
            	//property.configClassify(db);      	
            }
    
            public List filesystem(String fs) {
            	//MyXMLConfig property = (MyXMLConfig) MyXMLConfig.getConfigInstance();
            	return null;
            	// TODO fix
            	//property.configFileSystem(fs);      	
            }
            
            class MemRunner implements Runnable {

                private static Logger log = LoggerFactory.getLogger(IndexRunner.class);

                public static volatile int timeout = 3600;

                public void run() {
                    long heapSize = Runtime.getRuntime().totalMemory(); 

                     // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
                     long heapMaxSize = Runtime.getRuntime().maxMemory();

                      // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
                     long heapFreeSize = Runtime.getRuntime().freeMemory(); 
                     log.info("MEM0 " + heapSize + " " + heapMaxSize + " " + heapFreeSize);

                     while (true) {
                        try {
                            TimeUnit.SECONDS.sleep(600);
                        } catch (/*Interrupted*/Exception e) {
                            // TODO Auto-generated catch block
                            log.error(Constants.EXCEPTION, e);
                        }
                    }
                }
            }
    }
