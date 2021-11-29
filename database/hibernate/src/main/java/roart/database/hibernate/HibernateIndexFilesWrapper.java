package roart.database.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.database.DatabaseConstructorParam;
import roart.common.database.DatabaseConstructorResult;
import roart.common.database.DatabaseFileLocationParam;
import roart.common.database.DatabaseFileLocationResult;
import roart.common.database.DatabaseIndexFilesParam;
import roart.common.database.DatabaseIndexFilesResult;
import roart.common.database.DatabaseLanguagesResult;
import roart.common.database.DatabaseMd5Param;
import roart.common.database.DatabaseMd5Result;
import roart.common.database.DatabaseParam;
import roart.common.database.DatabaseResult;
import roart.common.model.FileLocation;
import roart.common.model.IndexFiles;
import roart.database.DatabaseOperations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roart.common.util.FsUtil;

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
        Map<String, IndexFiles> indexFilesMap = new HashMap<>();
        for (String md5 : param.getMd5s()) {
            HibernateIndexFiles index = hibernateIndexFiles.getByMd5(md5);
            indexFilesMap.put(md5, convert(index));
        }
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        result.setIndexFilesMap(indexFilesMap);
        return result;
    }

    @Override
    public DatabaseFileLocationResult getFilelocationsByMd5(DatabaseMd5Param param) throws Exception {
        String md5 = param.getMd5();
        Set<FileLocation> fileLocationSet = hibernateIndexFiles.getFilelocationsByMd5(md5);
        DatabaseFileLocationResult result = new DatabaseFileLocationResult();
        FileLocation[] fileLocations = new FileLocation[1];
        result.fileLocation = fileLocationSet.stream().toArray(FileLocation[]::new);
        return result;
    }

    @Override
    public DatabaseIndexFilesResult getByFilelocation(DatabaseFileLocationParam param) throws Exception {
        FileLocation fl = param.getFileLocation();
        String filename = fl.getFilename();
        HibernateIndexFiles files = hibernateIndexFiles.getByFilename(filename);
        if (files == null) {
            return null;
        }
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        IndexFiles[] indexFiles = new IndexFiles[1];
        indexFiles[0] = convert(files);
        result.setIndexFiles(indexFiles);
        return result;
    }

    @Override
    public DatabaseMd5Result getMd5ByFilelocation(DatabaseFileLocationParam param) throws Exception {
        Map<String, String> md5Map = new HashMap<>();
        for (FileLocation fl : param.getFileLocations()) {
            String filename = fl.getFilename();
            String md5 = hibernateIndexFiles.getMd5ByFilename(fl.toString());
            md5Map.put(filename, md5);
        }
        DatabaseMd5Result result = new DatabaseMd5Result();
        result.setMd5Map(md5Map);
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
        result.setIndexFiles(retlist.stream().toArray(IndexFiles[]::new));
        return result;
    }

    @Override
    public DatabaseResult save(DatabaseIndexFilesParam param) {
        IndexFiles i = param.getIndexFiles();
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
            hif.setIsbn(i.getIsbn());
            hif.setFilenames(i.getFilelocations().stream().map(FileLocation::toString).collect(Collectors.toSet()));
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
        ifile.setIsbn(hif.getIsbn());
        Set<String> files = hif.getFilenames();
        for (String file : files) {
            ifile.addFile(FsUtil.getFileLocation(file));
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
        result.setMd5((String[]) hibernateIndexFiles.getAllMd5().stream().toArray(String[]::new));
        return result;
    }

    @Override
    public DatabaseLanguagesResult getLanguages(DatabaseParam param) throws Exception {
        DatabaseLanguagesResult result = new DatabaseLanguagesResult();
        result.languages = hibernateIndexFiles.getLanguages().stream().toArray(String[]::new);
        return result;
    }

    @Override
    public DatabaseResult delete(DatabaseIndexFilesParam param) throws Exception { 
        IndexFiles index = param.getIndexFiles();
        hibernateIndexFiles.delete(index);
        return null;
    }

    @Override
    public DatabaseConstructorResult destroy() throws Exception {
        hibernateIndexFiles.destroy();
        return null;
    }

    @Override
    public DatabaseConstructorResult clear(DatabaseConstructorParam param) throws Exception {
        hibernateIndexFiles.clear(param);
        return new DatabaseConstructorResult();
    }

    @Override
    public DatabaseConstructorResult drop(DatabaseConstructorParam param) throws Exception {
        hibernateIndexFiles.drop(param);
        return new DatabaseConstructorResult();        
    }
}
