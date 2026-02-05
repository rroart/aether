package roart.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Method;
import java.util.Collections;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import roart.common.config.ConfigConstants;
import roart.common.config.NodeConfig;
import roart.common.model.Location;
import roart.config.MyXMLConfig;
import roart.filesystem.FileSystemDao;
import roart.service.ControlService;

public class FsIT {

    private NodeConfig nodeConf = MyXMLConfig.getConfigInstance(getConfigfile());

    @Test
    public void testConstructFileSystemDao() {
        // Minimal smoke test: construct FileSystemDao with a basic NodeConfig and ControlService.
        //NodeConfig nodeConf = new NodeConfig();
        ControlService controlService = new ControlService(nodeConf);
        FileSystemDao dao = new FileSystemDao(nodeConf, controlService);
        assertNotNull(dao, "FileSystemDao should be constructed");
    }

    @Test
    public void testGetUrlWithMockedCuratorReturnsData() throws Exception {
        //NodeConfig nodeConf = new NodeConfig();
        ControlService controlService = new ControlService(nodeConf);
        FileSystemDao dao = new FileSystemDao(nodeConf, controlService);

        // Mock CuratorFramework and builders to simulate a ZK node that contains a URL
        CuratorFramework curator = Mockito.mock(CuratorFramework.class);
        ExistsBuilder existsBuilder = Mockito.mock(ExistsBuilder.class);
        GetChildrenBuilder childrenBuilder = Mockito.mock(GetChildrenBuilder.class);
        GetDataBuilder dataBuilder = Mockito.mock(GetDataBuilder.class);
        Stat stat = Mockito.mock(Stat.class);

        Mockito.when(curator.checkExists()).thenReturn(existsBuilder);
        Mockito.when(existsBuilder.forPath(Mockito.anyString())).thenReturn(stat);
        Mockito.when(curator.getChildren()).thenReturn(childrenBuilder);
        Mockito.when(childrenBuilder.forPath(Mockito.anyString())).thenReturn(Collections.emptyList());
        Mockito.when(curator.getData()).thenReturn(dataBuilder);
        Mockito.when(dataBuilder.forPath(Mockito.anyString())).thenReturn("localhost:9090".getBytes());
        Mockito.when(stat.getMtime()).thenReturn(System.currentTimeMillis());

        // Use reflection to call package-private getUrl(CuratorFramework, Location, String, String)
        Method getUrl = FileSystemDao.class.getDeclaredMethod("getUrl", CuratorFramework.class, Location.class, String.class, String.class);
        getUrl.setAccessible(true);

        Location loc = new Location("mynode", "local");
        String url = (String) getUrl.invoke(dao, curator, loc, "/some/path", "");

        assertEquals("localhost:9090", url);
    }

    @Test
    public void testStringOrNullReflection() throws Exception {
        //NodeConfig nodeConf = new NodeConfig();
        ControlService controlService = new ControlService(nodeConf);
        FileSystemDao dao = new FileSystemDao(nodeConf, controlService);

        Method stringOrNull = FileSystemDao.class.getDeclaredMethod("stringOrNull", String.class);
        stringOrNull.setAccessible(true);
        String res1 = (String) stringOrNull.invoke(dao, (Object) null);
        String res2 = (String) stringOrNull.invoke(dao, "abc");

        assertEquals("", res1);
        assertEquals("/abc", res2);
    }
    
    // duplicated from MyXMLConfig to get config file path
    private String getConfigfile() {
        String myConfigFile = System.getProperty("config");
        if (myConfigFile == null) {
            myConfigFile = ConfigConstants.CONFIGFILE;
        }
        //myConfigFile = "../conf/" + myConfigFile;
        return myConfigFile;
    }

}