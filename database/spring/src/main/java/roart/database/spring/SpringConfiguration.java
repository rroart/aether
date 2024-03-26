package roart.database.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@ConditionalOnProperty(name = "springdata.single", havingValue = "true")
@Configuration
@EnableJdbcRepositories("roart.database.spring")
@EnableAutoConfiguration
public class SpringConfiguration {

    @Value("${spring.datasource.driverClassName}")
    private String driver;

    public String getDriver() {
        return driver;
    }

    @Value("${spring.datasource.url}")
    private String url;

    public String getUrl() {
        return url;
    }

}
