package roart.database.dynamodb;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.Test;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

import roart.common.model.FileLocation;
import roart.common.model.IndexFiles;
import roart.common.model.IndexFilesUtil;
import roart.database.dynamodb.DynamodbIndexFiles;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamodbIT {
    @RegisterExtension
    public static LocalDbCreationRule dynamoDB = new LocalDbCreationRule();

    DynamoDbClient ddb;
    DynamodbIndexFiles indexfiles;

    @BeforeEach
    public void setup() {
        ddb = null; //DynamoDBEmbedded.create().amazonDynamoDB();
        System.out.println("here");
        indexfiles = new DynamodbIndexFiles(ddb, "localhost", null);
        //indexfiles.setClient(ddb);
    }

    private void deleteTables() {
        DeleteTableRequest deleteTableRequest = new DeleteTableRequest().withTableName(DynamodbIndexFiles.TABLE_FILES_NAME);
        boolean status = false; // TableUtils.deleteTableIfExists(indexfiles.client, deleteTableRequest);
        System.out.println("res1 " + status);
        DeleteTableRequest deleteTableRequest2 = new DeleteTableRequest().withTableName(DynamodbIndexFiles.TABLE_INDEXFILES_NAME);
        boolean status2 = false; //TableUtils.deleteTableIfExists(indexfiles.client, deleteTableRequest2);
        System.out.println("res1 " + status2);
    }

    @Test
    public void test() throws Exception {
        IndexFiles indexFiles;
        indexFiles = IndexFilesUtil.getSample();
        indexfiles.put(indexFiles);
        Set<FileLocation> locs = indexfiles.getFilelocationsByMd5("1234");
        assertEquals(2,locs.size());
        IndexFiles indexFilesGet = indexfiles.get("1234");
        System.out.println("ifget " + indexFilesGet);
        IndexFilesUtil.changeSample(indexFiles);
        indexfiles.put(indexFiles);
        locs = indexfiles.getFilelocationsByMd5("1234");
        assertEquals(1,locs.size());
        List<IndexFiles> list = indexfiles.getAll();
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
    }

    @AfterEach
    public void shutdown() {
        deleteTables();
        //ddb.shutdown();
        System.out.println("shutdown");
        //dynamoDB.stopUnchecked(null);
    }
}

