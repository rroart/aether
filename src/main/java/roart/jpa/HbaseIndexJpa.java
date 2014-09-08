package roart.jpa;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import roart.model.Index;

public class HbaseIndexJpa extends IndexJpa {

    private Log log = LogFactory.getLog(this.getClass());

    public List<Index> getAll() throws Exception {
	return null;
    }

    public Index getByMd5(String md5) throws Exception {
	return null;
    }

}

