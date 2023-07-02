package roart.database.cassandra;

import java.util.List;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;

@Dao
public interface FilesDao {
    @Select
    Files findById(String filename);

    @Insert
    void save(Files product);

    //@Insert
    //void saveAll(List<Files> product);

    @Delete(entityClass = Files.class)
    void deleteById(String filename);

    @Select
    List<Files> findAll();
    
}
