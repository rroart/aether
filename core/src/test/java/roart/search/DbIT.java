package roart.search;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import roart.common.config.ConfigConstants;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.model.FileLocation;
import roart.common.model.IndexFiles;
import roart.database.IndexFilesDS;
import roart.database.IndexFilesDao;
import roart.service.ControlService;

public class DbIT {

    @Test
    public void testSaveGetDelete() throws Exception {
        // minimal NodeConfig
        MyConfig.createMapsNot();
        NodeConfig nodeConf = new NodeConfig();
        nodeConf.configValueMap = new HashMap<>();
        nodeConf.deflt = new HashMap<>();
        // choose hibernate db implementation by setting the flag (factory will pick it)
        nodeConf.configValueMap.put(ConfigConstants.DATABASEHIBERNATE, Boolean.TRUE);

        ControlService controlService = new ControlService(nodeConf);

        // create dao and then inject an in-memory IndexFilesDS to avoid external services
        IndexFilesDao dao = new IndexFilesDao(nodeConf, controlService);

        InMemoryIndexFilesDS mem = new InMemoryIndexFilesDS(nodeConf, controlService);

        // use reflection to replace the private indexFiles field on dao
        Field f = IndexFilesDao.class.getDeclaredField("indexFiles");
        f.setAccessible(true);
        f.set(dao, mem);

        // create indexfiles, add to dao, commit and verify persisted through stub
        String md5 = "md5-test-123";
        IndexFiles index = new IndexFiles(md5);
        index.addFile("localhost", "/tmp/testfile1");
        index.setCreated("" + System.currentTimeMillis());
        dao.add(index);

        Assertions.assertTrue(dao.dirty() > 0, "Dao should be dirty after add");

        dao.commit(); // this will call mem.save(...) via our injected stub

        IndexFiles loaded = dao.getByMd5(md5);
        Assertions.assertNotNull(loaded, "Loaded index should not be null");
        Assertions.assertEquals(md5, loaded.getMd5());
        Set<FileLocation> fls = loaded.getFilelocations();
        Assertions.assertFalse(fls.isEmpty(), "Filelocations should be present");

        // now delete and commit
        dao.delete(loaded);
        dao.commit();

        IndexFiles afterDelete = dao.getExistingByMd5(md5);
        Assertions.assertNull(afterDelete, "Index should be deleted from underlying store");
    }

    // In-memory stub implementation that stores IndexFiles in a map
    static class InMemoryIndexFilesDS extends IndexFilesDS {

        private ConcurrentMap<String, IndexFiles> store = new ConcurrentHashMap<>();

        public InMemoryIndexFilesDS(NodeConfig nodeConf, ControlService controlService) {
            super(nodeConf, controlService);
        }

        @Override
        public String getAppName() {
            return "inmemory";
        }

        @Override
        public String getQueueName() {
            return "inmemory";
        }

        @Override
        public IndexFiles getByMd5(String md5) throws Exception {
            if (md5 == null) return null;
            IndexFiles i = store.get(md5);
            if (i == null) return null;
            // return a copy to simulate DB read
            IndexFiles copy = new IndexFiles(i.getMd5());
            copy.setCreated(i.getCreated());
            copy.setChecked(i.getChecked());
            copy.setClassification(i.getClassification());
            copy.setConvertsw(i.getConvertsw());
            copy.setConverttime(i.getConverttime());
            copy.setFailedreason(i.getFailedreason());
            copy.setLanguage(i.getLanguage());
            copy.setMimetype(i.getMimetype());
            copy.setSize(i.getSize());
            copy.setConvertsize(i.getConvertsize());
            copy.setVersion(i.getVersion());
            copy.setTimeclass(i.getTimeclass());
            copy.setTimeindex(i.getTimeindex());
            copy.setTimestamp(i.getTimestamp());
            copy.setIndexed(i.getIndexed());
            copy.setFilelocations(new HashSet<>(i.getFilelocations()));
            return copy;
        }

        @Override
        public Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
            IndexFiles i = store.get(md5);
            if (i == null) return null;
            return i.getFilelocations();
        }

        @Override
        public java.util.List<IndexFiles> getAll() throws Exception {
            return java.util.List.copyOf(store.values());
        }

        @Override
        public java.util.List<roart.common.model.Files> getAllFiles() throws Exception {
            return java.util.List.of();
        }

        @Override
        public void save(Set<IndexFiles> saves) throws Exception {
            if (saves == null) return;
            for (IndexFiles i : saves) {
                store.put(i.getMd5(), i);
            }
        }

        @Override
        public void delete(IndexFiles index) throws Exception {
            if (index != null) {
                store.remove(index.getMd5());
            }
        }

        @Override
        public void flush() throws Exception {
            // no-op
        }

        @Override
        public void commit() throws Exception {
            // no-op for in-memory
        }

        @Override
        public void close() throws Exception {
            store.clear();
        }

        @Override
        public Set<String> getAllMd5() throws Exception {
            return new HashSet<>(store.keySet());
        }

        @Override
        public Set<String> getLanguages() throws Exception {
            return new HashSet<>();
        }

        // other methods are left as-is (inherited)
    }

}