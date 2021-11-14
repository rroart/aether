package roart.queue;

import java.util.List;
import java.util.Map;

import org.apache.tika.metadata.Metadata;

import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;
import roart.common.model.SearchDisplay;

public class IndexQueueElement {
	public String type;
	public String md5;
    public String lang;
    // public String classification;
    // TODO also remove
    //public String content; // made from inputStream
	//public InputStream inputStream;
	public volatile IndexFiles index;
    public String retlistid;
    public String retlistnotid;
	public int size;
	public String dbfilename;
    public Map<String, String> metadata;
    public String convertsw;
    public InmemoryMessage message;
	
    public IndexQueueElement(String type, String md5, IndexFiles index, String retlistid, String retlistnotid, String dbfilename, Map<String, String> metadata, InmemoryMessage message) {
	this.type = type;
	this.md5 = md5;
	this.index = index;
	this.retlistid = retlistid;
	this.retlistnotid = retlistnotid;
	this.dbfilename = dbfilename;
	this.metadata = metadata;
	this.message = message;
    }

}
