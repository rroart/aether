package roart.queue;

import java.io.InputStream;
import java.util.List;
import org.apache.tika.metadata.Metadata;

import roart.model.IndexFiles;
import roart.model.ResultItem;

import com.vaadin.ui.UI;

public class ClientQueueElement {
    public UI ui;
    public String function;
    public String file;
    public String suffix;
    public String lowerdate;
    public String higherdate;
    public boolean reindex;
    public boolean md5change;
	
    public ClientQueueElement(UI ui, String function, String file, String suffix, String lowerdate, String higherdate, boolean reindex, boolean md5change) {
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
