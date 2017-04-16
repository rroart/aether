package roart.queue;

import roart.model.SearchDisplay;
import roart.service.ControlService;
import roart.service.SearchService;
import roart.service.ServiceParam;

import com.vaadin.ui.UI;

public class ClientQueueElement {
	
    public SearchDisplay display;
    //public UI ui;
    public String uiid;
    public ServiceParam.Function function;
    public String file;
    public String suffix;
    public String lowerdate;
    public String higherdate;
    public boolean reindex;
    public boolean md5change;
    public boolean clean;
	
    public ClientQueueElement() {    
    }
    
    public ClientQueueElement(UI ui, ServiceParam.Function function, String file, String suffix, String lowerdate, String higherdate, boolean reindex, boolean md5change, boolean clean) {
        this.display = SearchService.getSearchDisplay(ui);
 if (ui != null) {
        this.uiid = ui.getId();
 }
	this.function = function;
	this.file = file;
	this.suffix = suffix;
	this.lowerdate = lowerdate;
	this.higherdate = higherdate;
	this.reindex = reindex;
	this.md5change = md5change;
	this.clean = clean;
	
	//MyVaadinUI.uis.put(this.uiid, ui);
    }

}
