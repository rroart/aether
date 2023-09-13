package roart.thread;

import java.util.Set;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.hcutil.GetHazelcastInstance;
import roart.service.ControlService;
import roart.common.leader.impl.MyLeaderFactory;
import roart.common.model.ConfigParam;
import roart.common.webflux.WebFluxUtil;
import roart.eureka.util.EurekaUtil;
import roart.common.leader.MyLeader;
import roart.common.collections.MyQueue;
import roart.common.collections.impl.MyQueues;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.constants.EurekaConstants;
import java.util.HashSet;

public class LeaderRunner implements Runnable {
    static Logger log = LoggerFactory.getLogger(ClientQueueRunner.class);

    private static final java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();

    private static final int LIMIT = 100;

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
        MyLeader leader = new MyLeaderFactory().create(controlService.nodename, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance(nodeConf.getInmemoryHazelcast()));
        while (true) {
            boolean leading = leader.await(1, TimeUnit.SECONDS);
            if (!leading) {
                log.info("I am not leader");
            } else {
                log.info("I am leader");
                String zPath = "/" + Constants.AETHER + "/" + Constants.CONFIG;
                CuratorFramework curatorClient = controlService.curatorClient;
                while (true) {
                    Set<String> done = new HashSet<>();
                    try {
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
                            Thread.sleep(10 * 1000);
                        }
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e);
                        break;
                    }
                    try {
                        String path = "/" + Constants.AETHER + "/" + Constants.DB;
                        deleteOld(curatorClient, path, 15 * 60 * 1000, false);
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e);
                        break;
                    }

                    try {
                        String path = "/" + Constants.AETHER + "/" + Constants.QUEUES;
                        deleteOld(curatorClient, path, 20 * 60 * 1000, true);
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e);
                        break;
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

    private void deleteOld(CuratorFramework curatorClient, String path, int deleteTime, boolean deleteQueue) throws Exception {
        Stat b = curatorClient.checkExists().forPath(path);
        if (b == null) {
            //continue;
        }
        List<String> children = curatorClient.getChildren().forPath(path);
        log.debug("Children {}", children.size());
        for (String child : children) {
            Stat stat = curatorClient.checkExists().forPath(path + "/" + child);
            log.debug("Time {} {}", System.currentTimeMillis(), stat.getMtime());;
            long time = System.currentTimeMillis() - stat.getMtime();
            log.debug("Time {}", time);
            if (stat.getNumChildren() > 0) {
                deleteOld(curatorClient, path + "/" + child, deleteTime, deleteQueue);
                continue;
            }
            if (time > deleteTime) {
                curatorClient.delete().forPath(path + "/" + child);                                
                log.info("Delete old lock {}", child);
                if (deleteQueue) {
                    MyQueue queue = MyQueues.get(child, nodeConf, controlService.curatorClient, GetHazelcastInstance.instance(nodeConf.getInmemoryHazelcast()));
                    queue.destroy();
                }
            }
        }
    }

}
