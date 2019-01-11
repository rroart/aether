package roart.common.model;

import java.util.HashSet;
import java.util.Set;

public class IndexFilesUtil {
    public static IndexFiles getSample() {
        IndexFiles indexFiles = new IndexFiles("1234");
        indexFiles.setFailed(1);
        indexFiles.setConvertsw("tika");
        Set<FileLocation> filelocations = new HashSet<>();
        FileLocation filelocation = new FileLocation("localhost", "/tmp/t");
        filelocations.add(filelocation);
        filelocations.add(new FileLocation("localhost", "/tmp/t2"));
        indexFiles.setFilelocations(filelocations );
        //indexfiles.createTable();
        return indexFiles;
    }
    
    public static IndexFiles changeSample(IndexFiles indexfiles) {
        Set<FileLocation> fls = indexfiles.getFilelocations();
        fls.remove(new FileLocation("localhost", "/tmp/t2"));
        return indexfiles;
    }    
}
