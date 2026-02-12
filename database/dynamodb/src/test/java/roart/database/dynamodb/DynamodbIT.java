package roart.database.dynamodb;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.Test;

import roart.common.model.FileLocation;
import roart.common.model.IndexFilesDTO;
import roart.common.model.IndexFilesUtil;
import roart.database.dynamodb.DynamodbIndexFiles;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DynamodbIT {
    @RegisterExtension
    public static LocalDbCreationRule dynamoDB = new LocalDbCreationRule();

    DynamoDbClient ddb;
    DynamodbIndexFiles indexfiles;

    @BeforeEach
    public void setup() {
        // Build an SDK v2 client pointing at the local DynamoDB instance started by the extension
        String endpoint = dynamoDB.getEndpoint();
        ddb = DynamoDbClient.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("x", "x")))
                .region(Region.US_WEST_2)
                .build();

        System.out.println("here");
        indexfiles = new DynamodbIndexFiles(ddb, "localhost", null);
    }

    private void deleteTables() {
        try {
            DeleteTableRequest deleteTableRequest = DeleteTableRequest.builder()
                    .tableName(DynamodbIndexFiles.TABLE_FILES_NAME)
                    .build();
            ddb.deleteTable(deleteTableRequest);
            System.out.println("deleted " + DynamodbIndexFiles.TABLE_FILES_NAME);
        } catch (DynamoDbException e) {
            System.out.println("could not delete " + DynamodbIndexFiles.TABLE_FILES_NAME + ": " + e.getMessage());
        } catch (Exception e) {
            System.out.println("unexpected error deleting " + DynamodbIndexFiles.TABLE_FILES_NAME + ": " + e.getMessage());
        }

        try {
            DeleteTableRequest deleteTableRequest2 = DeleteTableRequest.builder()
                    .tableName(DynamodbIndexFiles.TABLE_INDEXFILES_NAME)
                    .build();
            ddb.deleteTable(deleteTableRequest2);
            System.out.println("deleted " + DynamodbIndexFiles.TABLE_INDEXFILES_NAME);
        } catch (DynamoDbException e) {
            System.out.println("could not delete " + DynamodbIndexFiles.TABLE_INDEXFILES_NAME + ": " + e.getMessage());
        } catch (Exception e) {
            System.out.println("unexpected error deleting " + DynamodbIndexFiles.TABLE_INDEXFILES_NAME + ": " + e.getMessage());
        }
    }

    // TODO classnotfound @Test
    public void test() throws Exception {
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
    }

    @AfterEach
    public void shutdown() {
        deleteTables();
        if (ddb != null) ddb.close();
        System.out.println("shutdown");
    }
}