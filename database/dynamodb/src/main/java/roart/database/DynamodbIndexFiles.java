package roart.database;

import java.util.HashSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.io.IOException;

import roart.config.NodeConfig;
import roart.model.IndexFiles;
import roart.model.FileLocation;
import roart.util.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.RangeKeyCondition;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableResult;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.amazonaws.services.dynamodbv2.util.TableUtils.TableNeverTransitionedToStateException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class DynamodbIndexFiles {

    private Logger log = LoggerFactory.getLogger(DynamodbIndexFiles.class);

    private DynamodbConfig config;

    public void amain() {
        DynamoDB ddb = new DynamoDB(client);
        /* Read the name from command args */

        List<KeySchemaElement> indexfileskeyelements = new ArrayList<>();
        List<KeySchemaElement> fileskeyelements = new ArrayList<>();
        KeySchemaElement md5keyelement = new KeySchemaElement("md5", KeyType.HASH);
        KeySchemaElement fileskeyelement = new KeySchemaElement("filename", KeyType.HASH);
        indexfileskeyelements.add(md5keyelement);
        fileskeyelements.add(fileskeyelement);
        //fileskeyelements.add(new KeySchemaElement("md5", KeyType.RANGE));

        List<AttributeDefinition> indexfilesattributes = new ArrayList<>();
        List<AttributeDefinition> filesattributes = new ArrayList<>();

        AttributeDefinition indexed = new AttributeDefinition(indexedq, ScalarAttributeType.S);
        AttributeDefinition timestamp = new AttributeDefinition(timestampq, ScalarAttributeType.S);
        AttributeDefinition timeindex = new AttributeDefinition(timeindexq, ScalarAttributeType.S);
        AttributeDefinition timeclass = new AttributeDefinition(timeclassq, ScalarAttributeType.S);
        AttributeDefinition classification = new AttributeDefinition(classificationq, ScalarAttributeType.S);
        AttributeDefinition convertsw = new AttributeDefinition(convertswq, ScalarAttributeType.S);
        AttributeDefinition converttime = new AttributeDefinition(converttimeq, ScalarAttributeType.S);
        AttributeDefinition failed = new AttributeDefinition(failedq, ScalarAttributeType.S);
        AttributeDefinition failedreason = new AttributeDefinition(failedreasonq, ScalarAttributeType.S);
        AttributeDefinition timeoutreason = new AttributeDefinition(timeoutreasonq, ScalarAttributeType.S);
        AttributeDefinition noindexreason = new AttributeDefinition(noindexreasonq, ScalarAttributeType.S);
        AttributeDefinition language = new AttributeDefinition(languageq, ScalarAttributeType.S);
        AttributeDefinition node = new AttributeDefinition(nodeq, ScalarAttributeType.S);
        AttributeDefinition filename = new AttributeDefinition(filenameq, ScalarAttributeType.S);
        AttributeDefinition filelocation = new AttributeDefinition(filelocationq, ScalarAttributeType.S);
        AttributeDefinition md5 = new AttributeDefinition(md5q, ScalarAttributeType.S);

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
                TABLE_FILES_NAME);

        ProvisionedThroughput ptIndex = new ProvisionedThroughput()
                .withReadCapacityUnits(1L)
                .withWriteCapacityUnits(1L);
        GlobalSecondaryIndex md5Index = new GlobalSecondaryIndex() 
                .withIndexName("Md5Index") 
                .withProvisionedThroughput(ptIndex) 
                .withKeySchema(new KeySchemaElement()  
                        .withAttributeName("md5")  
                        .withKeyType(KeyType.HASH))                 //Sort key 
                .withProjection(new Projection() 
                        .withProjectionType("KEYS_ONLY"));        
        CreateTableRequest request = new CreateTableRequest()
                .withTableName(TABLE_FILES_NAME)
                .withAttributeDefinitions(filesattributes)
                .withKeySchema(fileskeyelements)
                //.withGlobalSecondaryIndexes(md5Index)
                .withProvisionedThroughput(new ProvisionedThroughput(
                        new Long(10), new Long(10)));


        //final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
        System.out.println("cl " + client);
        /*
        try {
            Table table = ddb.getTable(TABLE_FILES_NAME);
            table.delete();
            table.waitForDelete();
            //DeleteTableResult res = client.deleteTable(TABLE_FILES_NAME);
            //Thread.sleep(10000);
            //System.out.println("res0 " + res.toString());
        } catch (InterruptedException e) {
            System.err.println("res5 " + e.getMessage());            
        }
         */
        createTable(request, ddb, TABLE_FILES_NAME);

        CreateTableRequest request2 = new CreateTableRequest()
                .withTableName(TABLE_INDEXFILES_NAME)
                .withAttributeDefinitions(indexfilesattributes)
                .withKeySchema(indexfileskeyelements)
                .withProvisionedThroughput(new ProvisionedThroughput(
                        new Long(10), new Long(10)));
        createTable(request2, ddb, TABLE_INDEXFILES_NAME);
        //filesTable = ddb.getTable(TABLE_FILES_NAME);
        //indexTable = ddb.getTable(TABLE_INDEXFILES_NAME);
        /*
        try {
            Table table = ddb.getTable(TABLE_INDEXFILES_NAME);
            table.delete();
            table.waitForDelete();
            //DeleteTableResult res = client.deleteTable(TABLE_INDEXFILES_NAME);
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
            TableUtils.waitUntilActive(client, TABLE_INDEXFILES_NAME);
            TableUtils.waitUntilExists(client, TABLE_INDEXFILES_NAME);
            Table table = ddb.getTable(TABLE_INDEXFILES_NAME);
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
        for (Table res : ddb.listTables()) {
            System.out.println("r1 " + res.getTableName());;
        }
        for (String res : client.listTables().getTableNames()) {
            System.out.println("r2 " + res);;

        }
        System.out.println("Done!");


    }

    private void createTable(CreateTableRequest request, DynamoDB ddb, String tableName) {
        try {
            //AmazonDynamoDB ddb = new AmazonDynamoDB(client);
            boolean created = TableUtils.createTableIfNotExists(client, request);
            System.out.println("res1 " + created);
            TableUtils.waitUntilActive(client, tableName);
            TableUtils.waitUntilExists(client, tableName);
            Table table = ddb.getTable(tableName);
            System.out.println("res1 " + table);
            DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(tableName);
            TableDescription tableDescription = client.describeTable(describeTableRequest).getTable();
            System.out.println("Table Description: " + tableDescription);
            //CreateTableResult result = client.createTable(request);
            //System.out.println("res1 " + result.getTableDescription().getTableName());
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            System.err.println("res7 " + e.getErrorMessage());
            System.exit(1);
        } catch (TableNeverTransitionedToStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
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
    private final String nodeq = "node";
    private final String filenameq = "filename";
    private final String filelocationq = "filelocation";

    static final String TABLE_FILES_NAME = "files";
    static final String TABLE_INDEXFILES_NAME = "indexfiles";
    AmazonDynamoDB client;
    Table filesTable;   
    Table indexTable;
    //DynamoDB ddb;
    public DynamodbIndexFiles(AmazonDynamoDB ddb, String nodename, NodeConfig nodeConf) {
        config = new DynamodbConfig();
        String port = "8000";
        String host = "localhost";
        if (ddb != null) {
            client = ddb;
        } else {
            client = AmazonDynamoDBClientBuilder.defaultClient();
            if (nodeConf != null) {
                port = nodeConf.getDynamodbPort();
                host = nodeConf.getDynamodbHost();
                EndpointConfiguration endpointConfiguration = new EndpointConfiguration(host, port);
                client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(endpointConfiguration).build();
            }
        }
        /*
	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
	        new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
	        .build(); 
	ddb = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
	        new AwsClientBuilder.EndpointConfiguration("http://" + host + ":" + port, "us-west-2"))
	        .build(); 
         */
        //client.setEndpoint("http://" + host + ":" + port);
        config.setClient(client);
        config.setNodename(nodename);
        amain();

        //ddb = new DynamoDB(new AmazonDynamoDBClient( 
        //     new ProfileCredentialsProvider()));  
        DynamoDB ddb2 = new DynamoDB(client);
        indexTable = ddb2.getTable(TABLE_INDEXFILES_NAME);
        filesTable = ddb2.getTable(TABLE_FILES_NAME);
    }

    /*
    public void setClient(AmazonDynamoDB client) {
        this.client = client;
    }
     */

    public AmazonDynamoDB getClient() {
        return client;
    }

    public void put(IndexFiles ifile) throws Exception {
        //HTable /*Interface*/ filesTable = new HTable(conf, "index");
        Item item = new Item();
        item.withPrimaryKey(md5q, ifile.getMd5());
        Map<String,AttributeValue/*Update*/> updatedvalues = new HashMap<>();
        updatedvalues.put(md5q, new AttributeValue(ifile.getMd5()));
        if (ifile.getIndexed() != null) {
            updatedvalues.put(indexedq, new AttributeValue("" + ifile.getIndexed())); //, AttributeAction.PUT));
        }
        if (ifile.getTimestamp() != null) {
            updatedvalues.put(timestampq, new AttributeValue(ifile.getTimestamp()));
        }
        if (ifile.getTimeindex() != null) {
            updatedvalues.put(timeindexq, new AttributeValue(ifile.getTimeindex()));
        }
        if (ifile.getTimeclass() != null) {
            updatedvalues.put(timeclassq, new AttributeValue(ifile.getTimeclass()));
        }
        if (ifile.getClassification() != null) {
            updatedvalues.put(classificationq, new AttributeValue(ifile.getClassification()));
        }
        if (ifile.getConvertsw() != null) {
            updatedvalues.put(convertswq, new AttributeValue(ifile.getConvertsw()));
        }
        if (ifile.getConverttime() != null) {
            updatedvalues.put(converttimeq, new AttributeValue(ifile.getConverttime()));
        }
        if (ifile.getFailed() != null) {
            updatedvalues.put(failedq, new AttributeValue("" + ifile.getFailed()));
        }
        if (ifile.getFailedreason() != null) {
            updatedvalues.put(failedreasonq, new AttributeValue(ifile.getFailedreason()));
        }
        if (ifile.getTimeoutreason() != null) {
            updatedvalues.put(timeoutreasonq, new AttributeValue(ifile.getTimeoutreason()));
        }
        if (ifile.getNoindexreason() != null) {
            updatedvalues.put(noindexreasonq, new AttributeValue(ifile.getNoindexreason()));
        }
        if (ifile.getLanguage() != null) {
            updatedvalues.put(languageq, new AttributeValue(ifile.getLanguage()));
        }
        if (ifile.getFilelocations() != null && !ifile.getFilelocations().isEmpty()) {
            //updatedvalues.put(filelocationq, new AttributeValue(new ArrayList(ifile.getFilelocations())));              
            String str = null;
            System.out.println("put floc");
            str = new FileLocationConverter().convert(new ArrayList(ifile.getFilelocations()));
            updatedvalues.put(filelocationq, new AttributeValue(str));	        
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
        for (; i < ifile.getMaxfilelocations(); i++) {
            //Delete d = new Delete(ifile.getMd5()));
            //d.addColumns(flcf, "q" + i)); // yes this deletes, was previously deleteColumns
            //log.info("Hbase delete q" + i);
            //indexTable.delete(d);
        }

        put(ifile.getMd5(), ifile.getFilelocations());
        //client.putI;
        client.putItem(TABLE_INDEXFILES_NAME, updatedvalues);

        // or if still to slow, simply get current (old) indexfiles
        Set<FileLocation> curfls = getFilelocationsByMd5(ifile.getMd5());
        curfls.removeAll(ifile.getFilelocations());

        // delete the files no longer associated to the md5
        for (FileLocation fl : curfls) {
            if (!fl.isLocal(config.getNodename())) {
                continue;
            }
            String name = fl.toString();
            log.info("Dynamodb delete {}", name);
                try {
                    System.out.println("Attempting a conditional delete...");
                    filesTable.deleteItem(filenameq, fl.toString());
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
            updatedvalues.put(filenameq, new AttributeValue(filename)); //, AttributeAction.PUT));
            updatedvalues.put(md5q, new AttributeValue(md5));
            //Put put = new Put(filename));
            //put.addColumn(filescf, md5q, md5));
            //filesTable.put(put);
            client.putItem(TABLE_FILES_NAME, updatedvalues);
        }
    }

    public IndexFiles get(Item item) {
        String md5 = item.getString(md5q);
        IndexFiles ifile = new IndexFiles(md5);
        //ifile.setMd5(bytesToString(index.getValue(indexcf, md5q)));
        ifile.setIndexed(new Boolean(item.getString(indexedq)));
        ifile.setTimeindex(item.getString(timeindexq));
        ifile.setTimestamp(item.getString(timestampq));
        ifile.setTimeclass(item.getString(timeclassq));
        ifile.setClassification(item.getString(classificationq));
        ifile.setConvertsw(item.getString(convertswq));
        ifile.setConverttime(item.getString(converttimeq));
        ifile.setFailed(Integer.valueOf(item.getString(failedq) != null ? item.getString(failedq) : "0"));
        ifile.setFailedreason(item.getString(failedreasonq));
        ifile.setTimeoutreason(item.getString(timeoutreasonq));
        ifile.setNoindexreason(item.getString(noindexreasonq));
        ifile.setLanguage(item.getString(languageq));
        System.out.println("get fl " + item.getList(filelocationq) + " " + item.getString(filelocationq));
        ifile.setFilelocations(item.getList(filelocationq) != null ? new HashSet<>(item.getList(filelocationq)) : new HashSet<>());
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
        Item item = indexTable.getItem(md5q, md5);
        if (item == null) {
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
        Item item = filesTable.getItem(filenameq, name);
        if (item == null) {
            return null;
        }
        return item.getString(md5q);
    }

    public Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
        System.out.println("smd5 " + md5);
        Set<FileLocation> flset = new HashSet<>();
        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("#md5 = :md5")
                .withNameMap(new NameMap()
                        .with("#md5", "md5"))
                .withValueMap(new ValueMap()
                        .withString(":md5", md5));
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("#md5 = :md5")
                .withNameMap(new NameMap()
                        .with("#md5", "md5"))
                .withValueMap(new ValueMap()
                        .withString(":md5", md5));
        RangeKeyCondition rangeKeyCondition = new RangeKeyCondition("md5")
                .eq(md5);

        spec = new QuerySpec()
                .withRangeKeyCondition(rangeKeyCondition);
        // filesTable.query(spec)
        //ItemCollection<QueryOutcome> items = filesTable.query(spec);
        ItemCollection<ScanOutcome> scans = filesTable.scan(scanSpec);
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
        for (Item item : scans) {
            System.out.println("h1");
            FileLocation fl = getFileLocation(item.getString(filenameq));
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
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(TABLE_INDEXFILES_NAME);

        ScanResult result = client.scan(scanRequest);
        List<Map<String, AttributeValue>> list = result.getItems();
        for (Map<String, AttributeValue> itemMap : list){
            Item item = new Item();
            for (Entry<String, AttributeValue> entry : itemMap.entrySet()) {
                System.out.println("key " + entry.getKey());
                //item.with(entry.getKey(), entry.getValue());
            }
            retlist.add(get(item));
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
        return new FileLocation(fl, config.getNodename(), null);
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
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(TABLE_INDEXFILES_NAME);

        ScanResult result = client.scan(scanRequest);
        for (Map<String, AttributeValue> itemMap : result.getItems()){

            md5s.add(itemMap.get(md5q).getS());
        }
        return md5s;
    }

    public Set<String> getLanguages() throws Exception {
        Set<String> languages = new HashSet<String>();
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(TABLE_INDEXFILES_NAME);

        ScanResult result = client.scan(scanRequest);
        for (Map<String, AttributeValue> itemMap : result.getItems()){

            languages.add(itemMap.get(languageq).getS());
        }
        return languages;
    }

    public void delete(IndexFiles index) throws Exception {
            indexTable.deleteItem(md5q, index.getMd5());

        Set<FileLocation> curfls = getFilelocationsByMd5(index.getMd5());
        System.out.println("curfls " + curfls.size());
        //curfls.removeAll(index.getFilelocations());
        //System.out.println("curfls " + curfls.size());

        // delete the files no longer associated to the md5
        for (FileLocation fl : curfls) {
            String name = fl.toString();
            filesTable.deleteItem(filenameq, name);
        }
    }

    public void destroy() throws Exception {
        //config.getConnection().close();
    }

    static public class FileLocationConverter implements DynamoDBTypeConverter<String, List<FileLocation>> {
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
        @Override
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

        @Override
        public List<FileLocation> unconvert(String objectsString) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<FileLocation> objects = objectMapper.readValue(objectsString, new TypeReference<List<Object>>(){});
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
}

