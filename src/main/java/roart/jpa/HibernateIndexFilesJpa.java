package roart.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import roart.dao.IndexFilesDao;

import roart.model.FileLocation;
import roart.model.IndexFiles;
import roart.model.HibernateIndexFiles;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HibernateIndexFilesJpa extends IndexFilesJpa {

    private Log log = LogFactory.getLog(this.getClass());

    public IndexFiles getByMd5(String md5) throws Exception {
	IndexFiles ifile = new IndexFiles();
	HibernateIndexFiles index = HibernateIndexFiles.getByMd5(md5);
	ifile.setMd5(index.getMd5());
	ifile.setIndexed(index.getIndexed());
	ifile.setTimestamp(index.getTimestamp());
	ifile.setConvertsw(index.getConvertsw());
	ifile.setFailed(index.getFailed());
	ifile.setFailedreason(index.getFailedreason());
	ifile.setTimeoutreason(index.getTimeoutreason());
	Set<String> files = index.getFilenames();
	for (String file : files) {
	    ifile.addFile(new FileLocation(file));
	}
	return ifile;
    }

    public IndexFiles getByFilelocation(FileLocation fl) throws Exception {
	String filename = fl.getFilename();
	IndexFiles files = IndexFilesDao.getByFilename(filename);
	return files;
    }

    public List<IndexFiles> getAll() throws Exception {
	List<IndexFiles> retlist = new ArrayList<IndexFiles>();
	List<HibernateIndexFiles> indexes = HibernateIndexFiles.getAll();
	for (HibernateIndexFiles index : indexes) {
	    IndexFiles ifile = new IndexFiles();
	    String md5 = index.getMd5();
	    ifile.setMd5(index.getMd5());
	    ifile.setIndexed(index.getIndexed());
	    ifile.setTimestamp(index.getTimestamp());
	    ifile.setConvertsw(index.getConvertsw());
	    ifile.setFailed(index.getFailed());
	    Set<String> files = index.getFilenames();
	    for (String file : files) {
		ifile.addFile(new FileLocation(file));
	    }
	}
	return retlist;
    }

    public void save(IndexFiles i) {
	try {
	    HibernateIndexFiles hif = HibernateIndexFiles.ensureExistence(i.getMd5());
	    hif.setIndexed(i.getIndexed());
	    hif.setTimestamp(i.getTimestamp());
	    hif.setConvertsw(i.getConvertsw());
	    hif.setFailed(i.getFailed());
	    hif.setFailedreason(i.getFailedreason());
	    hif.setTimeoutreason(i.getTimeoutreason());
	    hif.setFilenames(i.getFilenames());
	} catch (Exception e) {
	    log.error("Exception", e);
	}
    }

}
