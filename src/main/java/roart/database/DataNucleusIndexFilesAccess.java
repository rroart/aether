package roart.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import roart.model.FileLocation;
import roart.model.IndexFiles;
import roart.service.ControlService;
import roart.util.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataNucleusIndexFilesAccess extends IndexFilesAccess {

    private static Logger log = LoggerFactory
            .getLogger(DataNucleusIndexFilesAccess.class);

    @Override
    public IndexFiles getByMd5(String md5) throws Exception {
        DataNucleusIndexFiles index = DataNucleusIndexFiles.getByMd5(md5);
        return convert(index);
    }

    @Override
    public Set<FileLocation> getFilelocationsByMd5(String md5) throws Exception {
        return DataNucleusIndexFiles.getFilelocationsByMd5(md5);
    }

    @Override
    public IndexFiles getByFilelocation(FileLocation fl) throws Exception {
        DataNucleusIndexFiles files = DataNucleusIndexFiles.getByFilelocation(fl);
        if (files == null) {
            return null;
        }
        return convert(files);
    }

    @Override
    public String getMd5ByFilelocation(FileLocation fl) throws Exception {
        return DataNucleusIndexFiles.getMd5ByFilelocation(fl);
    }

    @Override
    public List<IndexFiles> getAll() throws Exception {
        List<IndexFiles> retlist = new ArrayList<IndexFiles>();
        List<DataNucleusIndexFiles> indexes = DataNucleusIndexFiles.getAll();
        for (DataNucleusIndexFiles index : indexes) {
            IndexFiles ifile = convert(index);
            retlist.add(ifile);
        }
        return retlist;
    }

    @Override
    public void save(IndexFiles i) throws Exception {
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
	    hif.setLanguage(i.getLanguage());

	    // check timing of this
	    List<DataNucleusFiles> curFiles = DataNucleusFiles.getByMd5(i.getMd5());
	    Map<String, DataNucleusFiles> curMap = new HashMap<String, DataNucleusFiles>();
	    for (DataNucleusFiles f : curFiles) {
		curMap.put(f.getFilelocation(), f);
	    }

	    List<DataNucleusFiles> newFiles = new ArrayList<DataNucleusFiles>();
	    
	    Set<FileLocation> fls = i.getFilelocations();
	    for (FileLocation fl : fls) {
	        DataNucleusFiles hf = DataNucleusFiles.ensureExistence(fl);
	        hf.setMd5(i.getMd5());
	        newFiles.add(hf);
		curMap.remove(fl.toString());
	    }
	    for (String key : curMap.keySet()) {
		DataNucleusFiles f = curMap.get(key);
	        log.info("deleting " + f.getFilelocation());
	        f.delete();
	    }
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
        // ifile.setMd5(hif.getMd5());
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
        Set<String> files = hif.getFilelocations();
        if (files != null) {
        for (String file : files) {
            ifile.addFile(new FileLocation(file));
        }
        }
        ifile.setUnchanged();
        return ifile;
    }

    @Override
    public void flush() throws Exception {
        DataNucleusIndexFiles.flush();
    }

    @Override
    public void commit() throws Exception {
        DataNucleusIndexFiles.commit();
    }

    @Override
    public void close() throws Exception {
        DataNucleusIndexFiles.close();
    }

    @Override
    public Set<String> getAllMd5() throws Exception {
        return DataNucleusIndexFiles.getAllMd5();
    }

    @Override
    public Set<String> getLanguages() throws Exception {
        return DataNucleusIndexFiles.getLanguages();
    }

    @Override
    public void delete(IndexFiles index) throws Exception {
        DataNucleusIndexFiles.delete(index);
        DataNucleusFiles.delete(index);
    }

}
