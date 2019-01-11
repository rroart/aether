package roart.database.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

public class DynamodbConfig {

    private String nodename = null;

    private AmazonDynamoDB client;
    
    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public void setClient(AmazonDynamoDB client) {
        this.client = client;
    }

    public AmazonDynamoDB getClient() {
        return client;
    }
}
