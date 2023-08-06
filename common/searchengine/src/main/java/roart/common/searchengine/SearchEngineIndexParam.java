package roart.common.searchengine;

import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.FileObject;

public class SearchEngineIndexParam extends SearchEngineParam {

    public String md5;
    public FileObject dbfilename;
    public String[] metadata;
    public String lang;
    public InmemoryMessage message;
    public String classification;
}
