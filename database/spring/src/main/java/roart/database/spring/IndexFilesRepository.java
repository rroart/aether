package roart.database.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

@Repository
public interface IndexFilesRepository extends CrudRepository<Index, String> {

    String[] findDistinctByLanguageNotIn(List l);

    String[] findDistinctByMd5NotIn(List l);

    @Modifying
    @Query("CREATE CACHED TABLE IF NOT EXISTS \"PUBLIC\".\"INDEX\" (\n"
            + "    \"MD5\" CHARACTER VARYING(255) NOT NULL,\n"
            + "    \"VERSION\" INTEGER,\n"
            + "    \"CHECKED\" CHARACTER VARYING(255),\n"
            + "    \"CLASSIFICATION\" CHARACTER VARYING(255),\n"
            + "    \"CONVERTSW\" CHARACTER VARYING(255),\n"
            + "    \"CONVERTTIME\" CHARACTER VARYING(255),\n"
            + "    \"CREATED\" CHARACTER VARYING(255),\n"
            + "    \"FAILED\" INTEGER,\n"
            + "    \"FAILEDREASON\" CHARACTER VARYING(255),\n"
            + "    \"FILENAMES\" CHARACTER VARYING(255),\n"
            + "    \"INDEXED\" BOOLEAN,\n"
            + "    \"ISBN\" CHARACTER VARYING(255),\n"
            + "    \"LANGUAGE\" CHARACTER VARYING(255),\n"
            + "    \"NOINDEXREASON\" CHARACTER VARYING(255),\n"
            + "    \"TIMECLASS\" CHARACTER VARYING(255),\n"
            + "    \"TIMEINDEX\" CHARACTER VARYING(255),\n"
            + "    \"TIMEOUTREASON\" CHARACTER VARYING(255),\n"
            + "    \"TIMESTAMP\" CHARACTER VARYING(255)\n"
            + ");\n"
            + "ALTER TABLE \"PUBLIC\".\"INDEX\" ADD CONSTRAINT IF NOT EXISTS \"PUBLIC\".\"CONSTRAINT_4\" PRIMARY KEY(\"MD5\");        \n"
            + "")
    void createH2();

    @Modifying
    @Query("CREATE TABLE IF NOT EXISTS \"index\" (\n"
            + "    \"md5\" CHARACTER VARYING(255) NOT NULL,\n"
            + "    \"version\" INTEGER,\n"
            + "    \"checked\" CHARACTER VARYING(255),\n"
            + "    \"classification\" CHARACTER VARYING(255),\n"
            + "    \"convertsw\" CHARACTER VARYING(255),\n"
            + "    \"converttime\" CHARACTER VARYING(255),\n"
            + "    \"created\" CHARACTER VARYING(255),\n"
            + "    \"failed\" INTEGER,\n"
            + "    \"failedreason\" CHARACTER VARYING(255),\n"
            + "    \"filenames\" CHARACTER VARYING(255),\n"
            + "    \"indexed\" BOOLEAN,\n"
            + "    \"isbn\" CHARACTER VARYING(255),\n"
            + "    \"language\" CHARACTER VARYING(255),\n"
            + "    \"noindexreason\" CHARACTER VARYING(255),\n"
            + "    \"timeclass\" CHARACTER VARYING(255),\n"
            + "    \"timeindex\" CHARACTER VARYING(255),\n"
            + "    \"timeoutreason\" CHARACTER VARYING(255),\n"
            + "    \"timestamp\" CHARACTER VARYING(255)\n"
            + ");\n"
            + "ALTER TABLE \"index\" DROP CONSTRAINT IF EXISTS \"constraint_4\";        \n"
            + "ALTER TABLE \"index\" ADD CONSTRAINT \"constraint_4\" PRIMARY KEY(\"md5\");        \n"
            + "")
    void createPsql();
}
