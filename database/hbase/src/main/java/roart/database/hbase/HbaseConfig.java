package roart.database.hbase;

import org.apache.hadoop.hbase.client.Connection;

public class HbaseConfig {

    private Connection connection = null;

    private String configname = null;
    
    private String tableprefix = null;
    
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getConfigname() {
        return configname;
    }

    public void setConfigname(String configname) {
        this.configname = configname;
    }

    public String getTableprefix() {
        return tableprefix;
    }

    public void setTableprefix(String tableprefix) {
        this.tableprefix = tableprefix;
    }

}
