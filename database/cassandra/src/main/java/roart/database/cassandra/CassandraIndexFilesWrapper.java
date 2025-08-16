package roart.database.cassandra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import roart.common.model.FilesDTO;
import roart.common.model.IndexFilesDTO;
import roart.common.util.FsUtil;
import roart.database.DatabaseOperations;

public class CassandraIndexFilesWrapper extends DatabaseOperations {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static boolean useMapper = false;
    
    private CassandraIndexFiles cassandraIndexFiles;

    public CassandraIndexFilesWrapper(String configname, NodeConfig nodeConf, String configid) {
        cassandraIndexFiles = new CassandraIndexFiles(null, configname, nodeConf);
    }

    @Override
    public DatabaseIndexFilesResult getByMd5(DatabaseMd5Param param) throws Exception {
        Set<String> md5s = param.getMd5s();
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        Map<String, IndexFilesDTO> indexFilesMap = cassandraIndexFiles.get(md5s);
        result.setIndexFilesMap(indexFilesMap);
        return result;
    }

    @Override
    public DatabaseFileLocationResult getFilelocationsByMd5(DatabaseMd5Param param) throws Exception {
        String md5 = param.getMd5();
        Set<FileLocation> fileLocationSet = cassandraIndexFiles.getFilelocationsByMd5(md5);
        DatabaseFileLocationResult result = new DatabaseFileLocationResult();
        FileLocation[] fileLocations = new FileLocation[1];
        result.fileLocation = fileLocationSet.stream().toArray(FileLocation[]::new);
        return result;
    }

    @Override
    public DatabaseIndexFilesResult getByFilelocation(DatabaseFileLocationParam param) throws Exception {
        FileLocation fl = param.getFileLocation();
        IndexFilesDTO indexFilesGot = cassandraIndexFiles.getIndexByFilelocation(fl);
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        IndexFilesDTO[] indexFiles = new IndexFilesDTO[1];
        indexFiles[0] = indexFilesGot;
        result.setIndexFiles(indexFiles);
        return result;
        /*
	IndexFiles ifile = new IndexFiles();
	IndexFiles files = IndexFilesDao.getByFilename(filename);
	ResultScanner scanner = table.getScanner(new Scan());
	for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
	    byte[] key = rr.getRow();
	}
	return files;
         */
    }

    @Override
    public DatabaseMd5Result getMd5ByFilelocation(DatabaseFileLocationParam param) throws Exception {
        Map<String, String> md5Map = new HashMap<>();
        for (FileLocation fl : param.getFileLocations()) {
            String filename = fl.getFilename();
            String md5 = cassandraIndexFiles.getMd5ByFilelocation(fl);
            md5Map.put(filename, md5);
        }
        DatabaseMd5Result result = new DatabaseMd5Result();
        result.setMd5Map(md5Map);
        return result;
    }

    @Override
    public DatabaseIndexFilesResult getAll(DatabaseParam param) throws Exception {
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        if (useMapper) {
            result.setIndexFiles(cassandraIndexFiles.getIndexDao().findAll().stream().map(e -> map(e)).toList().toArray(IndexFilesDTO[]::new));
        } else {
        result.setIndexFiles(cassandraIndexFiles.getAll().stream().toArray(IndexFilesDTO[]::new));
        }
        return result;
    }

    @Override
    public DatabaseIndexFilesResult getAllFiles(DatabaseParam param) throws Exception {
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        if (useMapper) {
            result.setFiles(cassandraIndexFiles.getFilesDao().findAll().stream().map(e -> map(e)).toList().toArray(FilesDTO[]::new));
        } else {
        result.setFiles(cassandraIndexFiles.getAllFiles().stream().toArray(FilesDTO[]::new));
        }
        return result;
    }

    @Override
    public DatabaseResult save(DatabaseIndexFilesParam param) throws Exception {
        Set<IndexFilesDTO> is = param.getIndexFiles();
        for (IndexFilesDTO i : is) {
            cassandraIndexFiles.put(i);
        }
        return null;
    }

    @Override
    public DatabaseResult flush(DatabaseParam param) throws Exception {
        cassandraIndexFiles.flush();
        return null;
    }

    @Override
    public DatabaseResult commit(DatabaseParam param) throws Exception {
        cassandraIndexFiles.commit();
        return null;
    }

    @Override
    public DatabaseResult close(DatabaseParam param) throws Exception {
        cassandraIndexFiles.close();
        return null;
    }

    @Override
    public DatabaseMd5Result getAllMd5(DatabaseParam param) throws Exception {
        DatabaseMd5Result result = new DatabaseMd5Result();
        result.setMd5(cassandraIndexFiles.getAllMd5().stream().toArray(String[]::new));
        return result;
    }

    @Override
    public DatabaseLanguagesResult getLanguages(DatabaseParam param) throws Exception {
        DatabaseLanguagesResult result = new DatabaseLanguagesResult();
        result.languages = cassandraIndexFiles.getLanguages().stream().toArray(String[]::new);
        return result;
    }

