package roart.database;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.eq;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import roart.common.collections.MyQueue;
import roart.common.config.NodeConfig;
import roart.common.database.DatabaseConstructorResult;
import roart.common.database.DatabaseFileLocationResult;
import roart.common.database.DatabaseIndexFilesResult;
import roart.common.database.DatabaseLanguagesResult;
import roart.common.database.DatabaseMd5Result;
import roart.common.database.DatabaseResult;
import roart.common.model.FileLocation;
import roart.common.model.FilesDTO;
import roart.common.model.IndexFilesDTO;
import roart.common.model.Files;
import roart.common.model.IndexFiles;
import roart.common.queue.QueueElement;
import roart.common.util.FsUtil;
import roart.service.ControlService;
import roart.common.constants.EurekaConstants;
import roart.eureka.util.EurekaUtil;
import roart.common.model.FileObject;

public class IndexFilesDSTest {

    private static class TestIndexFilesDS extends IndexFilesDS {
        public TestIndexFilesDS(NodeConfig nodeConf, ControlService controlService) {
            super(nodeConf, controlService);
        }

        @Override
        public String getAppName() {
            return "TESTAPP";
        }

        @Override
        public String getQueueName() {
            return "test-queue";
        }
    }

    private NodeConfig nodeConf() {
        NodeConfig nc = new NodeConfig();
        return nc;
    }

    private ControlService controlService(NodeConfig nc) {
        ControlService cs = new ControlService(nc);
        cs.iconf = null;
        cs.setAppid("");
        return cs;
    }

