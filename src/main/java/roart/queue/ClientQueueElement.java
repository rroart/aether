package roart.queue;

import com.vaadin.ui.UI;

public class ClientQueueElement {
	
    public enum Function { INDEX, FILESYSTEM, OVERLAPPING, REINDEXSUFFIX, REINDEXDATE, MEMORYUSAGE, NOTINDEXED, FILESYSTEMLUCENENEW, DBINDEX, DBSEARCH, CONSISTENTCLEAN, SEARCH, SEARCHSIMILAR, REINDEXLANGUAGE, DELETEPATH };
	
    public UI ui;
    public Function function;
    public String file;
    public String suffix;
    public String lowerdate;
    public String higherdate;
    public boolean reindex;
    public boolean md5change;
	
    public ClientQueueElement(UI ui, Function function, String file, String suffix, String lowerdate, String higherdate, boolean reindex, boolean md5change) {
	this.ui = ui;
	this.function = function;
	this.file = file;
	this.suffix = suffix;
	this.lowerdate = lowerdate;
	this.higherdate = higherdate;
	this.reindex = reindex;
	this.md5change = md5change;
    }

}
