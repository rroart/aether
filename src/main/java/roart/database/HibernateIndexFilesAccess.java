package roart.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;


import roart.model.FileLocation;
import roart.model.IndexFiles;
import roart.service.ControlService;
import roart.util.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateIndexFilesAccess extends IndexFilesAccess {

    private static Logger log = LoggerFactory.getLogger(HibernateIndexFilesAccess.class);

	@Override
    public IndexFiles getByMd5(String md5) throws Exception {
	HibernateIndexFiles index = HibernateIndexFiles.getByMd5(md5);
	return convert(index);
    }

	@Override
    public Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
    	return HibernateIndexFiles.getFilelocationsByMd5(md5);
    }

	@Override
    public IndexFiles getByFilelocation(FileLocation fl) throws Exception {
	String filename = fl.getFilename();
	HibernateIndexFiles files = HibernateIndexFiles.getByFilename(filename);
	if (files == null) {
	    return null;
	}
	return convert(files);
    }

	@Override
    public String getMd5ByFilelocation(FileLocation fl) throws Exception {
	String filename = fl.getFilename();
	return HibernateIndexFiles.getMd5ByFilename(filename);
    }

	@Override
    public List<IndexFiles> getAll() throws Exception {
	List<IndexFiles> retlist = new ArrayList<IndexFiles>();
	List<HibernateIndexFiles> indexes = HibernateIndexFiles.getAll();
	for (HibernateIndexFiles index : indexes) {
	    IndexFiles ifile = convert(index);
	    retlist.add(ifile);
	}
	return retlist;
    }

	@Override
    public void save(IndexFiles i) {
	try {
	    HibernateIndexFiles hif = HibernateIndexFiles.ensureExistence(i.getMd5());
	    hif.setIndexed(i.getIndexed());
	    hif.setTimeindex(i.getTimeindex());
	    hif.setTimestamp(i.getTimestamp());
	    hif.setTimeclass(i.getTimeclass());
	    hif.setClassification(i.getClassification());
	    hif.setConvertsw(i.getConvertsw());
	    hif.setConverttime(i.getConverttime());
	    hif.setFailed(i.getFailed());
	    String fr = i.getFailedreason();
	    if (fr != null && fr.length() > 250) {
		fr = fr.substring(0,250);
	    }
	    hif.setFailedreason(fr); // temp fix substr
	    hif.setTimeoutreason(i.getTimeoutreason());
	    hif.setNoindexreason(i.getNoindexreason());
	    hif.setLanguage(i.getLanguage());
	    hif.setFilenames(i.getFilenames());
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
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
	ifile.setTimeclass(hif.getTimeclass());
	ifile.setClassification(hif.getClassification());
	ifile.setConvertsw(hif.getConvertsw());
	ifile.setConverttime(hif.getConverttime());
	ifile.setFailed(hif.getFailed());
	ifile.setFailedreason(hif.getFailedreason());
	ifile.setTimeoutreason(hif.getTimeoutreason());
	ifile.setNoindexreason(hif.getNoindexreason());
	ifile.setLanguage(hif.getLanguage());
	Set<String> files = hif.getFilenames();
	for (String file : files) {
	    ifile.addFile(new FileLocation(file));
	}
	ifile.setUnchanged();
	return ifile;
    }

	@Override
    public void flush() throws Exception {
	HibernateIndexFiles.flush();
    }

	@Override
    public void close() throws Exception {
	HibernateIndexFiles.commit();
    }

	@Override
	public Set<String> getAllMd5() throws Exception {
		return HibernateIndexFiles.getAllMd5();
	}

	@Override
	public Set<String> getLanguages() throws Exception {
		return HibernateIndexFiles.getLanguages();
	}

    @Override
    public void delete(IndexFiles index) throws Exception {
       HibernateIndexFiles.delete(index);
    }

}
