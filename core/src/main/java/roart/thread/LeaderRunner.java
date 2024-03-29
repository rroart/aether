package roart.thread;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.queue.Queues;
import roart.service.ControlService;
import roart.common.leader.impl.MyLeaderFactory;
import roart.common.model.ConfigParam;
import roart.common.util.JsonUtil;
import roart.common.webflux.WebFluxUtil;
import roart.common.zkutil.ZKUtil;
import roart.eureka.util.EurekaUtil;
import roart.common.hcutil.GetHazelcastInstance;
import roart.common.leader.MyLeader;
import roart.common.collections.MyCollections;
import roart.common.collections.MyMap;
import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyAtomicLong;
import roart.common.collections.impl.MyQueues;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import roart.common.inmemory.common.Inmemory;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.InmemoryMessage;

import java.util.HashMap;
import java.util.HashSet;

public class LeaderRunner implements Runnable {
    static Logger log = LoggerFactory.getLogger(LeaderRunner.class);

    private static final java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();

    ThreadPoolExecutor /*ExecutorService*/ pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

    private NodeConfig nodeConf;

    private ControlService controlService;
    
    public LeaderRunner(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.controlService = controlService;
    }

    @SuppressWarnings("squid:S2189")
    public void run() {
        Queues queues = new Queues(nodeConf, controlService);
        MyMap<String,InmemoryMessage> resultMap = new Queues(nodeConf, controlService).getResultMap();
        MyMap<String, String> traverseCountMap = new Queues(nodeConf, controlService).getTraverseCountMap();
        Map<String, Long> keyMap = new HashMap<>();
        MyLeader leader = new MyLeaderFactory().create(controlService.nodename, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance(nodeConf));
        long qtime = 0;
        while (true) {
            boolean leading = leader.await(1, TimeUnit.SECONDS);
            if (!leading) {
                log.info("I am not leader");
            } else {
                log.info("I am leader");

                if (!"false".equals(System.getProperty("eureka.client.enabled"))) {
                    Runnable confMe = new EurekaThread(nodeConf);
                    confMe.run(); // return when finished, don't start new
                }

                String zAppidPath = ZKUtil.getAppidPath() + Constants.CONFIG;
                String zCommonPath = ZKUtil.getCommonPath() + Constants.CONFIG;
                boolean useCommon = !zAppidPath.equals(zCommonPath);
                CuratorFramework curatorClient = controlService.curatorClient;
                while (true) {
                    Set<String> done = new HashSet<>();
                    try {
                        zConfigure(zAppidPath, curatorClient, done);
                        if (useCommon) {
                            zConfigure(zCommonPath, curatorClient, done);
                        }
                        Thread.sleep(10 * 1000);
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e);
                        break;
                    }
                    try {
                        String path = ZKUtil.getAppidPath() + Constants.DB;
                        deleteOld(curatorClient, path, 15 * 60 * 1000, false, false);
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e);
                        break;
                    }

                    try {
                        String path = ZKUtil.getAppidPath() + Constants.QUEUES;
                        deleteOld(curatorClient, path, 20 * 60 * 1000, true, false);
                        if (useCommon) {
                            String pathCommon = ZKUtil.getCommonPath() + Constants.QUEUES;
                            deleteOld(curatorClient, pathCommon, 20 * 60 * 1000, true, false);                            
                        }
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e);
                        break;
                    }

                    try {
                        String path = ZKUtil.getAppidPath() + Constants.DATA;
                        deleteOld(curatorClient, path, 20 * 60 * 1000, true, true);
                        if (useCommon) {
                            String pathCommon = ZKUtil.getCommonPath() + Constants.DATA;
                            deleteOld(curatorClient, pathCommon, 20 * 60 * 1000, true, true);                            
                        }
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e);
                        break;
                    }
                    deleteOldResults(resultMap, keyMap);
                    try {
                        deleteOldTraverseCounts(traverseCountMap, curatorClient);
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e);
                    }
                    long newTime = System.currentTimeMillis();
                    try {
                        if ((newTime - qtime) > 60 * 1000) {
                            qtime = newTime;
                            mylogs(queues, curatorClient);
                        }
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e);
                    }
                }
            }
            log.info("Leader status: {}", leader.isLeader());
            try {
                TimeUnit.SECONDS.sleep(60 /* 3600 */);
            } catch (InterruptedException e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
    }

    private void zConfigure(String zPath, CuratorFramework curatorClient, Set<String> done)
            throws Exception, InterruptedException {
        if (curatorClient.checkExists().forPath(zPath) != null) {
            List<String> children = curatorClient.getChildren().forPath(zPath);
            for (String child : children) {
                log.debug("Child {}", child);
                String path = zPath + "/" + child;            
                String data = new String(curatorClient.getData().forPath(path));
                Stat stat = curatorClient.checkExists().forPath(path);
                long time = System.currentTimeMillis() - stat.getMtime();
                if (false && time > 3600 * 1000) {
                    curatorClient.delete().forPath(path);
                    done.remove(data);
                }
                if (done.contains(data)) {
                    continue;
                }
                ConfigParam param = new ConfigParam();
                param.setConfigname(controlService.getConfigName());
                param.setConfigid(controlService.getConfigId());
                param.setIconf(controlService.iconf);
                param.setIserver(nodeConf.getInmemoryServer());
                if (Constants.REDIS.equals(nodeConf.getInmemoryServer())) {
                    param.setIconnection(nodeConf.getInmemoryRedis());
                } else {
                    param.setIconnection(nodeConf.getInmemoryHazelcast());
                }
                try {
                    String result = WebFluxUtil.sendMe(String.class, param, "http://" + data + "/" + EurekaConstants.CONSTRUCTOR);
                    done.add(data);
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                    curatorClient.delete().forPath(path);
                }
            }      
        }
    }

    private void deleteOldResults(MyMap<String, InmemoryMessage> resultMap, Map<String, Long> keyMap) {
        Set<String> removes = new HashSet<>();
        for (String key : resultMap.keySet()) {
            Long time = System.currentTimeMillis();
            Long timestamp = keyMap.get(key);
            if (timestamp == null) {
                keyMap.put(key, time);
            } else {
                if ((timestamp - time) / 1000 > 120) {
                    removes.add(key);
                }
            }
        }
        for (String id : removes) {
            // duplicated
            Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
            InmemoryMessage msg = resultMap.remove(id, InmemoryMessage.class);
            inmemory.delete(msg);
            resultMap.remove(id);
            log.info("Removed old result {}", id);
        }
    }

    private void deleteOldTraverseCounts(MyMap<String, String> traverseCountMap, CuratorFramework curatorClient) throws Exception {
        Map<String, String> map = traverseCountMap.getAll();
        Set<String> removes = new HashSet<>();
        for (String key : traverseCountMap.keySet()) {
            Long time = System.currentTimeMillis();
            Long timestamp = Long.valueOf(map.get(key));
            if ((timestamp - time) / 1000 > 120) {
                removes.add(key);
            }
        }
        for (String id : removes) {
            // duplicated
            traverseCountMap.remove(id);
            log.info("Removed old traverse {}", id);
            String path = ZKUtil.getAppidPath(Constants.QUEUES) + id;
            Stat b = curatorClient.checkExists().forPath(path);
            if (b == null) {
                continue;
            }
            curatorClient.delete().forPath(path );                                            
        }
    }

    private void deleteOld(CuratorFramework curatorClient, String path, int deleteTime, boolean deleteQueue, boolean deleteInmemory) throws Exception {
        Stat b = curatorClient.checkExists().forPath(path);
        if (b == null) {
            return;
        }
        List<String> children = curatorClient.getChildren().forPath(path);
        if (!children.isEmpty()) {
            //log.info("Children {} {}", children.size(), path);
        }
        log.debug("Children {}", children.size());
        for (String child : children) {
            Stat stat = curatorClient.checkExists().forPath(path + "/" + child);
            if (stat == null) {
                log.info("Gone already {}", path + "/" + child);
                continue;
            }
            log.debug("Time {} {}", System.currentTimeMillis(), stat.getMtime());;
            long time = System.currentTimeMillis() - stat.getMtime();
            log.debug("Time {}", time);
            if (stat.getNumChildren() > 0) {
                deleteOld(curatorClient, path + "/" + child, deleteTime, deleteQueue, deleteInmemory);
                continue;
            }
            if (time > deleteTime) {
                byte[] data = curatorClient.getData().forPath(path + "/" + child);                                
                curatorClient.delete().forPath(path + "/" + child);                                
                log.info("Delete old lock or data {}", child);
                if (deleteQueue) {
                    // TODO MyAtomicLong
                    // TODO distr
                    MyCollections.remove(child);
                    //MyQueue queue =  MyCollections.get(child, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance(nodeConf));
                    //queue.destroy();
                }
                if (deleteInmemory && data != null && data.length > 0) {
                    String str = new String(data);
                    Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
                    InmemoryMessage msg = JsonUtil.convert(str, InmemoryMessage.class);
                    inmemory.delete(msg);
                }
            }
        }
    }

    private void mylogs(Queues queues, CuratorFramework curatorClient) throws Exception {
        log.info("Queues {} {} {} {}", queues.getListingQueueSize(), queues.getTraverseQueueSize(), queues.getConvertQueueSize(), queues.getIndexQueueSize());
        log.info("Queues {} {} {} {}", queues.getMyListings().get(), queues.getMyTraverses().get(), queues.getMyConverts().get(), queues.getMyIndexs().get());
        queues.queueStat();
        String path = ZKUtil.getAppidPath() + Constants.QUEUES;
        Stat b = curatorClient.checkExists().forPath(path);
        if (b == null) {
            return;
        }
        List<String> children = curatorClient.getChildren().forPath(path);
        if (!children.isEmpty()) {
            //log.info("Children {} {}", children.size(), path);
        }
        log.debug("Children {}", children.size());
        for (String child : children) {
            Stat stat = curatorClient.checkExists().forPath(path + "/" + child);
            if (stat == null) {
                log.info("Gone already {}", path + "/" + child);
                continue;
            }
            log.debug("Time {} {}", System.currentTimeMillis(), stat.getMtime());;
            long time = System.currentTimeMillis() - stat.getMtime();
            log.debug("Time {}", time);
            /*
            if (stat.getNumChildren() > 0) {
                deleteOld(curatorClient, path + "/" + child, deleteTime, deleteQueue, deleteInmemory);
                continue;
            }
            */
            if ((stat.getMtime() - System.currentTimeMillis()) < 20 * 60 * 1000) {
                byte[] data = curatorClient.getData().forPath(path + "/" + child);                                
                //curatorClient.delete().forPath(path + "/" + child);                                
                //log.info("Delete old lock or data {}", child);
                MyQueue queue = MyQueues.get(new String(data), nodeConf, controlService.curatorClient);
                log.info("Queue size {} {}", child, queue.size());
            }
        }
        
    }
}
