package roart.database.spring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

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
import roart.common.util.FsUtil;
import roart.database.DatabaseOperations;

@Component
@ConditionalOnProperty(name = "springdata.single", havingValue = "true")
public class SpringIndexFilesWrapper extends DatabaseOperations {

    private static Logger log = LoggerFactory.getLogger(SpringIndexFilesWrapper.class);

    private IndexFilesRepository repo;
    
    private FilesRepository filesrepo;
    
    private SpringConfiguration config;
    
    @Autowired
    public SpringIndexFilesWrapper(IndexFilesRepository repo, FilesRepository filesrepo, NodeConfig nodeConf, SpringConfiguration config) {
        this.repo = repo;
        this.filesrepo = filesrepo;
        this.config = config;

        String driver = config != null ? config.getDriver() : null;
        if (driver == null) {
            log.info("Getting driver from nodeConf");
            driver = nodeConf.getSpringdataDriver();
        }
        log.info("Using driver {}", driver);
        
        if ("org.h2.Driver".equals(driver) || driver == null) {
            if (repo != null) {
                repo.createH2();
            }
            if (filesrepo != null) {
                filesrepo.createH2();
            }
        }
        if ("org.postgresql.Driver".equals(driver)) {
            if (repo != null) {
                repo.createPsql();
            }
            if (filesrepo != null) {
                filesrepo.createPsql();
            }
        }
        if ("com.microsoft.sqlserver.jdbc.SQLServerDriver".equals(driver)) {
            if (repo != null) {
                repo.createMssql();
            }
            if (filesrepo != null) {
                filesrepo.createMssql();
            }
        }
    }

    @Override
    public DatabaseIndexFilesResult getByMd5(DatabaseMd5Param param) throws Exception {
        Map<String, IndexFiles> indexFilesMap = new HashMap<>();
        /*
        for (String md5 : param.getMd5s()) {
            Optional<Index> index = repo.findById(md5);
            if (index.isPresent()) {
                indexFilesMap.put(md5, convert(index.get()));
            } else {
                log.error("Not");
            }
        }
        */
        Iterable<Index> indexes = repo.findAllById(param.getMd5s());
        indexes.forEach(i -> indexFilesMap.put(i.getMd5(), convert(i)));        
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        result.setIndexFilesMap(indexFilesMap);
        return result;
    }

    @Override
    public DatabaseFileLocationResult getFilelocationsByMd5(DatabaseMd5Param param) throws Exception {
        DatabaseFileLocationResult result = new DatabaseFileLocationResult();
        String md5 = param.getMd5();
        Optional<Index> index = repo.findById(md5);
        if (index.isPresent()) {
            IndexFiles i = convert(index.get());
            Set<FileLocation> fileLocationSet = i.getFilelocations();
            FileLocation[] fileLocations = new FileLocation[1];
            result.fileLocation = fileLocationSet.stream().toArray(FileLocation[]::new);            
        } else {
            log.error("Not");
        }
        return result;
    }

    @Deprecated
    @Override
    public DatabaseIndexFilesResult getByFilelocation(DatabaseFileLocationParam param) throws Exception {
        FileLocation fl = param.getFileLocation();
        String filename = fl.getFilename();
        Files f = filesrepo.findById(fl.toString()).orElse(null);
        if (f != null) {
            String md5 = f.getMd5();
            Optional<Index> index = repo.findById(md5);
            if (index.isPresent()) {
                DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
                IndexFiles i = convert(index.get());
                IndexFiles[] indexFiles = new IndexFiles[1];
                indexFiles[0] = i;
                result.setIndexFiles(indexFiles);
            } else {
                log.error("Not");
            }
        }
        return null;
    }

    @Override
    public DatabaseMd5Result getMd5ByFilelocation(DatabaseFileLocationParam param) throws Exception {
        Map<String, String> md5Map = new HashMap<>();
        /*
        for (FileLocation fl : param.getFileLocations()) {
            String filename = fl.getFilename();
            Files f = filesrepo.findById(fl.toString()).orElse(null);
            if (f != null) {
                String md5 = f.getMd5();
                md5Map.put(filename, md5);
            }
        }
        */
        DatabaseMd5Result result = new DatabaseMd5Result();
        Iterable<Files> files = filesrepo.findAllById(param.getFileLocations().stream().map(FileLocation::toString).toList());
        files.forEach(f -> md5Map.put(f.getFilename(), f.getMd5()));
        result.setMd5Map(md5Map);
        return result;
    }

    @Override
    public DatabaseIndexFilesResult getAll(DatabaseParam param) throws Exception {
        //List<IndexFiles> retlist = new ArrayList<>();
        //int indexes = repo.findAll();
        List<IndexFiles> retlist = StreamSupport.stream(repo.findAll().spliterator(), false).map(e -> map(e)).toList();

        /*
        for (HibernateIndexFiles index : indexes) {
            IndexFiles ifile = convert(index);
            retlist.add(ifile);
        }
        */
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        result.setIndexFiles(retlist.stream().toArray(IndexFiles[]::new));
        return result;
    }

    @Override
    public DatabaseIndexFilesResult getAllFiles(DatabaseParam param) throws Exception {
        List<roart.common.model.Files> retlist = StreamSupport.stream(filesrepo.findAll().spliterator(), false).map(e -> map(e)).toList();
        /*
        List<HibernateIndexFiles> indexes = hibernateIndexFiles.getAll();
        for (HibernateIndexFiles index : indexes) {
            for (String filename : index.getFilenames()) {
                retlist.add(new Files(filename, index.getMd5()));
            }
        }
        */
        DatabaseIndexFilesResult result = new DatabaseIndexFilesResult();
        result.setFiles(retlist.stream().toArray(roart.common.model.Files[]::new));
        return result;
    }

