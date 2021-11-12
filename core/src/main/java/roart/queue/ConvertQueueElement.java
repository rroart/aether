package roart.queue;

import java.util.List;
import java.util.Map;

import org.apache.tika.metadata.Metadata;

import roart.common.filesystem.MyFile;
import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;

public class ConvertQueueElement {

    public int size;
    public String dbfilename;
    public String filename;
    public String md5;
    public volatile IndexFiles index;
    public String retlistid;
    public String retlistnotid;
    public Map<String, String> metadata;
    public String convertsw;
  //  public UI ui;
    public String mimetype;
    public MyFile fsData;
    public InmemoryMessage message;

    public ConvertQueueElement(String dbfilename, String filename, String md5, IndexFiles index, String retlistid, String retlistnotid, Map<String, String> metadata, MyFile fsData, InmemoryMessage message) {
        this.dbfilename = dbfilename;
        this.filename = filename;
        this.md5 = md5;
        this.index = index;
        this.retlistid = retlistid;
        this.retlistnotid = retlistnotid;
        this.metadata = metadata;
        this.fsData = fsData;
        this.message = message;
    }

}