    // TODO @Test
    public void testConstructorDestructorClearDrop() {
        NodeConfig nc = nodeConf();
        ControlService cs = controlService(nc);
        IndexFilesDS ds = new TestIndexFilesDS(nc, cs);

        DatabaseConstructorResult constructorResult = new DatabaseConstructorResult();
        constructorResult.error = "ok";

        try (MockedStatic<EurekaUtil> mocked = Mockito.mockStatic(EurekaUtil.class)) {
            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseConstructorResult.class), any(), eq(ds.getAppName()), eq(EurekaConstants.CONSTRUCTOR), eq(nc)))
                    .thenReturn(constructorResult);
            String r = ds.constructor();
            assertEquals("ok", r);

            constructorResult.error = "destroyed";
            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseConstructorResult.class), any(), eq(ds.getAppName()), eq(EurekaConstants.DESTRUCTOR), eq(nc)))
                    .thenReturn(constructorResult);
            assertEquals("destroyed", ds.destructor());

            constructorResult.error = "cleared";
            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseConstructorResult.class), any(), eq(ds.getAppName()), eq(EurekaConstants.CLEAR), eq(nc)))
                    .thenReturn(constructorResult);
            assertEquals("cleared", ds.clear());

            constructorResult.error = "dropped";
            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseConstructorResult.class), any(), eq(ds.getAppName()), eq(EurekaConstants.DROP), eq(nc)))
                    .thenReturn(constructorResult);
            assertEquals("dropped", ds.drop());
        }
    }

    // TODO @Test
    public void testGetByFilelocationAndGetMd5ByFilelocation() throws Exception {
        NodeConfig nc = nodeConf();
        ControlService cs = controlService(nc);
        IndexFilesDS ds = new TestIndexFilesDS(nc, cs);

        FileLocation fl = new FileLocation("node", "path/to/file");

        IndexFilesDTO dto = new IndexFilesDTO();
        dto.setMd5("md5-1");
        DatabaseIndexFilesResult indexResult = new DatabaseIndexFilesResult();
        indexResult.setIndexFiles(new IndexFilesDTO[] { dto });

        DatabaseMd5Result md5Result = new DatabaseMd5Result();
        md5Result.setMd5(new String[] { "md5-1" });

        try (MockedStatic<EurekaUtil> mocked = Mockito.mockStatic(EurekaUtil.class)) {
            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseIndexFilesResult.class), any(), eq(ds.getAppName()), eq(EurekaConstants.GETBYFILELOCATION), eq(nc)))
                    .thenReturn(indexResult);
            IndexFiles byFl = ds.getByFilelocation(fl);
            assertNotNull(byFl);
            assertEquals("md5-1", byFl.getMd5());

            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseMd5Result.class), any(), eq(ds.getAppName()), eq(EurekaConstants.GETMD5BYFILELOCATION), eq(nc)))
                    .thenReturn(md5Result);
            String md5 = ds.getMd5ByFilelocation(fl);
            assertEquals("md5-1", md5);
        }
    }

    // TODO @Test
    public void testGetByMd5AndGetFilelocationsByMd5() throws Exception {
        NodeConfig nc = nodeConf();
        ControlService cs = controlService(nc);
        IndexFilesDS ds = new TestIndexFilesDS(nc, cs);

        IndexFilesDTO dto = new IndexFilesDTO();
        dto.setMd5("m2");
        Map<String, IndexFilesDTO> map = new HashMap<>();
        map.put("m2", dto);
        DatabaseIndexFilesResult idxResult = new DatabaseIndexFilesResult();
        idxResult.setIndexFilesMap(map);

        DatabaseFileLocationResult flr = new DatabaseFileLocationResult();
        FileLocation f1 = new FileLocation("n", "o");
        flr.setFileLocation(new FileLocation[] { f1 });

        try (MockedStatic<EurekaUtil> mocked = Mockito.mockStatic(EurekaUtil.class)) {
            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseIndexFilesResult.class), any(), eq(ds.getAppName()), eq(EurekaConstants.GETBYMD5), eq(nc)))
                    .thenReturn(idxResult);
            IndexFiles byMd5 = ds.getByMd5("m2");
            assertNotNull(byMd5);
            assertEquals("m2", byMd5.getMd5());

            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseFileLocationResult.class), any(), eq(ds.getAppName()), eq(EurekaConstants.GETFILELOCATIONSBYMD5), eq(nc)))
                    .thenReturn(flr);
            Set<FileLocation> fls = ds.getFilelocationsByMd5("m2");
            assertNotNull(fls);
            assertEquals(1, fls.size());
            assertTrue(fls.contains(f1));
        }
    }

    // TODO @Test
    public void testGetAllAndGetAllFiles() throws Exception {
        NodeConfig nc = nodeConf();
        ControlService cs = controlService(nc);
        IndexFilesDS ds = new TestIndexFilesDS(nc, cs);

        IndexFilesDTO dto1 = new IndexFilesDTO(); dto1.setMd5("a");
        IndexFilesDTO dto2 = new IndexFilesDTO(); dto2.setMd5("b");
        DatabaseIndexFilesResult allResult = new DatabaseIndexFilesResult();
        allResult.setIndexFiles(new IndexFilesDTO[] { dto1, dto2 });

        FilesDTO f1 = new FilesDTO();
        FilesDTO f2 = new FilesDTO();
        DatabaseIndexFilesResult filesResult = new DatabaseIndexFilesResult();
        filesResult.setFiles(new FilesDTO[] { f1, f2 });

        try (MockedStatic<EurekaUtil> mocked = Mockito.mockStatic(EurekaUtil.class)) {
            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseIndexFilesResult.class), any(), eq(ds.getAppName()), eq(EurekaConstants.GETALL), eq(nc)))
                    .thenReturn(allResult);
            List<IndexFiles> all = ds.getAll();
            assertEquals(2, all.size());

            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseIndexFilesResult.class), any(), eq(ds.getAppName()), eq(EurekaConstants.GETALLFILES), eq(nc)))
                    .thenReturn(filesResult);
            List<Files> files = ds.getAllFiles();
            assertEquals(2, files.size());
        }
    }

    // TODO @Test
    public void testSaveFlushCloseCommitAndSimpleResults() throws Exception {
        NodeConfig nc = nodeConf();
        ControlService cs = controlService(nc);
        IndexFilesDS ds = new TestIndexFilesDS(nc, cs);

        DatabaseResult dbres = new DatabaseIndexFilesResult();
        DatabaseMd5Result md5res = new DatabaseMd5Result(); 
        md5res.setMd5(new String[] { "x","y" });
        DatabaseLanguagesResult langs = new DatabaseLanguagesResult(); langs.languages = new String[] { "en","no" };

        try (MockedStatic<EurekaUtil> mocked = Mockito.mockStatic(EurekaUtil.class)) {
            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseResult.class), any(), eq(ds.getAppName()), eq(EurekaConstants.SAVE), eq(nc)))
                    .thenReturn(dbres);
            Set<IndexFiles> saves = new HashSet<>();
            IndexFiles i = new IndexFiles("mdx");
            saves.add(i);
            ds.save(saves);

            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseResult.class), any(), eq(ds.getAppName()), eq(EurekaConstants.FLUSH), eq(nc)))
                    .thenReturn(dbres);
            ds.flush();

            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseResult.class), any(), eq(ds.getAppName()), eq(EurekaConstants.CLOSE), eq(nc)))
                    .thenReturn(dbres);
            ds.close();

            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseResult.class), any(), eq(ds.getAppName()), eq(EurekaConstants.COMMIT), eq(nc)))
                    .thenReturn(dbres);
            ds.commit();

            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseMd5Result.class), any(), eq(ds.getAppName()), eq(EurekaConstants.GETALLMD5), eq(nc)))
                    .thenReturn(md5res);
            Set<String> allmd5 = ds.getAllMd5();
            assertEquals(2, allmd5.size());

            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseLanguagesResult.class), any(), eq(ds.getAppName()), eq(EurekaConstants.GETLANGUAGES), eq(nc)))
                    .thenReturn(langs);
            Set<String> langsRes = ds.getLanguages();
            assertEquals(2, langsRes.size());
        }
    }

    // TODO @Test
    public void testDeleteAndGetByMd5MapAndMd5ByFilelocation() throws Exception {
        NodeConfig nc = nodeConf();
        ControlService cs = controlService(nc);
        IndexFilesDS ds = new TestIndexFilesDS(nc, cs);

        DatabaseResult dbres = new DatabaseIndexFilesResult();
        IndexFiles idx = new IndexFiles("delmd5");
        Files f = new Files();

        Map<String, IndexFilesDTO> map = new HashMap<>();
        IndexFilesDTO dto = new IndexFilesDTO(); dto.setMd5("m1");
        map.put("m1", dto);
        DatabaseIndexFilesResult idxRes = new DatabaseIndexFilesResult(); idxRes.setIndexFilesMap(map);

        DatabaseMd5Result md5res = new DatabaseMd5Result();
        Map<String, String> md5map = new HashMap<>(); md5map.put("file1", "m1");
        md5res.setMd5Map(md5map);

        try (MockedStatic<EurekaUtil> mocked = Mockito.mockStatic(EurekaUtil.class)) {
            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseResult.class), any(), eq(ds.getAppName()), eq(EurekaConstants.DELETE), eq(nc)))
                    .thenReturn(dbres);
            ds.delete(idx);
            ds.delete(f);

            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseIndexFilesResult.class), any(), eq(ds.getAppName()), eq(EurekaConstants.GETBYMD5), eq(nc)))
                    .thenReturn(idxRes);
            Set<String> md5s = new HashSet<>(); md5s.add("m1");
            Map<String, IndexFiles> ret = ds.getByMd5(md5s);
            assertTrue(ret.containsKey("m1"));
            assertEquals("m1", ret.get("m1").getMd5());

            mocked.when(() -> EurekaUtil.sendMe(eq(DatabaseMd5Result.class), any(), eq(ds.getAppName()), eq(EurekaConstants.GETMD5BYFILELOCATION), eq(nc)))
                    .thenReturn(md5res);
            Set<FileLocation> fls = new HashSet<>(); fls.add(new FileLocation("n","file1"));
            Map<String, String> mm = ds.getMd5ByFilelocation(fls);
            assertEquals(1, mm.size());
            assertEquals("m1", mm.get("file1"));

            // getBothByFilelocation currently returns null, ensure it doesn't throw
            List<Map> both = ds.getBothByFilelocation(fls);
            assertNull(both);
        }
    }

    // TODO @Test
    public void testQueuesSetAndQueueOperations() throws Exception {
        NodeConfig nc = nodeConf();
        ControlService cs = controlService(nc);
        IndexFilesDS ds = new TestIndexFilesDS(nc, cs) {
            @Override
            public boolean queueWithAppId() {
                return false; // simpler queue name
            }
        };

        ds.setQueue("some-queue");

        FileLocation fl = new FileLocation("n","obj");
        FileObject fo = FsUtil.getFileObject(fl);
        MyQueue queue = ds.getQueue(fo);
        assertNotNull(queue);

        QueueElement element = new QueueElement();
        element.setFileObject(fo);
        Set<String> md5s = new HashSet<>(); md5s.add("mxyz");
        ds.getByMd5Queue(element, md5s);
        assertEquals(roart.common.constants.OperationConstants.GETBYMD5, element.getOpid());
        assertNotNull(element.getDatabaseMd5Param());

        QueueElement element2 = new QueueElement();
        element2.setFileObject(fo);
        Set<FileLocation> fls = new HashSet<>(); fls.add(fl);
        ds.getMd5ByFilelocationQueue(element2, fls);
        assertEquals(roart.common.constants.OperationConstants.GETMD5BYFILELOCATION, element2.getOpid());
        assertNotNull(element2.getDatabaseFileLocationParam());
    }

}
