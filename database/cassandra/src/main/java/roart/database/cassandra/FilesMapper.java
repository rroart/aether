package roart.database.cassandra;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface FilesMapper {
    @DaoFactory
    FilesDao filesDao(@DaoKeyspace CqlIdentifier keyspace);

}
