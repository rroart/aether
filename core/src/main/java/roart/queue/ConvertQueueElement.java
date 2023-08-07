package roart.queue;

import java.util.Map;

import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;

@Deprecated
public class ConvertQueueElement {

    public String myid;
    public FileObject fileObject;
    public String md5;
    public volatile IndexFiles index;
    public Map<String, String> metadata;
    public InmemoryMessage message;

    // for Jackson
    public ConvertQueueElement() {
        super();
    }

    public ConvertQueueElement(String myid, FileObject filename, String md5, IndexFiles index, Map<String, String> metadata, InmemoryMessage message) {
        this.myid = myid;
        this.fileObject = filename;
        this.md5 = md5;
        this.index = index;
        this.metadata = metadata;
        this.message = message;
    }

}
