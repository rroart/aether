package roart.database.cassandra;

import java.util.List;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;

@Dao
public interface IndexDao {
    @Select
    Index findById(String md5);

    @Insert
    void save(Index product);

    //@Insert
    //void saveAll(List<Index> product);

    @Delete(entityClass = Index.class)
    void deleteById(String md5);

    @Select
    List<Index> findAll();
    
}
