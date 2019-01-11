package roart.queue;

import java.util.List;

import org.apache.tika.metadata.Metadata;

import roart.common.model.IndexFiles;
import roart.common.model.ResultItem;

public class TikaQueueElement {

    public int size;
    public String dbfilename;
    public String filename;
    public String md5;
    public volatile IndexFiles index;
    public String retlistid;
    public String retlistnotid;
    public Metadata metadata;
    public String convertsw;
  //  public UI ui;
    public String mimetype;

    public TikaQueueElement(String dbfilename, String filename, String md5, IndexFiles index, String retlistid, String retlistnotid, Metadata metadata) {
	this.dbfilename = dbfilename;
	this.filename = filename;
	this.md5 = md5;
	this.index = index;
	this.retlistid = retlistid;
	this.retlistnotid = retlistnotid;
	this.metadata = metadata;
    }

}
