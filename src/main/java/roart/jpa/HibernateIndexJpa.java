package roart.jpa;

import java.util.List;

import roart.model.Index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HibernateIndexJpa extends IndexJpa {

    private Log log = LogFactory.getLog(this.getClass());

    public Index getByMd5(String md5) throws Exception {
	return Index.getByMd5(md5);
    }

    public List<Index> getAll() throws Exception {
	return Index.getAll();
    }

}

