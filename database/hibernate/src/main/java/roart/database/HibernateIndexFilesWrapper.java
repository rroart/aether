package roart.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import roart.config.NodeConfig;
import roart.model.FileLocation;
import roart.model.IndexFiles;
import roart.util.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateIndexFilesWrapper extends DatabaseOperations {

    private static Logger log = LoggerFactory.getLogger(HibernateIndexFilesWrapper.class);

    private HibernateIndexFiles hibernateIndexFiles;
    private String nodename;
    
	public HibernateIndexFilesWrapper(String nodename, NodeConfig nodeConf) {
	    hibernateIndexFiles = new HibernateIndexFiles(nodename, nodeConf);
	    this.nodename = nodename;
	}

    @Override
    public DatabaseIndexFilesResult getByMd5(DatabaseMd5Param param) throws Exception {
	    String md5 = param.md5;
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        IndexFiles[] indexFiles = new IndexFiles[1];
        HibernateIndexFiles index = hibernateIndexFiles.getByMd5(md5);
        indexFiles[0] = convert(index);
        result.indexFiles = indexFiles;
        return result;
    }

	@Override
    public DatabaseFileLocationResult getFilelocationsByMd5(DatabaseMd5Param param) throws Exception {
        String md5 = param.md5;
        Set<FileLocation> fileLocationSet = hibernateIndexFiles.getFilelocationsByMd5(md5);
        DatabaseFileLocationResult result = new DatabaseFileLocationResult();
        FileLocation[] fileLocations = new FileLocation[1];
        result.fileLocation = (FileLocation[]) fileLocationSet.toArray();
        return result;
    }

	@Override
    public DatabaseIndexFilesResult getByFilelocation(DatabaseFileLocationParam param) throws Exception {
	    FileLocation fl = param.fileLocation;
	    String filename = fl.getFilename();
	HibernateIndexFiles files = hibernateIndexFiles.getByFilename(filename);
	if (files == null) {
	    return null;
	}
    DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
    IndexFiles[] indexFiles = new IndexFiles[1];
    indexFiles[0] = convert(files);
    result.indexFiles = indexFiles;
    return result;
    }

	@Override
    public DatabaseMd5Result getMd5ByFilelocation(DatabaseFileLocationParam param) throws Exception {
	    FileLocation fl = param.fileLocation;
	String filename = fl.getFilename();
    DatabaseMd5Result result = new DatabaseMd5Result();
    String[] md5 = new String[1];
    md5[0] = hibernateIndexFiles.getMd5ByFilename(filename);
    result.md5 = md5;
    return result;
    }

	@Override
    public DatabaseIndexFilesResult getAll(DatabaseParam param) throws Exception {
	List<IndexFiles> retlist = new ArrayList<IndexFiles>();
	List<HibernateIndexFiles> indexes = hibernateIndexFiles.getAll();
	for (HibernateIndexFiles index : indexes) {
	    IndexFiles ifile = convert(index);
	    retlist.add(ifile);
	}
    DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
    result.indexFiles = (IndexFiles[]) retlist.toArray();
    return result;
    }

	@Override
    public DatabaseResult save(DatabaseIndexFilesParam param) {
	    IndexFiles i = param.indexFiles;
	    try {
	    HibernateIndexFiles hif = hibernateIndexFiles.ensureExistence(i.getMd5());
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
	    return null;
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
	    ifile.addFile(new FileLocation(file, nodename, null));
	}
	ifile.setUnchanged();
	return ifile;
    }

	@Override
    public DatabaseResult flush(DatabaseParam param) throws Exception {
	hibernateIndexFiles.flush();
	return null;
    }

	@Override
    public DatabaseResult commit(DatabaseParam param) throws Exception {
	hibernateIndexFiles.commit();
	return null;
    }

	@Override
    public DatabaseResult close(DatabaseParam param) throws Exception {
	hibernateIndexFiles.close();
	return null;
    }

	@Override
	public DatabaseMd5Result getAllMd5(DatabaseParam param) throws Exception {
        DatabaseMd5Result result = new DatabaseMd5Result();
        result.md5 = (String[]) hibernateIndexFiles.getAllMd5().toArray();
        return result;
	}

	@Override
	public DatabaseLanguagesResult getLanguages(DatabaseParam param) throws Exception {
	       DatabaseLanguagesResult result = new DatabaseLanguagesResult();
	       	result.languages = (String[]) hibernateIndexFiles.getLanguages().toArray();
	       	return result;
	}

    @Override
    public DatabaseResult delete(DatabaseIndexFilesParam param) throws Exception { 
        IndexFiles index = param.indexFiles;
       hibernateIndexFiles.delete(index);
       return null;
    }

    @Override
    public DatabaseConstructorResult destroy() throws Exception {
        hibernateIndexFiles.destroy();
        return null;
    }

}
