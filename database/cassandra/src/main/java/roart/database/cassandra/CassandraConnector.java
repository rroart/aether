package roart.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;

public class CassandraConnector {
    
    private CqlSession session;
 
    public void connect(String node, Integer port) {
    }
 
    public CqlSession getSession() {
        return this.session;
    }
 
    public void close() {
        session.close();
        //cluster.close();
    }

    public void createKeyspace(
            String keyspaceName, String replicationStrategy, int replicationFactor) {
            StringBuilder sb = 
              new StringBuilder("CREATE KEYSPACE IF NOT EXISTS ")
                .append(keyspaceName).append(" WITH replication = {")
                .append("'class':'").append(replicationStrategy)
                .append("','replication_factor':").append(replicationFactor)
                .append("};");
                   
              String query = sb.toString();
              session.execute(query);
          }
    
}