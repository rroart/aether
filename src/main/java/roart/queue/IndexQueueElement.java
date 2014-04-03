package roart.queue;

import java.io.InputStream;
import java.util.List;
import org.apache.tika.metadata.Metadata;

import roart.model.Index;

public class IndexQueueElement {
	public String type;
	public String md5;
	public InputStream inputStream;
	public Index index;
    public List<String> retlist;
	public int size;
	public String dbfilename;
    public Metadata metadata;
	
    public IndexQueueElement(String type, String md5, InputStream inputStream, Index index, List<String> retlist, String dbfilename, Metadata metadata) {
	this.type = type;
	this.md5 = md5;
	this.inputStream = inputStream;
	this.index = index;
	this.retlist = retlist;
	this.dbfilename = dbfilename;
	this.metadata = metadata;
    }

}