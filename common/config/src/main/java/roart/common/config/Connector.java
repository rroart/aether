package roart.common.config;

public class Connector {

    private String name;
    
    private boolean eureka = true;
    
    private String connection;

    public Connector() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEureka() {
        return eureka;
    }

    public void setEureka(boolean eureka) {
        this.eureka = eureka;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }
    
}
