package roart.filesystem;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import roart.model.FileObject;

public class FileSystemDaoIT {
    
    CuratorFramework curatorClient;
    
    @Before
    public void setup() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);     
        String zookeeperConnectionString = "localhost:2181";
        curatorClient = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        curatorClient.start();
    }
    
    @Test
    public void testIT() {
        FileObject f = new FileObject("/tmp/fakerepo/c/bla", "local");
        String url = FileSystemDao.getUrl(curatorClient, f, "");
        System.out.println("URL " + url);
    }
    /*
                // write zk nodename, hdfs-type, path
                CuratorFramework curatorClient;
                Stat i = curatorClient.checkExists().forPath(null);
                ;
                curatorClient.create().forPath("/hdfs/" + nodename + "/" + path);
     * 
     */
}
