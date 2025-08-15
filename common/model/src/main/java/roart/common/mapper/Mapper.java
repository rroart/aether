package roart.common.mapper;

import java.util.stream.Collectors;

import roart.common.model.FileLocation;
import roart.common.model.Files;
import roart.common.model.FilesDTO;
import roart.common.model.IndexFiles;
import roart.common.model.IndexFilesDTO;

public class Mapper {
    public static FilesDTO map(IndexFiles i, FileLocation f) {
        FilesDTO fi = new FilesDTO();
        fi.setFilename(f.toString());
        fi.setMd5(i.getMd5());
        return fi;
    }

    public static Files map(FilesDTO hif) {
        if (hif == null) {
            return null;
        }
        String md5 = hif.getMd5();
        Files ifile = new Files();
        ifile.setVersion(hif.getVersion());
        ifile.setMd5(hif.getMd5());
        ifile.setFilename(hif.getFilename());
        return ifile;
    }

    public static IndexFiles map(IndexFilesDTO i) {
        IndexFiles hif = new IndexFiles(i.getMd5());
        hif.setMd5(i.getMd5());
        hif.setVersion(i.getVersion());
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
        hif.setFilelocations(i.getFilelocations()/*.stream().map(FileLocation::toString).collect(Collectors.toSet())*/);
        hif.setCreated(i.getCreated());
        hif.setChecked(i.getChecked());
        return hif;
    }

    public static IndexFilesDTO map(IndexFiles i) {
        IndexFilesDTO hif = new IndexFilesDTO();
        hif.setMd5(i.getMd5());
        hif.setVersion(i.getVersion());
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
        hif.setFilelocations(i.getFilelocations()/*.stream().map(FileLocation::toString).collect(Collectors.toSet())*/);
        hif.setCreated(i.getCreated());
        hif.setChecked(i.getChecked());
        return hif;
    }
}
