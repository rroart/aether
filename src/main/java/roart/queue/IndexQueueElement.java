package roart.queue;

import java.io.InputStream;
import java.util.List;
import org.apache.tika.metadata.Metadata;

import roart.model.IndexFiles;
import roart.model.ResultItem;

public class IndexQueueElement {
	public String type;
	public String md5;
	public InputStream inputStream;
	public IndexFiles index;
    public List<ResultItem> retlist;
	public int size;
	public String dbfilename;
    public Metadata metadata;
    public String convertsw;
	
    public IndexQueueElement(String type, String md5, InputStream inputStream, IndexFiles index, List<ResultItem> retlist, String dbfilename, Metadata metadata) {
	this.type = type;
	this.md5 = md5;
	this.inputStream = inputStream;
	this.index = index;
	this.retlist = retlist;
	this.dbfilename = dbfilename;
	this.metadata = metadata;
    }

}
