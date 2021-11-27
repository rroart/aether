package roart.common.searchengine;

import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.FileObject;

public class SearchEngineIndexParam extends SearchEngineParam {

    public String type;
    public String md5;
    public FileObject dbfilename;
    public String[] metadata;
    public String lang;
    public String content;
    public InmemoryMessage message;
    public String classification;
}
