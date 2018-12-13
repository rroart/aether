package roart.database;

import com.datastax.driver.core.Session;

public class CassandraConfig {

    private Session session = null;

    private String port = null;
    
    private String nodename = null;
    
    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

}
