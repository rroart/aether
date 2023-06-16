package roart.database.spring;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilesRepository extends CrudRepository<Files, String>{
    @Modifying
    @Query("CREATE CACHED TABLE IF NOT EXISTS \"PUBLIC\".\"FILES\" (\n"
            + "    \"MD5\" CHARACTER VARYING(255) NOT NULL,\n"
            + "    \"VERSION\" INTEGER,\n"
            + "    \"FILENAME\" CHARACTER VARYING(511) NOT NULL\n"
            + ");             \n"
            + "ALTER TABLE \"PUBLIC\".\"FILES\" ADD CONSTRAINT IF NOT EXISTS \"PUBLIC\".\"CONSTRAINT_3\" PRIMARY KEY(\n"
            + "\"FILENAME\");            \n"
            + "")  
    void createH2();
    @Modifying
    @Query("CREATE CACHED TABLE \"PUBLIC\".\"FILES\"(\n"
            + "    \"MD5\" CHARACTER VARYING(255) NOT NULL,\n"
            + "    \"FILENAME\" CHARACTER VARYING(511) NOT NULL\n"
            + ");             \n"
            + "ALTER TABLE \"PUBLIC\".\"FILES\" ADD CONSTRAINT \"PUBLIC\".\"CONSTRAINT_3\" PRIMARY KEY(\n"
            + "\"MD5\", \"FILENAME\");            \n"
            + "")  
    void createNot();

}
