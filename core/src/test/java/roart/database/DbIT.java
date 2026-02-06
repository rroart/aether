package roart.database;

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
import roart.common.config.MyXMLConfig;
import roart.common.config.NodeConfig;
import roart.common.model.FileLocation;
import roart.common.model.IndexFiles;
import roart.database.IndexFilesDS;
import roart.database.IndexFilesDao;
import roart.service.ControlService;

public class DbIT {

    private NodeConfig nodeConf = MyXMLConfig.getConfigInstance(getConfigfile());

    @Test
    public void testSaveGetDelete() throws Exception {
        // minimal NodeConfig
        //MyConfig.createMapsNot();
        //NodeConfig nodeConf = new NodeConfig();
        //nodeConf.configValueMap = new HashMap<>();
        //nodeConf.deflt = new HashMap<>();
        // choose hibernate db implementation by setting the flag (factory will pick it)
        nodeConf.configValueMap.put(ConfigConstants.DATABASEHIBERNATE, Boolean.TRUE);

        ControlService controlService = new ControlService(nodeConf);

        // create dao and then inject an in-memory IndexFilesDS to avoid external services
        IndexFilesDao dao = new IndexFilesDao(nodeConf, controlService);
        Assertions.assertTrue(dao.works(), "Not working");
        
        // create indexfiles, add to dao, commit and verify persisted through stub
        String md5 = "md5-test-123";
        IndexFiles index = new IndexFiles(md5);
        index.addFile("localhost", "/tmp/testfile1");
        index.setCreated("" + System.currentTimeMillis());
        dao.add(index);

        Assertions.assertTrue(dao.dirty() > 0, "Dao should be dirty after add");

        dao.commit(); // this will call mem.save(...) via our injected stub

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Exception should not be thrown: " + e.getMessage());
        }
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