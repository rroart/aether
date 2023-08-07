package roart.queue;

import java.util.Map;

import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;

@Deprecated
public class IndexQueueElement {
    public String myid;
    public String md5;
    public volatile IndexFiles index;
    public FileObject fileObject;
    public Map<String, String> metadata;
    public InmemoryMessage message;

    // for Jackson
    public IndexQueueElement() {
        super();
    }

    public IndexQueueElement(String myid, FileObject fileObject, String md5, IndexFiles index, Map<String, String> metadata, InmemoryMessage message) {
        this.myid = myid;
        this.md5 = md5;
        this.index = index;
        this.fileObject = fileObject;
        this.metadata = metadata;
        this.message = message;
    }

}
