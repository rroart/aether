package roart.database.dynamodb;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamodbConfig {

    private String nodename = null;

    private DynamoDbClient client;
    
    private String tableprefix;
    
    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public void setClient(DynamoDbClient client) {
        this.client = client;
    }

    public DynamoDbClient getClient() {
        return client;
    }

    public String getTableprefix() {
        return tableprefix;
    }

    public void setTableprefix(String tableprefix) {
        this.tableprefix = tableprefix;
    }
}
