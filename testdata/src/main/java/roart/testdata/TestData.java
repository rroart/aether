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
        FileObject fo4 = new FileObject(new Location(), "/home/t4.txt");
        FileLocation f4 = new FileLocation(fo4.location.toString(), fo4.object);
        fileLocations = List.of(f1, f2, f3, f4);
        fileLocationSet = Set.of(f1, f2, f3, f4);
        
        IndexFiles i1 = new IndexFiles("123");
        i1.addFile(f1);
        IndexFiles i2 = new IndexFiles("234");
        i2.addFile(f2);
        IndexFiles i3 = new IndexFiles("345");
        i3.addFile(f3);
        i3.addFile(f4);
        indexFiles = List.of(i1, i2, i3);
    }
}
