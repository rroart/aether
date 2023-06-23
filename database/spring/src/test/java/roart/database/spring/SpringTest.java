package roart.database.spring;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SpringController.class,
properties = { "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:/tmp/h2",
        //:testdb?user=sa&password=password
        "spring.jpa.generate-ddl=true",
        "spring.jpa.hibernate.ddl-auto=create"
})
public class SpringTest {
    @Autowired
    private IndexFilesRepository repo;

    @Autowired
    private FilesRepository filesrepo;

    @Test
    public void test() {
        Index i = new Index();
        i.setMd5("42");
        i.setFilenames(Set.of("fn"));
        repo.createH2();
        filesrepo.createH2();
        repo.save(i);
        Index i2 = new Index();
        i2.setMd5("42");
        i2.setFilenames(Set.of("fn"));
        repo.save(i);
        repo.save(i);
    }
}