    @Override
    public DatabaseResult save(DatabaseIndexFilesParam param) {
        try {
            //HibernateUtil.currentSession(nodeConf.getH2dir()).clear();
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        Set<IndexFiles> is = param.getIndexFiles();
        save(is);
        /*
        for (IndexFiles i : is) {
            save(i);
        }
        */
        return null;
    }

    private void save(Set<IndexFiles> is) {
        Set<Index> indexes = new HashSet<>();
        Set<Files> files = new HashSet<>();
        for (IndexFiles i : is) {
            Index in = map(i);
            if (in != null) {
                log.debug("Save {}", in.toString());
                indexes.add(in);
            }
            for (FileLocation f : i.getFilelocations()) {                
                Files fi = map(i, f);
                log.debug("Save {}", fi.toString());
                files.add(fi);
            }
        }
        repo.saveAll(indexes);
        //filesrepo.saveAll(files);
        for (Files file : files) {
            Optional<Files> optFile = filesrepo.findById(file.getFilename());
            if (optFile.isPresent()) {
                Files aFile = optFile.get();
                if (!aFile.getMd5().equals(file.getMd5())) {
                    aFile.setMd5(file.getMd5());
                    log.info("Saving modified files");
                    filesrepo.save(aFile);
                }
            } else {
                filesrepo.save(file);
            }
         }
    }

    public DatabaseResult save(IndexFiles i) {
        log.debug("Md5 {}", i.getMd5());
        Index hif = map(i);
        if ( hif != null ) {
            log.debug("Save {}",  hif.toString());
            repo.save(hif);
        }
        for (FileLocation f : i.getFilelocations()) {
            Files fi = map(i, f);
            log.debug("Save {}", fi.toString());
            filesrepo.save(fi);
        }
        return null;
    }

    private Files map(IndexFiles i, FileLocation f) {
        Files fi = new Files();
        fi.setFilename(f.toString());
        fi.setMd5(i.getMd5());
        return fi;
    }

    private Index map(IndexFiles i) {
        try {
            Index hif = new Index(); //hibernateIndexFiles.ensureExistence(i.getMd5());
            hif.setMd5(i.getMd5());
            hif.setVersion(i.getVersion());
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
            hif.setCreated(i.getCreated());
            hif.setChecked(i.getChecked());
            return hif;
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            return null;
        }
    }

    private IndexFiles convert(Index hif) {
        if (hif == null) {
            return null;
        }
        String md5 = hif.getMd5();
        IndexFiles ifile = new IndexFiles(md5);
        //ifile.setMd5(hif.getMd5());
        ifile.setVersion(hif.getVersion());
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
        ifile.setCreated(hif.getCreated());
        ifile.setChecked(hif.getChecked());
        Set<String> files = hif.getFilenames();
        for (String file : files) {
            ifile.addFile(FsUtil.getFileLocation(file));
        }
        ifile.setUnchanged();
        return ifile;
    }

    @Override
    public DatabaseResult flush(DatabaseParam param) throws Exception {
        return null;
    }

    @Override
    public DatabaseResult commit(DatabaseParam param) throws Exception {
        return null;
    }

    @Override
    public DatabaseResult close(DatabaseParam param) throws Exception {
        return null;
    }

    @Override
    public DatabaseMd5Result getAllMd5(DatabaseParam param) throws Exception {
        DatabaseMd5Result result = new DatabaseMd5Result();
        result.setMd5(repo.findDistinctByMd5NotIn(List.of("bla")));
        return result;
    }

    @Override
    public DatabaseLanguagesResult getLanguages(DatabaseParam param) throws Exception {
        DatabaseLanguagesResult result = new DatabaseLanguagesResult();
        result.languages = repo.findDistinctByLanguageNotIn(List.of("bla"));
        return result;
    }

    @Override
    public DatabaseResult delete(DatabaseIndexFilesParam param) throws Exception { 
        Set<IndexFiles> indexes = param.getIndexFiles();
        for (IndexFiles index : indexes) {
            repo.deleteById(index.getMd5());
        }
        Set<roart.common.model.Files> files = param.getFiles();
        for (roart.common.model.Files index : files) {
            filesrepo.deleteById(index.getFilename());
        }
        return null;
    }

    @Override
    public DatabaseConstructorResult destroy() throws Exception {
        return null;
    }

    @Override
    public DatabaseConstructorResult clear(DatabaseConstructorParam param) throws Exception {
        repo.deleteAll();
        filesrepo.deleteAll();
        return new DatabaseConstructorResult();
    }

    @Override
    public DatabaseConstructorResult drop(DatabaseConstructorParam param) throws Exception {
        return new DatabaseConstructorResult();        
    }
    
    private IndexFiles map(Index hif) {
        if (hif == null) {
            return null;
        }
        String md5 = hif.getMd5();
        IndexFiles ifile = new IndexFiles(md5);
        ifile.setVersion(hif.getVersion());
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
        ifile.setCreated(hif.getCreated());
        ifile.setChecked(hif.getChecked());
        Set<String> files = hif.getFilenames();
        for (String file : files) {
            ifile.addFile(FsUtil.getFileLocation(file));
        }
        ifile.setUnchanged();
        return ifile;
    }

    private roart.common.model.Files map(Files hif) {
        if (hif == null) {
            return null;
        }
        String md5 = hif.getMd5();
        roart.common.model.Files ifile = new roart.common.model.Files();
        ifile.setVersion(hif.getVersion());
        ifile.setMd5(hif.getMd5());
        ifile.setFilename(hif.getFilename());
        return ifile;
    }

}
