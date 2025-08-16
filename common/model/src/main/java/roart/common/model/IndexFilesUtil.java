package roart.common.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import roart.common.config.MyConfig;

public class IndexFilesUtil {
    public static final int FILENAMECOLUMN = 3;
    public static final int MIMETYPECOLUMN = 4;    

    public static IndexFilesDTO getSample() {
        IndexFilesDTO indexFiles = new IndexFilesDTO("1234");
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
    
    public static IndexFilesDTO changeSample(IndexFilesDTO indexfiles) {
        Set<FileLocation> fls = indexfiles.getFilelocations();
        fls.remove(new FileLocation("localhost", "/tmp/t2"));
        return indexfiles;
    }

    public static ResultItem getSearchResultItem(IndexFiles index, String lang, float score, String[] highlights, List<String> metadata, String csnodename, FileLocation maybeFl) {
        boolean doclassify = MyConfig.conf.wantClassify();
        boolean admin = MyConfig.conf.admin;
        boolean dohighlightmlt = MyConfig.conf.getHighlightmlt();
    
        ResultItem ri = new ResultItem();
        ri.add("" + score);
        ri.add(index.getMd5());
        FileLocation fl = maybeFl;
        String nodename = null;
        String filename = null;
        if (fl != null) {
            nodename = fl.getNodeNoLocalhost(csnodename);
            filename = fl.getFilename();
        }
        ri.add(nodename);
        ri.add(filename);
        ri.add(index.getMimetype());
        if (dohighlightmlt) {
            if (highlights != null && highlights.length > 0) {
                ri.add(highlights[0]);
            } else {
                ri.add(null);
            }
        }
        ri.add(lang);
        ri.add(index.getIsbn());
        if (doclassify) {
            ri.add(index.getClassification());
        }
        ri.add(index.getTimestampDate().toString());
        ri.add(index.getCheckedDate().toString());
        if (admin) {
            ri.add("" + index.getSize());
            ri.add("" + index.getConvertsize());
            ri.add(index.getConvertsw());
            ri.add(index.getConverttime("%.2f"));
            ri.add(index.getTimeindex("%.2f"));
            if (doclassify) {
                ri.add(index.getTimeclass("%.2f"));
            }
            ri.add("" + index.getFailed());
            ri.add(index.getFailedreason());
            ri.add(index.getTimeoutreason());
            ri.add(index.getNoindexreason());
            ri.add("" + index.getFilelocations().size());
            ri.add(index.getCreated());
            ri.add(index.getChecked());
            String metadatastring = "";
            if (metadata != null) {
                for (String md : metadata) {
                    metadatastring = metadatastring + md + "<br>";
                }  
            }
            ri.add(metadatastring);
        }
        return ri;
    }

    public static ResultItem getResultItem(IndexFiles index, String lang, String csnodename, FileLocation maybeFl) {
        boolean doclassify = MyConfig.conf.wantClassify();
    
        if (lang == null || lang.length() == 0) {
            lang = "n/a";
        }
    
        ResultItem ri = new ResultItem();
        ri.add("" + index.getIndexed());
        ri.add(index.getMd5());
        FileLocation fl = maybeFl;
        String nodename = null;
        String filename = null;
        if (fl != null) {
            nodename = fl.getNodeNoLocalhost(csnodename);
            filename = fl.getFilename();
        }
        ri.add(nodename);
        ri.add(filename);
        ri.add(index.getMimetype());
        ri.add(lang);
        ri.add(index.getIsbn());
        if (doclassify) {
            ri.add(index.getClassification());
        }
        ri.add(index.getTimestampDate().toString());
        ri.add(index.getCheckedDate().toString());
        ri.add("" + index.getSize());
        ri.add("" + index.getConvertsize());
        ri.add(index.getConvertsw());
        ri.add(index.getConverttime("%.2f"));
        ri.add(index.getTimeindex("%.2f"));
        if (doclassify) {
            ri.add(index.getTimeclass("%.2f"));
        }
        ri.add("" + index.getFailed());
        ri.add(index.getFailedreason());
        ri.add(index.getTimeoutreason());
        ri.add(index.getNoindexreason());
        ri.add("" + index.getFilelocations().size());
        return ri;
    }

    public static ResultItem getHeader() {
        boolean doclassify = MyConfig.conf.wantClassify();
    
        ResultItem ri = new ResultItem();
        ri.add("Indexed");
        ri.add("Md5/Id");
        ri.add("Node");
        ri.add("Filename");
        ri.add("Mimetype");
        ri.add("Lang");
        ri.add("ISBN");
        if (doclassify) {
            ri.add("Classification");
        }
        ri.add("Timestamp");
        ri.add("Updated");
        ri.add("Size");
        ri.add("Convertsize");
        ri.add("Convertsw");
        ri.add("Converttime");
        ri.add("Indextime");
        if (doclassify) {
            ri.add("Classificationtime");
        }
        ri.add("Failed");
        ri.add("Failed reason");
        ri.add("Timeout reason");
        ri.add("No indexing reason");
        ri.add("Filenames");
        return ri;
    }

    public static ResultItem getHeaderSearch() {
        boolean doclassify = MyConfig.conf.wantClassify();
        boolean admin = MyConfig.conf.admin;
        boolean dohighlightmlt = MyConfig.conf.getHighlightmlt();
    
        ResultItem ri = new ResultItem();
        ri.add("Score");
        ri.add("Md5/Id");
        ri.add("Node");
        ri.add("Filename");
        ri.add("Mimetype");
        if (dohighlightmlt) {
            ri.add("Highlight and similar");
        }
        ri.add("Lang");
        ri.add("ISBN");
        if (doclassify) {
            ri.add("Classification");
        }
        ri.add("Timestamp");
        ri.add("Updated");
        if (admin) {
            ri.add("Size");
            ri.add("Convertsize");
            ri.add("Convertsw");
            ri.add("Converttime");
            ri.add("Indextime");
            if (doclassify) {
                ri.add("Classificationtime");
            }
            ri.add("Failed");
            ri.add("Failed reason");
            ri.add("Timeout reason");
            ri.add("No indexing reason");
            ri.add("Filenames");
            ri.add("Created");
            ri.add("Checked");
            ri.add("Metadata");
        }
        return ri;
    }

}
