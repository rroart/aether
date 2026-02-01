package roart.database.dynamodb;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;

public class LocalDbCreationRule implements BeforeAllCallback, AfterAllCallback {
    private DynamoDBProxyServer server;
    private final String port = "8000";
 
    public LocalDbCreationRule() {
        System.setProperty("sqlite4java.library.path", "native-libs");
    }
 
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        server = ServerRunner.createServerFromCommandLineArgs(
          new String[] {"-inMemory", "-port", port});
        server.start();
    }
 
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        this.stopUnchecked(server);
    }
 
    protected void stopUnchecked(DynamoDBProxyServer dynamoDbServer) {
        try {
            if (dynamoDbServer != null) {
                dynamoDbServer.stop();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getEndpoint() {
        return "http://localhost:" + port;
    }
}