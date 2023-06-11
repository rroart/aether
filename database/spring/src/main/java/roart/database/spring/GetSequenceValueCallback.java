package roart.database.spring;

import java.sql.SQLException;

import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

//@Component
public class GetSequenceValueCallback implements BeforeConvertCallback<Index> {
 
    //private Logger log = LogManager.getLogger(GetSequenceValueCallback.class);
 
    private final JdbcTemplate jdbcTemplate;
 
    public GetSequenceValueCallback(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
 
    @Override
    public Index onBeforeConvert(Index game) {
        if (game.getMd5() == null) {
            System.out.println("Get the next value from a database sequence and use it as the primary key");
 
            
            Long id = jdbcTemplate.query("select nextval('timingbl_seq')",
                    rs -> {
                        if (rs.next()) {
                            return rs.getLong(1);
                        } else {
                            System.out.println("sqle");
                            throw new SQLException("Unable to retrieve value from sequence chessgame_seq.");
                        }
                    });
            System.out.println("sqlid" + id);
            game.setMd5("" + id);
        }
 
        return game;
    }
}