package roart.database.hbase;

import org.apache.hadoop.hbase.client.Connection;

public class HbaseConfig {

    private Connection connection = null;

    private String nodename = null;
    
    private String tableprefix = null;
    
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public String getTableprefix() {
        return tableprefix;
    }

    public void setTableprefix(String tableprefix) {
        this.tableprefix = tableprefix;
    }

}
