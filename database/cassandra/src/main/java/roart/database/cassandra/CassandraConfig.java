package roart.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;

public class CassandraConfig {

    private CqlSession session = null;

    private String port = null;
    
    private String configname = null;
    
    public CqlSession getSession() {
        return session;
    }

    public void setSession(CqlSession session) {
        this.session = session;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getConfigname() {
        return configname;
    }

    public void setConfigname(String configname) {
        this.configname = configname;
    }

}
