package roart.testdata;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import roart.common.constants.FileSystemConstants;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.model.Location;

public class TestData {
    
    public List<IndexFiles> indexFiles;
    
    public List<FileLocation> fileLocations;

    public Set<FileLocation> fileLocationSet;
    
    public TestData() {
        FileObject fo1 = new FileObject(new Location(), "/home/t1.txt");
        FileLocation f1 = new FileLocation(fo1.location.toString(), fo1.object);
        FileObject fo2 = new FileObject(new Location(), "/home/t2.txt");
        FileLocation f2 = new FileLocation(fo2.location.toString(), fo2.object);
        FileObject fo3 = new FileObject(new Location(null, FileSystemConstants.S3TYPE, "chess"), "/tmp/t3.txt");
        FileLocation f3 = new FileLocation(fo3.location.toString(), fo3.object);
        fileLocations = List.of(f1, f2, f3);
        fileLocationSet = Set.of(f1, f2, f3);
        
        IndexFiles i1 = new IndexFiles("123");
        IndexFiles i2 = new IndexFiles("1234");
        IndexFiles i3 = new IndexFiles("12345");
        indexFiles = List.of(i1, i2, i3);
    }
}
