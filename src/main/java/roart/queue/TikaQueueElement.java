package roart.queue;

import java.util.List;

import roart.model.IndexFiles;
import roart.model.ResultItem;
import roart.model.SearchDisplay;

import org.apache.tika.metadata.Metadata;

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
    public SearchDisplay display;
  //  public UI ui;
    public String mimetype;

    public TikaQueueElement(String dbfilename, String filename, String md5, IndexFiles index, String retlistid, String retlistnotid, Metadata metadata, SearchDisplay display) {
	this.dbfilename = dbfilename;
	this.filename = filename;
	this.md5 = md5;
	this.index = index;
	this.retlistid = retlistid;
	this.retlistnotid = retlistnotid;
	this.metadata = metadata;
	this.display = display;
	//this.ui = ui;
    }

}
