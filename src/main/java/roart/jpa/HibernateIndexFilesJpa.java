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

    private static Log log = LogFactory.getLog("HibernateIndexFilesJpa");

    public IndexFiles getByMd5(String md5) throws Exception {
	HibernateIndexFiles index = HibernateIndexFiles.getByMd5(md5);
	return convert(index);
    }

    public IndexFiles getByFilelocation(FileLocation fl) throws Exception {
	String filename = fl.getFilename();
	HibernateIndexFiles files = HibernateIndexFiles.getByFilename(filename);
	if (files == null) {
	    return null;
	}
	return convert(files);
    }

    public String getMd5ByFilelocation(FileLocation fl) throws Exception {
	String filename = fl.getFilename();
	return HibernateIndexFiles.getMd5ByFilename(filename);
    }

    public List<IndexFiles> getAll() throws Exception {
	List<IndexFiles> retlist = new ArrayList<IndexFiles>();
	List<HibernateIndexFiles> indexes = HibernateIndexFiles.getAll();
	for (HibernateIndexFiles index : indexes) {
	    IndexFiles ifile = convert(index);
	}
	return retlist;
    }

    public void save(IndexFiles i) {
	try {
	    HibernateIndexFiles hif = HibernateIndexFiles.ensureExistence(i.getMd5());
	    hif.setIndexed(i.getIndexed());
	    hif.setTimeindex(i.getTimeindex());
	    hif.setTimestamp(i.getTimestamp());
	    hif.setConvertsw(i.getConvertsw());
	    hif.setConverttime(i.getConverttime());
	    hif.setFailed(i.getFailed());
	    String fr = i.getFailedreason();
	    if (fr != null && fr.length() > 250) {
		fr = fr.substring(0,250);
	    }
	    hif.setFailedreason(fr); // temp fix substr
	    hif.setTimeoutreason(i.getTimeoutreason());
	    hif.setFilenames(i.getFilenames());
	} catch (Exception e) {
	    log.error("Exception", e);
	}
    }

    private IndexFiles convert(HibernateIndexFiles hif) {
	if (hif == null) {
	    return null;
	}
	String md5 = hif.getMd5();
	IndexFiles ifile = new IndexFiles(md5);
	//ifile.setMd5(hif.getMd5());
	ifile.setIndexed(hif.getIndexed());
	ifile.setTimeindex(hif.getTimeindex());
	ifile.setTimestamp(hif.getTimestamp());
	ifile.setConvertsw(hif.getConvertsw());
	ifile.setConverttime(hif.getConverttime());
	ifile.setFailed(hif.getFailed());
	ifile.setFailedreason(hif.getFailedreason());
	ifile.setTimeoutreason(hif.getTimeoutreason());
	Set<String> files = hif.getFilenames();
	for (String file : files) {
	    ifile.addFile(new FileLocation(file));
	}
	return ifile;
    }

    public void flush() {
	HibernateIndexFiles.flush();
    }

    public void close() {
	HibernateIndexFiles.commit();
    }

}
