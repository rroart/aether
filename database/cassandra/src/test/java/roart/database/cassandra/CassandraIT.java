package roart.database.cassandra;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.datastax.oss.driver.api.core.CqlSession;

import roart.common.model.FileLocation;
import roart.common.model.IndexFilesDTO;
import roart.common.model.IndexFilesUtil;
import roart.database.cassandra.CassandraIndexFiles;

public class CassandraIT {

    CqlSession session;
    CassandraIndexFiles indexfiles;

    @BeforeEach
    public void setup() throws ConfigurationException, TTransportException, IOException, InterruptedException {
        System.out.println("st1");
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        System.out.println("st2");
        session = EmbeddedCassandraServerHelper.getSession();
        System.out.println("st3");
        indexfiles = new CassandraIndexFiles(session, "localhost", null);
        //indexfiles.setSession(session);
    }

    @Test
    public void test() throws Exception {
        try {
            IndexFilesDTO indexFiles;
            indexFiles = IndexFilesUtil.getSample();
            indexfiles.put(indexFiles);
            Set<FileLocation> locs = indexfiles.getFilelocationsByMd5("1234");
            assertEquals(2,locs.size());
            IndexFilesDTO indexFilesGet = indexfiles.get("1234");
            System.out.println("ifget " + indexFilesGet);
            IndexFilesUtil.changeSample(indexFiles);
            indexfiles.put(indexFiles);
            locs = indexfiles.getFilelocationsByMd5("1234");
            assertEquals(1,locs.size());
            List<IndexFilesDTO> list = indexfiles.getAll();
            assertEquals(1, list.size());
            Set<String> md5s = indexfiles.getAllMd5();
            assertEquals(1, md5s.size());
            String md5 = indexfiles.getMd5ByFilelocation(new FileLocation("localhost", "/tmp/t"));
            System.out.println("md5 " + md5);
            indexfiles.delete(indexFiles);
            indexFilesGet = indexfiles.get("1234");
            System.out.println("ifget " + indexFilesGet);
            md5 = indexfiles.getMd5ByFilelocation(new FileLocation("localhost", "/tmp/t"));
            System.out.println("md5 " + md5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void shutdown() {
        System.out.println("stopped0");
        try {
            session.close();
            //EmbeddedCassandraServerHelper.
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("stopped");
    }
}
