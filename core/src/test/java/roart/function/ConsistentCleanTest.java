package roart.function;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.FileSystemConstants;
import roart.common.model.FileLocation;
import roart.common.model.Location;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.util.FsUtil;
import roart.database.IndexFilesDao;
import roart.filesystem.FileSystemDao;
import roart.search.SearchDao;
import roart.service.ControlService;

import roart.testdata.TestData;

import org.apache.curator.framework.CuratorFramework;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ConsistentCleanTest {

    FileSystemDao fileSystemDao = mock(FileSystemDao.class);
    
    IndexFilesDao indexFilesDao = mock(IndexFilesDao.class);
    
    SearchDao searchDao = mock(SearchDao.class);
    
    TestData testData = new TestData();
    
    static NodeConfig nodeConf;
    
    static ControlService controlService;
    
    @BeforeAll
    public static void first() {
        nodeConf = mock(NodeConfig.class);
        controlService = mock(ControlService.class);
        //ControlService.curatorClient = mock(CuratorFramework.class);
        //when(ControlService.curatorClient.checkExists().forPath(any()).).thenReturn(true);        
    }
    
    @Test
    public void deletepath() throws Exception {
        var flset = testData.fileLocationSet;
        String path = "/tmp";
        List<IndexFiles> indexes = testData.indexFiles;
        List<ResultItem> delList = new ArrayList<>();
        Set<FileObject> delfileset = new HashSet<>(); 
        Set<IndexFiles> ifs = new HashSet<>();
        ConsistentClean cl = new ConsistentClean(null, nodeConf, controlService);
        cl.setFileSystemDao(fileSystemDao);
        cl.setIndexFilesDao(indexFilesDao);
        cl.setSearchDao(searchDao);
        cl.extracted(delList, delfileset, path, indexes, ifs, false, true);
        assertEquals(1, delList.size());
        assertEquals(1, ifs.size());
        assertEquals(1, indexes.get(2).getFilelocations().size());
    }

    @Test
    public void consistentnoclean() throws Exception {
        var flset = testData.fileLocationSet;
        var fls = testData.fileLocations;
        FileObject fo = mock(FileObject.class);
        when(fileSystemDao.get(FsUtil.getFileObject(fls.get(0)))).thenReturn(fo);
        when(fileSystemDao.get(FsUtil.getFileObject(fls.get(3)))).thenReturn(fo);
        when(fileSystemDao.exists(fo)).thenReturn(true);
        when(indexFilesDao.getAll()).thenReturn(testData.indexFiles);
        String path = null;
        List<IndexFiles> indexes = testData.indexFiles;
        List<ResultItem> delList = new ArrayList<>();
        Set<FileObject> delfileset = new HashSet<>(); 
        Set<IndexFiles> ifs = new HashSet<>();
        ConsistentClean cl = new ConsistentClean(null, nodeConf, controlService);
        cl.setFileSystemDao(fileSystemDao);
        cl.setIndexFilesDao(indexFilesDao);
        cl.setSearchDao(searchDao);
        cl.extracted(delList, delfileset, path, indexes, ifs, true, false);
        assertEquals(2, delList.size());
        assertEquals(0, ifs.size());
        assertEquals(1, indexes.get(0).getFilelocations().size());
        assertEquals(1, indexes.get(1).getFilelocations().size());
        assertEquals(2, indexes.get(2).getFilelocations().size());
    }

    @Test
    public void consistentclean() throws Exception {
        var flset = testData.fileLocationSet;
        var fls = testData.fileLocations;
        FileObject fo = mock(FileObject.class);
        when(fileSystemDao.get(FsUtil.getFileObject(fls.get(0)))).thenReturn(fo);
        when(fileSystemDao.get(FsUtil.getFileObject(fls.get(3)))).thenReturn(fo);
        when(fileSystemDao.exists(fo)).thenReturn(true);
        when(indexFilesDao.getAll()).thenReturn(testData.indexFiles);
        String path = null;
        List<IndexFiles> indexes = testData.indexFiles;
        List<ResultItem> delList = new ArrayList<>();
        Set<FileObject> delfileset = new HashSet<>(); 
        Set<IndexFiles> ifs = new HashSet<>();
        ConsistentClean cl = new ConsistentClean(null, nodeConf, controlService);
        cl.setFileSystemDao(fileSystemDao);
        cl.setIndexFilesDao(indexFilesDao);
        cl.setSearchDao(searchDao);
        cl.extracted(delList, delfileset, path, indexes, ifs, true, true);
        assertEquals(2, delList.size());
        assertEquals(2, ifs.size());
        assertEquals(1, indexes.get(0).getFilelocations().size());
        assertEquals(0, indexes.get(1).getFilelocations().size());
        assertEquals(1, indexes.get(2).getFilelocations().size());
    }

}
