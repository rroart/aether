package roart.database.spring;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcClientAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jdbc.core.convert.DefaultDataAccessStrategy;
import org.springframework.data.jdbc.core.convert.DefaultJdbcTypeFactory;
import org.springframework.data.jdbc.core.convert.DelegatingDataAccessStrategy;
import org.springframework.data.jdbc.core.convert.InsertStrategyFactory;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.core.convert.MappingJdbcConverter;
import org.springframework.data.jdbc.core.convert.SqlGeneratorSource;
import org.springframework.data.jdbc.core.convert.SqlParametersFactory;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.H2Dialect;
import org.springframework.data.relational.core.dialect.PostgresDialect;
import org.springframework.data.relational.core.dialect.SqlServerDialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.event.RelationalEvent;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.RestController;

import roart.common.config.NodeConfig;
import roart.common.constants.QueueConstants;
import roart.database.DatabaseAbstractController;
import roart.database.DatabaseOperations;

@RestController
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class, 
        DataSourceTransactionManagerAutoConfiguration.class, 
        JdbcRepositoriesAutoConfiguration.class
})
@EnableDiscoveryClient
@ComponentScan(basePackages = "roart.database.spring")
public class SpringController extends DatabaseAbstractController {

    private static Logger log = LoggerFactory.getLogger(SpringController.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringController.class, args);
    }
    
    //@Autowired
    //public SpringIndexFilesWrapper springIndexFilesWrapper;
    @Autowired
    private ApplicationContext context;
    
    private final CollectingEventPublisher publisher = new CollectingEventPublisher();

    private DefaultDataAccessStrategy dataAccessStrategy;

    @Override
    protected DatabaseOperations createOperations(String configname, String configid, NodeConfig nodeConf) {
        String single = System.getProperty("springdata.single");
        if ("true".equals(single)) {
            return context.getBean(SpringIndexFilesWrapper.class);
            //return springIndexFilesWrapper;
        }
        
        RelationalMappingContext context = new JdbcMappingContext();
        DataSource dataSource = getDataSource(nodeConf);
        NamedParameterJdbcOperations operations = new NamedParameterJdbcTemplate(dataSource);

        Dialect dialect = null;
        dialect = PostgresDialect.INSTANCE;
        String driver = null;
        driver = nodeConf.getSpringdataDriver();
        
        if ("org.h2.Driver".equals(driver) || driver == null) {
            dialect = H2Dialect.INSTANCE;
        }
        if ("org.postgresql.Driver".equals(driver)) {
            dialect = PostgresDialect.INSTANCE;
        }
        if ("com.microsoft.sqlserver.jdbc.SQLServerDriver".equals(driver)) {
            dialect = SqlServerDialect.INSTANCE;
        }
        DelegatingDataAccessStrategy delegatingDataAccessStrategy = new DelegatingDataAccessStrategy();
        JdbcConverter converter = new MappingJdbcConverter(context, delegatingDataAccessStrategy,
                new JdbcCustomConversions(), new DefaultJdbcTypeFactory(operations.getJdbcOperations()));
        SqlGeneratorSource generatorSource = new SqlGeneratorSource(context, converter, dialect);
        SqlParametersFactory sqlParametersFactory = new SqlParametersFactory(context, converter);
        InsertStrategyFactory insertStrategyFactory = new InsertStrategyFactory(operations, dialect);

        this.dataAccessStrategy = new DefaultDataAccessStrategy(generatorSource, context, converter, operations,
                sqlParametersFactory, insertStrategyFactory);
        delegatingDataAccessStrategy.setDelegate(dataAccessStrategy);
        JdbcRepositoryFactory factory = new JdbcRepositoryFactory(dataAccessStrategy, context, converter,
                dialect, publisher, operations);

        IndexFilesRepository repo = factory.getRepository(IndexFilesRepository.class);

        FilesRepository filesrepo = factory.getRepository(FilesRepository.class);

        SpringConfiguration config = null;

        return new SpringIndexFilesWrapper(repo, filesrepo, nodeConf, config);
        //return springIndexFilesWrapper;
    }

    @Override
    public String getQueueName() {
        return QueueConstants.SPRING;
    }

    @Override
    public boolean useAppId() {
        return true;
    }
    
    static class CollectingEventPublisher implements ApplicationEventPublisher {

        List<RelationalEvent> events = new ArrayList<>();

        @Override
        public void publishEvent(Object o) {
            events.add((RelationalEvent) o);
        }
    }
    
    public DataSource getDataSource(NodeConfig nodeConf) {
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver"/*nodeConf.getSpringdataDriver()*/)
                .url(nodeConf.getSpringdataURL())
                .username(nodeConf.getSpringdataUsername())
                .password(nodeConf.getSpringdataPassword())
                .build();     
    }
    
    @Bean
    public NodeConfig getNodeConfig() {
        return new NodeConfig();
    }

    //@Bean
    ServletWebServerApplicationContext getMe() {
        return new ServletWebServerApplicationContext();
    }
}
