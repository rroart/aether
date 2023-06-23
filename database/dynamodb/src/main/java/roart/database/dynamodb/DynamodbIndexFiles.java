package roart.database.dynamodb;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.io.IOException;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.database.DatabaseConstructorParam;
import roart.common.model.FileLocation;
import roart.common.model.Files;
import roart.common.model.IndexFiles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import roart.common.util.FsUtil;
import roart.common.util.JsonUtil;

public class DynamodbIndexFiles {

    private Logger log = LoggerFactory.getLogger(DynamodbIndexFiles.class);

    private DynamodbConfig config;

    public void amain() {
        DynamoDbClient ddb = client; //new DynamoDbClient(client);
        /* Read the name from command args */

        List<KeySchemaElement> indexfileskeyelements = new ArrayList<>();
        List<KeySchemaElement> fileskeyelements = new ArrayList<>();
        KeySchemaElement md5keyelement = KeySchemaElement.builder().attributeName("md5").keyType(KeyType.HASH).build();
        KeySchemaElement fileskeyelement = KeySchemaElement.builder().attributeName("filename").keyType(KeyType.HASH).build();
        indexfileskeyelements.add(md5keyelement);
        fileskeyelements.add(fileskeyelement);
        //fileskeyelements.add(new KeySchemaElement("md5", KeyType.RANGE));

        List<AttributeDefinition> indexfilesattributes = new ArrayList<>();
        List<AttributeDefinition> filesattributes = new ArrayList<>();

        AttributeDefinition indexed = AttributeDefinition.builder().attributeName(indexedq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition timestamp = AttributeDefinition.builder().attributeName(timestampq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition timeindex = AttributeDefinition.builder().attributeName(timeindexq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition timeclass = AttributeDefinition.builder().attributeName(timeclassq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition classification = AttributeDefinition.builder().attributeName(classificationq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition convertsw = AttributeDefinition.builder().attributeName(convertswq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition converttime = AttributeDefinition.builder().attributeName(converttimeq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition failed = AttributeDefinition.builder().attributeName(failedq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition failedreason = AttributeDefinition.builder().attributeName(failedreasonq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition timeoutreason = AttributeDefinition.builder().attributeName(timeoutreasonq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition noindexreason = AttributeDefinition.builder().attributeName(noindexreasonq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition language = AttributeDefinition.builder().attributeName(languageq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition isbn = AttributeDefinition.builder().attributeName(isbnq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition created = AttributeDefinition.builder().attributeName(createdq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition checked = AttributeDefinition.builder().attributeName(checkedq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition node = AttributeDefinition.builder().attributeName(nodeq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition filename = AttributeDefinition.builder().attributeName(filenameq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition filelocation = AttributeDefinition.builder().attributeName(filelocationq).attributeType(ScalarAttributeType.S).build();
        AttributeDefinition md5 = AttributeDefinition.builder().attributeName(md5q).attributeType(ScalarAttributeType.S).build();

        /*
        indexfilesattributes.add(timestamp);
        indexfilesattributes.add(timeindex);
        indexfilesattributes.add(timeclass);
        indexfilesattributes.add(classification);
        indexfilesattributes.add(convertsw);
        indexfilesattributes.add(converttime);
        indexfilesattributes.add(failed);
        indexfilesattributes.add(failedreason);
        indexfilesattributes.add(timeoutreason);
        indexfilesattributes.add(noindexreason);
        indexfilesattributes.add(language);
        indexfilesattributes.add(node);
        indexfilesattributes.add(filename);
        indexfilesattributes.add(filelocation);
         */

        filesattributes.add(filename);
        //filesattributes.add(md5);
        indexfilesattributes.add(md5);

        System.out.format(
                "Creating table \"%s\" with a simple primary key: \"Name\".\n",
                getFiles());

        ProvisionedThroughput ptIndex = ProvisionedThroughput.builder()
                .readCapacityUnits(1L)
                .writeCapacityUnits(1L).build();
        GlobalSecondaryIndex md5Index = GlobalSecondaryIndex.builder()
                .indexName("Md5Index") 
                .provisionedThroughput(ptIndex) 
                .keySchema(KeySchemaElement.builder()
                        .attributeName("md5")
                        .keyType(KeyType.HASH).build())                 //Sort key
                .projection(Projection.builder()
                        .projectionType("KEYS_ONLY").build()).build();
        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(getFiles())
                .attributeDefinitions(filesattributes)
                .keySchema(fileskeyelements)
                //.globalSecondaryIndexes(md5Index)
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(10L).writeCapacityUnits(10L).build())
                        .build();


        //final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
        System.out.println("cl " + client);
        /*
        try {
            Table table = ddb.getTable(getFiles());
            table.delete();
            table.waitForDelete();
            //DeleteTableResult res = client.deleteTable(getFiles());
            //Thread.sleep(10000);
            //System.out.println("res0 " + res.toString());
        } catch (InterruptedException e) {
            System.err.println("res5 " + e.getMessage());            
        }
         */
        log.info("y1");
        //printEndpoints(client);
        createTable(request, ddb, getFiles());
        log.info("y12");

        CreateTableRequest request2 = CreateTableRequest.builder()
                .tableName(getIndexFiles())
                .attributeDefinitions(indexfilesattributes)
                .keySchema(indexfileskeyelements)
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(10L).writeCapacityUnits(10L).build()).build();
        log.info("y123");
        createTable(request2, ddb, getIndexFiles());
        log.info("y1234");
        //filesTable = ddb.getTable(getFiles());
        //indexTable = ddb.getTable(getIndexFiles());
        /*
        try {
            Table table = ddb.getTable(getIndexFiles());
            table.delete();
            table.waitForDelete();
            //DeleteTableResult res = client.deleteTable(getIndexFiles());
            //Thread.sleep(10000);
            //System.out.println("res2 " + res.toString());
        } catch (InterruptedException e) {
            System.err.println("res6 " + e.getMessage());            
        }
         */
        /*
        try {
            boolean created = TableUtils.createTableIfNotExists(client, request2);
            System.out.println("res1 " + created);
            TableUtils.waitUntilActive(client, getIndexFiles());
            TableUtils.waitUntilExists(client, getIndexFiles());
            Table table = ddb.getTable(getIndexFiles());
            System.out.println("res1 " + table);
            //CreateTableResult result = client.createTable(request2);
            //System.out.println("res3 " + result.getTableDescription().getTableName());
        } catch (AmazonServiceException e) {
            System.err.println("res8 " + e.getErrorMessage());
            System.exit(1);
        } catch (TableNeverTransitionedToStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         */
        for (String res : ddb .listTables().tableNames()) {
            System.out.println("r1 " + res);;
        }
        for (String res : client.listTables() .tableNames()) {
            System.out.println("r2 " + res);;

        }
        System.out.println("Done!");
        log.info("yend");


    }

    private void printEndpoints(DynamoDbClient cli) {
        try {
        DescribeEndpointsRequest describeEndpointsRequest = DescribeEndpointsRequest.builder().build();
        DescribeEndpointsResponse res00 = cli.describeEndpoints(describeEndpointsRequest);
        for (Endpoint endpoint : res00.endpoints()) {
            log.info("endp " + endpoint.address());
        }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
    }

    private void createTable(CreateTableRequest request, DynamoDbClient ddb, String tableName) {
        try {
            //AmazonDynamoDB ddb = new AmazonDynamoDB(client);
            CreateTableResponse response = ddb.createTable(request);
            System.out.println("res1 " + response);
            DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build();
            DynamoDbWaiter dbWaiter = ddb.waiter();
            WaiterResponse<DescribeTableResponse> waiterResponse = dbWaiter.waitUntilTableExists(tableRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);
            String newTable = response.tableDescription().tableName();
            //return newTable;

            //Table table = ddb.getTable(tableName);
            //System.out.println("res1 " + table);
            DescribeTableRequest describeTableRequest = DescribeTableRequest.builder().tableName(tableName).build();
            TableDescription tableDescription = client.describeTable(describeTableRequest).table();
            System.out.println("Table Description: " + tableDescription);
            //CreateTableResult result = client.createTable(request);
            //System.out.println("res1 " + result.getTableDescription().getTableName());
        } catch (ResourceInUseException e) {
            log.error(Constants.EXCEPTION, e);
            e.printStackTrace();
        } catch (AwsServiceException e) {
            log.error(Constants.EXCEPTION, e);
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }    
    // column families
    private final String indexcf = "if";
    private final String flcf = "fl";
    private final String filescf = "fi";

    // column qualifiers
    private final String md5q = "md5";
    private final String indexedq = "indexed";
    private final String timestampq = "timestamp";
    private final String timeindexq = "timeindex";
    private final String timeclassq = "timeclass";
    private final String classificationq = "classification";
    private final String convertswq = "convertsw";
    private final String converttimeq = "converttime";
    private final String failedq = "failed";
    private final String failedreasonq = "failedreason";
    private final String timeoutreasonq = "timeoutreason";
    private final String noindexreasonq = "noindexreason";
    private final String languageq = "language";
    private final String isbnq = "isbn";
    private final String createdq = "created";
    private final String checkedq = "checked";
    private final String nodeq = "node";
    private final String filenameq = "filename";
    private final String filelocationq = "filelocation";

    static final String TABLE_FILES_NAME = "files";
    static final String TABLE_INDEXFILES_NAME = "indexfiles";
    DynamoDbClient client;
    //Table filesTable;
    //Table indexTable;
    //DynamoDB ddb;
    public DynamodbIndexFiles(DynamoDbClient ddb, String nodename, NodeConfig nodeConf) {
        config = new DynamodbConfig();
        String port = "8000";
        String host = "localhost";
        if (ddb != null) {
            this.client = ddb;
        } else {
            this.client = DynamoDbClient.builder().build();
            //printEndpoints(this.client);
            log.info("Nodeconf {}", nodeConf);
            if (nodeConf != null) {
                port = nodeConf.getDynamodbPort();
                host = nodeConf.getDynamodbHost();
                log.info("Host port {} {}", host, port);
                //new EndpointConfiguration();
                URI endpointConfiguration = null;
                try {
                    endpointConfiguration = new URI("http://" + host + ":" + port);
                } catch (Exception e) {
                    log.error(Constants.ERROR, e);
                }
                //this.client = AmazonDynamoDBClientBuilder.standard().endpointConfiguration(endpointConfiguration).
                //printEndpoints(this.client);
                AwsBasicCredentials credentials = AwsBasicCredentials.create(nodeConf.getS3AccessKey(), nodeConf.getS3SecretKey());
                this.client = DynamoDbClient.builder()
                    .endpointOverride(endpointConfiguration)
                    //.pathStyleAccessEnabled(true)
                    //.clientConfiguration(clientConfiguration)
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                        .build();
                //printEndpoints(this.client);
                log.info("Client connected {}", this.client);
            }
        }
        /*
	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().endpointConfiguration(
	        new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
	        .build(); 
	ddb = AmazonDynamoDBClientBuilder.standard().endpointConfiguration(
	        new AwsClientBuilder.EndpointConfiguration("http://" + host + ":" + port, "us-west-2"))
	        .build(); 
         */
        //client.setEndpoint("http://" + host + ":" + port);
        config.setClient(this.client);
        config.setNodename(nodename);
        config.setTableprefix(nodeConf.getDynamodbTableprefix());

        amain();
        log.info("After main");

        //ddb = new DynamoDB(new AmazonDynamoDBClient( 
        //     new ProfileCredentialsProvider()));  
        //DynamoDbClient ddb2 = new DynamoDbClient(this.client);
        //indexTable = ddb2 .getTable(getIndexFiles());
        //filesTable = ddb2.getTable(getFiles());
        log.info("After");
    }

    /*
    public void setClient(AmazonDynamoDB client) {
        this.client = client;
    }
     */

    public DynamoDbClient getClient() {
        return client;
    }

    public void put(IndexFiles ifile) throws Exception {
        //HTable /*Interface*/ filesTable = new HTable(conf, "index");
        Map<String, AttributeValue> item = new HashMap<>();
        //item.primaryKey(md5q, ifile.getMd5());
        Map<String,AttributeValue/*Update*/> updatedvalues = new HashMap<>();
        updatedvalues.put(md5q, AttributeValue.builder().s(ifile.getMd5()).build());
        if (ifile.getIndexed() != null) {
            updatedvalues.put(indexedq, AttributeValue.builder().s("" + ifile.getIndexed()).build()); //, AttributeAction.PUT));
        }
        if (ifile.getTimestamp() != null && !ifile.getTimestamp().isEmpty()) {
            updatedvalues.put(timestampq, AttributeValue.builder().s(ifile.getTimestamp()).build());
        }
        if (ifile.getTimeindex() != null && !ifile.getTimeindex().isEmpty()) {
            updatedvalues.put(timeindexq, AttributeValue.builder().s(ifile.getTimeindex()).build());
        }
        if (ifile.getTimeclass() != null && !ifile.getTimeclass().isEmpty()) {
            updatedvalues.put(timeclassq, AttributeValue.builder().s(ifile.getTimeclass()).build());
        }
        if (ifile.getClassification() != null && !ifile.getClassification().isEmpty()) {
            updatedvalues.put(classificationq, AttributeValue.builder().s(ifile.getClassification()).build());
        }
        if (ifile.getConvertsw() != null && !ifile.getConvertsw().isEmpty()) {
            updatedvalues.put(convertswq, AttributeValue.builder().s(ifile.getConvertsw()).build());
        }
        if (ifile.getConverttime() != null && !ifile.getConverttime().isEmpty()) {
            updatedvalues.put(converttimeq, AttributeValue.builder().s(ifile.getConverttime()).build());
        }
        if (ifile.getFailed() != null) {
            updatedvalues.put(failedq, AttributeValue.builder().s("" + ifile.getFailed()).build());
        }
        if (ifile.getFailedreason() != null && !ifile.getFailedreason().isEmpty()) {
            updatedvalues.put(failedreasonq, AttributeValue.builder().s(ifile.getFailedreason()).build());
        }
        if (ifile.getTimeoutreason() != null && !ifile.getTimeoutreason().isEmpty()) {
            updatedvalues.put(timeoutreasonq, AttributeValue.builder().s(ifile.getTimeoutreason()).build());
        }
        if (ifile.getNoindexreason() != null && !ifile.getNoindexreason().isEmpty()) {
            updatedvalues.put(noindexreasonq, AttributeValue.builder().s(ifile.getNoindexreason()).build());
        }
        if (ifile.getLanguage() != null && !ifile.getLanguage().isEmpty()) {
            updatedvalues.put(languageq, AttributeValue.builder().s(ifile.getLanguage()).build());
        }
        if (ifile.getIsbn() != null && !ifile.getIsbn().isEmpty()) {
            updatedvalues.put(isbnq, AttributeValue.builder().s(ifile.getIsbn()).build());
        }
        if (ifile.getCreated() != null && !ifile.getCreated().isEmpty()) {
            updatedvalues.put(createdq, AttributeValue.builder().s(ifile.getCreated()).build());
        }
        if (ifile.getChecked() != null && !ifile.getChecked().isEmpty()) {
            updatedvalues.put(checkedq, AttributeValue.builder().s(ifile.getChecked()).build());
        }
        if (ifile.getFilelocations() != null && !ifile.getFilelocations().isEmpty()) {
            //updatedvalues.put(filelocationq, AttributeValue.builder().s(new ArrayList(ifile.getFilelocations())));              
            String str = null;
            System.out.println("put floc");
            str = new FileLocationConverter().convert(new ArrayList(ifile.getFilelocations()));
            updatedvalues.put(filelocationq, AttributeValue.builder().s(str).build());	        
        }
        //item.with
        //log.info("hbase " + ifile.getMd5());
        int i = -1;
        /*
	    for (FileLocation file : ifile.getFilelocations()) {
		i++;
		String filename = getFile(file);
		//log.info("hbase " + filename);
		//item.addColumn(flcf, "q" + i), filename));
	    }
	    i++;
         */
        // now, delete the rest (or we would get some old historic content)
        // TODO
        for (; i < ifile.getMaxfilelocations(); i++) {
            //Delete d = new Delete(ifile.getMd5()));
            //d.addColumns(flcf, "q" + i)); // yes this deletes, was previously deleteColumns
            //log.info("Hbase delete q" + i);
            //indexTable.delete(d);
        }

        put(ifile.getMd5(), ifile.getFilelocations());
        //client.putI;
        log.info("grrrr"+updatedvalues.toString());
        client.putItem(PutItemRequest.builder().tableName(getIndexFiles()).item(updatedvalues).build());

        // or if still to slow, simply get current (old) indexfiles
        Set<FileLocation> curfls = getFilelocationsByMd5(ifile.getMd5());
        curfls.removeAll(ifile.getFilelocations());

        // delete the files no longer associated to the md5
        for (FileLocation fl : curfls) {
            String name = fl.toString();
            log.info("Dynamodb delete {}", name);
                try {
                    System.out.println("Attempting a conditional delete...");
                    client.deleteItem(DeleteItemRequest.builder().key(Map.of(filenameq, AttributeValue.builder().s(fl.toString()).build())).build());
                    System.out.println("DeleteItem succeeded");
                }
                catch (Exception e) {
                    System.err.println("Unable to delete item: " + fl);
                    System.err.println(e.getMessage());
                }
            //Delete d = new Delete(name));
            //filesTable.delete(d);
        }

    }

    // is this handling other nodes
    // plus get set of existing, remove new from that, delete the rest.

    public void put(String md5, Set<FileLocation> files) throws Exception {
        //HTable /*Interface*/ filesTable = new HTable(conf, "index");
        for (FileLocation file : files) {
            System.out.println("files put " + md5 + " " + file);
            String filename = getFile(file);
            Map<String,AttributeValue/*Update*/> updatedvalues = new HashMap<>();
            updatedvalues.put(filenameq, AttributeValue.builder().s(filename).build()); //, AttributeAction.PUT));
            updatedvalues.put(md5q, AttributeValue.builder().s(md5).build());
            //Put put = new Put(filename));
            //put.addColumn(filescf, md5q, md5));
            //filesTable.put(put);
            client.putItem(PutItemRequest.builder().tableName(getFiles()).item(updatedvalues).build());
        }
    }

    public IndexFiles get(Map<String, AttributeValue> item) {
        String md5 = item.get(md5q).s();
        IndexFiles ifile = new IndexFiles(md5);
        //ifile.setMd5(bytesToString(index.getValue(indexcf, md5q)));
        ifile.setIndexed(new Boolean(itemgets(item.get(indexedq))));
        ifile.setTimeindex(itemgets(item.get(timeindexq)));
        ifile.setTimestamp(itemgets(item.get(timestampq)));
        ifile.setTimeclass(itemgets(item.get(timeclassq)));
        ifile.setClassification(itemgets(item.get(classificationq)));
        ifile.setConvertsw(itemgets(item.get(convertswq)));
        ifile.setConverttime(itemgets(item.get(converttimeq)));
        ifile.setFailed(Integer.valueOf(itemgets(item.get(failedq)) != null ? itemgets(item.get(failedq)) : "0"));
        ifile.setFailedreason(itemgets(item.get(failedreasonq)));
        ifile.setTimeoutreason(itemgets(item.get(timeoutreasonq)));
        ifile.setNoindexreason(itemgets(item.get(noindexreasonq)));
        ifile.setLanguage(itemgets(item.get(languageq)));
        ifile.setIsbn(itemgets(item.get(isbnq)));
        ifile.setCreated(itemgets(item.get(createdq)));
        ifile.setChecked(itemgets(item.get(checkedq)));
        log.info("get fl " + item.get(filelocationq).getClass().getName() + " " + itemgets(item.get(filelocationq)).getClass().getName() + " " + item.get(filelocationq));
        ifile.setFilelocations(item.get(filelocationq) != null ? new HashSet<>(convert(itemgets(item.get(filelocationq)))) : new HashSet<>());
        Set<FileLocation> fls;
        /*
        try {
        fls = getFilelocationsByMd5(md5);
        ifile.setFilelocations(fls);
    } catch (Exception e) {
        log.error(Constants.EXCEPTION, e);
    }
         */
        ifile.setUnchanged();
        return ifile;
    }

    public Files getFiles(Map<String, AttributeValue> item) {
        Files ifile = new Files();
        ifile.setFilename(itemgets(item.get(filenameq)));
        ifile.setMd5(itemgets(item.get(md5q)));
        return ifile;
    }

    private String itemgets(AttributeValue attributeValue) {
        if (attributeValue != null) {
            return attributeValue.s();
        }
        return null;
    }

    private List<FileLocation> convert(String list) {
        List<FileLocation> listnew = new ArrayList<>();
        for (Object str : List.of(list)) {
            LinkedHashMap[] map;
            if (str instanceof String string) {
                map = JsonUtil.convertnostrip(string, LinkedHashMap[].class);
            } else {
                map = new LinkedHashMap[] { (LinkedHashMap) str };
            }
            FileLocation[] fl = JsonUtil.convert(map, FileLocation[].class);
            listnew.addAll(Arrays.asList(fl));
        }
        return listnew;
    }

    public Map<String, IndexFiles> get(Set<String> md5s) {
        Map<String, IndexFiles> indexFilesMap = new HashMap<>();
        for (String md5 : md5s) {
            IndexFiles indexFile = get(md5);
            if (indexFile != null) {
                indexFilesMap.put(md5, indexFile);
            }
        }
        return indexFilesMap;
    }

    public IndexFiles get(String md5) {
        Map<String, AttributeValue> keyMap = Map.of(md5q, AttributeValue.builder().s(md5).build());
        Map<String, AttributeValue> item = client.getItem(GetItemRequest.builder().tableName(getIndexFiles()).key(keyMap).build()).item();
        if (item == null || item.isEmpty()) {
            return null;
        }
        return get(item);
    }

    public IndexFiles getIndexByFilelocation(FileLocation fl) {
        String md5 = getMd5ByFilelocation(fl);
        if (md5.length() == 0) {
            return null;
        }
        return get(md5);
    }

    public String getMd5ByFilelocation(FileLocation fl) {
        String name = getFile(fl);
        log.info("NAME"+name);
        GetItemResponse item = client.getItem(GetItemRequest.builder().tableName(getFiles()).key(Map.of(filenameq, AttributeValue.builder().s(name).build())).build());
        if (item == null || item.item().isEmpty()) {
            return null;
        }
        log.info("" + item.item());
        return item.item().get(md5q).s();
    }

    public Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
        log.info("Search md5 {}", md5);
        Set<FileLocation> flset = new HashSet<>();

        Map<String, AttributeValue> expressionAttributeValues = Map.of(":md5", AttributeValue.builder().s(md5).build());

        ScanRequest scanSpec = ScanRequest.builder()
                .tableName(getFiles())
                .filterExpression("md5 = :md5")
                .expressionAttributeValues(expressionAttributeValues)
                .build();
        /*
        QuerySpec spec = new QuerySpec()
                .keyConditionExpression("#md5 = :md5")
                .nameMap(new NameMap()
                        .with("#md5", "md5"))
                .valueMap(new ValueMap()
                        .string(":md5", md5));
        RangeKeyCondition rangeKeyCondition = new RangeKeyCondition("md5")
                .eq(md5);
        spec = new QuerySpec()
                .rangeKeyCondition(rangeKeyCondition);
*/
        // filesTable.query(spec)
        //ItemCollection<QueryOutcome> items = filesTable.query(spec);
        ScanResponse scans = client.scan(scanSpec);
        //System.out.println("cnt " + items.getTotalCount());
        //List<Map<String, AttributeValue>> items = client.query(queryRequest ).getItems();
        /*
        for (Item item : items) {
            FileLocation fl = getFileLocation(item.getString(filelocationq));
            if (fl != null) {
                flset.add(fl);
            }            
        }
        */
        System.out.println("h0");
        for (Map<String, AttributeValue> item : scans.items()) {
            System.out.println("h1");
            FileLocation fl = getFileLocation(item.get(filenameq).s());
            System.out.println("h1 " + fl);
            if (fl != null) {
                flset.add(fl);
            }            
        }
        System.out.println("h2");
        return flset;
    }

    public List<IndexFiles> getAll() throws Exception {
        List<IndexFiles> retlist = new ArrayList<>();
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(getIndexFiles()).build();

        ScanResponse result = client.scan(scanRequest);
        List<Map<String, AttributeValue>> list = result.items();
        for (Map<String, AttributeValue> itemMap : list){
            Map<String, AttributeValue> item = new HashMap<>();
            for (Entry<String, AttributeValue> entry : itemMap.entrySet()) {
                if ( entry.getKey().equals(filelocationq)) {
                    String value = entry.getValue().s();
                    List<FileLocation> unconverted = new FileLocationConverter().unconvert(value);
                    item.put(entry.getKey(), entry.getValue());
                } else {
                    item.put(entry.getKey(), entry.getValue());
                }
            }
            retlist.add(get(item));
        }
        return retlist;
    }

    public List<Files> getAllFiles() throws Exception {
        List<Files> retlist = new ArrayList<>();
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(getFiles()).build();

        ScanResponse result = client.scan(scanRequest);
        List<Map<String, AttributeValue>> list = result.items();
        for (Map<String, AttributeValue> itemMap : list){
            Map<String, AttributeValue> item = new HashMap<>();
            for (Entry<String, AttributeValue> entry : itemMap.entrySet()) {
                if ( entry.getKey().equals(filelocationq)) {
                    String value = entry.getValue().s();
                    List<FileLocation> unconverted = new FileLocationConverter().unconvert(value);
                    item.put(entry.getKey(), entry.getValue());
                } else {
                    item.put(entry.getKey(), entry.getValue());
                }
            }
            retlist.add(getFiles(item));
        }
        return retlist;
    }

    public IndexFiles ensureExistenceNot(String md5) throws Exception {
        return null;
    }

    public String getFile(FileLocation fl) {
        if (fl == null) {
            return null;
        }
        return fl.toString();
    }

    public FileLocation getFileLocation(String fl) {
        if (fl == null) {
            return null;
        }
        return FsUtil.getFileLocation(fl);
    }

    private String convertNullNot(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    private String convert0(String s) {
        if (s == null) {
            return "0";
        }
        return s;
    }

    private String bytesToString(String bytes) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes);
    }

    public void flush() throws Exception {
        //
    }

    public void commit() throws Exception {        
    }

    public void close() throws Exception {
        log.info("closing db");
    }

    public Set<String> getAllMd5() throws Exception {
        Set<String> md5s = new HashSet<>();
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(getIndexFiles()).build();

        ScanResponse result = client.scan(scanRequest);
        for (Map<String, AttributeValue> itemMap : result.items()){

            md5s.add(itemMap.get(md5q).s());
        }
        return md5s;
    }

    public Set<String> getLanguages() throws Exception {
        Set<String> languages = new HashSet<String>();
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(getIndexFiles()).build();

        ScanResponse result = client.scan(scanRequest);
        for (Map<String, AttributeValue> itemMap : result.items()){

            languages.add(itemgets(itemMap.get(languageq)));
        }
        return languages;
    }

    public void delete(IndexFiles index) throws Exception {
            client.deleteItem(DeleteItemRequest.builder().tableName(getIndexFiles()).key(Map.of(md5q, AttributeValue.builder().s(index.getMd5()).build())).build());

        Set<FileLocation> curfls = getFilelocationsByMd5(index.getMd5());
        System.out.println("curfls " + curfls.size());
        //curfls.removeAll(index.getFilelocations());
        //System.out.println("curfls " + curfls.size());

        // delete the files no longer associated to the md5
        for (FileLocation fl : curfls) {
            String name = fl.toString();
            deleteFile(name);
        }
    }

    public void delete(Files index) throws Exception {
        deleteFile(index.getFilename());
    }

    public void deleteFile(String filename) throws Exception {
        client.deleteItem(DeleteItemRequest.builder().tableName(getFiles()).key(Map.of(filenameq, AttributeValue.builder().s(filename).build())).build());
    }

    public void destroy() throws Exception {
        //config.getConnection().close();
    }

    static public class FileLocationConverter /*implements DynamoDBTypeConverter<String, List<FileLocation>>*/ {
/*
        @Override
        public String convert(FileLocation object) {
            FileLocation itemDimensions = (FileLocation) object;
            String dimension = null;
            try {
                if (itemDimensions != null) {
                    dimension = String.format("%s x %s x %s", itemDimensions.getLength(), itemDimensions.getHeight(),
                        itemDimensions.getThickness());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return dimension;
        }

        @Override
        public FileLocation unconvert(String s) {

            FileLocation itemDimension = new FileLocation();
            try {
                if (s != null && s.length() != 0) {
                    String[] data = s.split("x");
                    itemDimension.setLength(data[0].trim());
                    itemDimension.setHeight(data[1].trim());
                    itemDimension.setThickness(data[2].trim());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return itemDimension;
        }
        */
        //@Override
        public String convert(List<FileLocation> objects) {
            //Jackson object mapper
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String objectsString = objectMapper.writeValueAsString(objects);
                return objectsString;
            } catch (JsonProcessingException e) {
                //do something
            }
            return null;
        }

        //@Override
        public List<FileLocation> unconvert(String objectsString) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List objects = objectMapper.readValue(objectsString, new TypeReference<List<Object>>(){});
                return objects;
            } catch (JsonParseException e) {
                //do something
            } catch (JsonMappingException e) {
                //do something
            } catch (IOException e) {
                //do something
            }
            return null;
        }
       }
    public String getIndexFiles() {
        return config.getTableprefix() + "_" + TABLE_INDEXFILES_NAME;
    }
public String getFiles() {
    return config.getTableprefix() + "_" + TABLE_FILES_NAME;
}

public void clear(DatabaseConstructorParam param) {
    try {
    clear(getIndexFiles(), "md5");
    clear(getFiles(), "filename");
} catch (Exception e) {
    log.info(Constants.EXCEPTION, e);
}
}

public void drop(DatabaseConstructorParam param) {
    client.deleteTable(DeleteTableRequest.builder().tableName(getIndexFiles()).build());
    client.deleteTable(DeleteTableRequest.builder().tableName(getFiles()).build());
}

private void clear(String table, String hashKeyName) throws Exception {
    ScanRequest spec = ScanRequest.builder().tableName(table).build();
    ScanResponse items = client.scan(spec);
    for(Map<String, AttributeValue> item : items.items()) {
        //String hashKey = item.get(hashKeyName);
        //PrimaryKey key = new PrimaryKey(hashKeyName, hashKey);
        client.deleteItem(DeleteItemRequest.builder().tableName(table).key(Map.of(hashKeyName, item.get(hashKeyName))).build());
        System.out.printf("Deleted item with key: %s\n", hashKeyName);
    }
}

}

