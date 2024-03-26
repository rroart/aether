package roart.database.spring;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootTest(classes = SpringController.class,
properties = { //"spring.datasource.driverClassName=org.h2.Driver",
        //"spring.datasource.url=jdbc:h2:/tmp/h2",
        "springdata.single=true",
        //"log4j.logger.org.springframework.jdbc.core=TRACE",
        //"logging.level.org.springframework.jdbc.core=TRACE",
        //"logging.level.root=TRACE",
        "spring.datasource.driverClassName=org.postgresql.Driver",
        //"spring.datasource.url=jdbc:postgresql://192.168.0.116:5432/aether?user=aether&password=password",
        "spring.datasource.url=jdbc:postgresql://localhost:5432/aether?user=aether&password=password",
        //"spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver",
        //       "spring.datasource.url=jdbc:sqlserver://192.168.122.163;databaseName=aether;integratedSecurity=true;encrypt=true;trustServerCertificate=true",
        //"spring.datasource.url=jdbc:sqlserver://192.168.122.163;databaseName=aether;encrypt=false",
        //"spring.datasource.username=aether",
        //"spring.datasource.password=Passw0rd"
        //:testdb?user=sa&password=password
        //"spring.jpa.generate-ddl=true",
        //"spring.jpa.hibernate.ddl-auto=create"
})
public class SpringTest {
    private Logger log = LoggerFactory.getLogger(SpringTest.class);
    @Autowired
    private IndexFilesRepository repo;

    @Autowired
    private FilesRepository filesrepo;

    @Bean 
    ServletWebServerFactory servletWebServerFactory(){
        return new TomcatServletWebServerFactory();
    }
    @Test
    public void test() {
        Index i = new Index();
        i.setMd5("42");
        i.setFilenames(Set.of("fn"));
        repo.deleteAll();
        filesrepo.deleteAll();
        //repo.createH2();
        //filesrepo.createH2();
        //repo.save(i);
        try {
            //Thread.sleep(10000);
            System.out.println("iiiiiii" + i);
            log.info("iiiiiii" + i);
            repo.save(i);
            System.out.println("iiiiiii" + i);
            log.info("iiiiiii" + i);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("eeeeeeeee");
        }
        Index i2 = new Index();
        i2.setMd5("43");
        i2.setFilenames(Set.of("fn"));
        //i.setVersion(2);
        try {
            repo.save(i);
            repo.save(i);
        } catch (Exception e) {
            log.error("Ex", e);
            e.printStackTrace();
            System.out.println("eeeeeeeee");
        }      
        Files f = new Files();
        f.setFilename("fn");
        f.setMd5("42");
        filesrepo.save(f);
    }

}
