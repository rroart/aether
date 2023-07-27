package roart.database.dynamodb;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamodbConfig {

    private String configname = null;

    private DynamoDbClient client;
    
    private String tableprefix;
    
    public String getConfigname() {
        return configname;
    }

    public void setConfigname(String configname) {
        this.configname = configname;
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
