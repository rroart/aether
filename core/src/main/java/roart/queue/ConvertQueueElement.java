package roart.queue;

import java.util.Map;

import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;

public class ConvertQueueElement {

    public int size;
    public FileObject filename;
    public String md5;
    public volatile IndexFiles index;
    public String retlistid;
    public String retlistnotid;
    public Map<String, String> metadata;
    public String convertsw;
    //  public UI ui;
    public String mimetype;
    public InmemoryMessage message;
    public String content;

    // for Jackson
    public ConvertQueueElement() {
        super();
    }

    public ConvertQueueElement(FileObject filename, String md5, IndexFiles index, String retlistid, String retlistnotid, Map<String, String> metadata, InmemoryMessage message, String content) {
        this.filename = filename;
        this.md5 = md5;
        this.index = index;
        this.retlistid = retlistid;
        this.retlistnotid = retlistnotid;
        this.metadata = metadata;
        this.message = message;
        this.content = content;
    }

}
