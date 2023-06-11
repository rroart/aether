package roart.database.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {

    @Value("${spring.datasource.driverClassName}")
    private String driver;

    public String getDriver() {
        return driver;
    }

}
