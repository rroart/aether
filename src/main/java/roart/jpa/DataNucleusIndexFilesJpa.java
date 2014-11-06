package roart.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import roart.dao.IndexFilesDao;

import roart.model.FileLocation;
import roart.model.IndexFiles;
import roart.model.DataNucleusIndexFiles;
import roart.service.ControlService;
import roart.util.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataNucleusIndexFilesJpa extends IndexFilesJpa {

    private static Logger log = LoggerFactory.getLogger("DataNucleusIndexFilesJpa");

    public IndexFiles getByMd5(String md5) throws Exception {
	DataNucleusIndexFiles index = DataNucleusIndexFiles.getByMd5(md5);
	return convert(index);
    }

    public Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
    	return DataNucleusIndexFiles.getFilelocationsByMd5(md5);
	}

    public IndexFiles getByFilelocation(FileLocation fl) throws Exception {
	DataNucleusIndexFiles files = DataNucleusIndexFiles.getByFilename(fl);
	if (files == null) {
	    return null;
	}
	return convert(files);
    }

    public String getMd5ByFilelocation(FileLocation fl) throws Exception {
	return DataNucleusIndexFiles.getMd5ByFilename(fl);
    }

    public List<IndexFiles> getAll() throws Exception {
	List<IndexFiles> retlist = new ArrayList<IndexFiles>();
	List<DataNucleusIndexFiles> indexes = DataNucleusIndexFiles.getAll();
	for (DataNucleusIndexFiles index : indexes) {
	    IndexFiles ifile = convert(index);
	    retlist.add(ifile);
	}
	return retlist;
    }

    public void save(IndexFiles i) {
	try {
	    DataNucleusIndexFiles hif = DataNucleusIndexFiles.ensureExistence(i.getMd5());
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
	    hif.setFilelocations(i.getFilelocations());
	} catch (Exception e) {
	    log.error(Constants.EXCEPTION, e);
	}
    }

    private IndexFiles convert(DataNucleusIndexFiles hif) {
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
	Set<String> files = hif.getFilelocations();
	for (String file : files) {
	    ifile.addFile(new FileLocation(file));
	}
	ifile.setUnchanged();
	return ifile;
    }

    public void flush() {
	DataNucleusIndexFiles.flush();
    }

    public void close() {
	DataNucleusIndexFiles.commit();
    }

}