    @Override
    public DatabaseResult delete(DatabaseIndexFilesParam param) throws Exception {
        Set<IndexFilesDTO> indexes = param.getIndexFiles();
        for (IndexFilesDTO index : indexes) {
            cassandraIndexFiles.delete(index);
        }
        Set<FilesDTO> files = param.getFiles();
        for (FilesDTO index : files) {
            cassandraIndexFiles.delete(index);
        }
        return null;
    }

    @Override
    public DatabaseConstructorResult destroy() throws Exception {
        cassandraIndexFiles.destroy();
        return null;
    }

    @Override
    public DatabaseConstructorResult clear(DatabaseConstructorParam param) throws Exception {
        cassandraIndexFiles.clear(param);
        return new DatabaseConstructorResult();
    }

    @Override
    public DatabaseConstructorResult drop(DatabaseConstructorParam param) throws Exception {
        cassandraIndexFiles.drop(param);
        return new DatabaseConstructorResult();        
    }
    
    private Files map(IndexFilesDTO i, FileLocation f) {
        Files fi = new Files();
        fi.setFilename(f.toString());
        fi.setMd5(i.getMd5());
        return fi;
    }

    private Index map(IndexFilesDTO i) {
        try {
            Index hif = new Index(); //hibernateIndexFiles.ensureExistence(i.getMd5());
            hif.setMd5(i.getMd5());
            hif.setIndexed(i.getIndexed());
            hif.setTimeindex(i.getTimeindex());
            hif.setTimestamp(i.getTimestamp());
            hif.setTimeclass(i.getTimeclass());
            hif.setClassification(i.getClassification());
            hif.setMimetype(i.getMimetype());
            hif.setSize(i.getSize());
            hif.setConvertsize(i.getConvertsize());
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
            hif.setFilelocations(i.getFilelocations().stream().map(e -> e /*FileLocation::toString*/).collect(Collectors.toSet()));
            hif.setCreated(i.getCreated());
            hif.setChecked(i.getChecked());
            return hif;
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            return null;
        }
    }

    private IndexFilesDTO convert(Index hif) {
        if (hif == null) {
            return null;
        }
        String md5 = hif.getMd5();
        IndexFilesDTO ifile = new IndexFilesDTO(md5);
        //ifile.setMd5(hif.getMd5());
        ifile.setIndexed(hif.getIndexed());
        ifile.setTimeindex(hif.getTimeindex());
        ifile.setTimestamp(hif.getTimestamp());
        ifile.setTimeclass(hif.getTimeclass());
        ifile.setClassification(hif.getClassification());
        ifile.setMimetype(hif.getMimetype());
        ifile.setSize(hif.getSize());
        ifile.setConvertsize(hif.getConvertsize());
        ifile.setConvertsw(hif.getConvertsw());
        ifile.setConverttime(hif.getConverttime());
        ifile.setFailed(hif.getFailed());
        ifile.setFailedreason(hif.getFailedreason());
        ifile.setTimeoutreason(hif.getTimeoutreason());
        ifile.setNoindexreason(hif.getNoindexreason());
        ifile.setLanguage(hif.getLanguage());
        ifile.setIsbn(hif.getIsbn());
        ifile.setCreated(hif.getCreated());
        ifile.setChecked(hif.getChecked());
        /*
        Set<String> files = hif.getFilelocation();
        for (String file : files) {
            ifile.addFile(FsUtil.getFileLocation(file));
        }
        */
        //ifile.setUnchanged();
        return ifile;
    }

     private IndexFilesDTO map(Index hif) {
        if (hif == null) {
            return null;
        }
        String md5 = hif.getMd5();
        IndexFilesDTO ifile = new IndexFilesDTO(md5);
        //ifile.setVersion(hif.getVersion());
        //ifile.setMd5(hif.getMd5());
        ifile.setIndexed(hif.getIndexed());
        ifile.setTimeindex(hif.getTimeindex());
        ifile.setTimestamp(hif.getTimestamp());
        ifile.setTimeclass(hif.getTimeclass());
        ifile.setClassification(hif.getClassification());
        ifile.setMimetype(hif.getMimetype());
        ifile.setSize(hif.getSize());
        ifile.setConvertsize(hif.getConvertsize());
        ifile.setConvertsw(hif.getConvertsw());
        ifile.setConverttime(hif.getConverttime());
        ifile.setFailed(hif.getFailed());
        ifile.setFailedreason(hif.getFailedreason());
        ifile.setTimeoutreason(hif.getTimeoutreason());
        ifile.setNoindexreason(hif.getNoindexreason());
        ifile.setLanguage(hif.getLanguage());
        ifile.setIsbn(hif.getIsbn());
        ifile.setCreated(hif.getCreated());
        ifile.setChecked(hif.getChecked());
        /*
        Set<String> files = hif.getFilelocation();
        for (String file : files) {
            ifile.addFile(FsUtil.getFileLocation(file));
        }
        */
        ifile.setFilelocations(new HashSet<>(hif.getFilelocations()));
        //ifile.setUnchanged();
        return ifile;
    }

    private FilesDTO map(Files hif) {
        if (hif == null) {
            return null;
        }
        String md5 = hif.getMd5();
        FilesDTO ifile = new FilesDTO();
        //ifile.setVersion(hif.getVersion());
        ifile.setMd5(hif.getMd5());
        ifile.setFilename(hif.getFilename());
        return ifile;
    }


}

