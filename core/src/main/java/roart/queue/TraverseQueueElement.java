package roart.queue;

import roart.common.model.FileObject;
import roart.common.model.SearchDisplay;
import roart.common.service.ServiceParam;

public class TraverseQueueElement {

	private String myid;
	private String filename;
    private ServiceParam clientQueueElement;
        private String retlistid = null;
	private String retnotlistid = null;
        private String newsetid = null; 
	private String notfoundsetid;
	private String filestodoid;
	private String traversecountid;

    public TraverseQueueElement() {
	}
	
	public TraverseQueueElement(String myid, String filename, ServiceParam element, String retlistid, String retnotlistid, String newsetid, String notfoundsetid, String filestodosetid, String traversecountid) {

	    this.setMyid(myid);
	    this.filename = filename;
	    this.setClientQueueElement(element);
		this.setRetlistid(retlistid);
		this.setRetnotlistid(retnotlistid);
		this.setNewsetid(newsetid);
		this.setNotfoundsetid(notfoundsetid);
		this.setFilestodoid(filestodosetid);
		this.setTraversecountid(traversecountid);
		//this.setNomd5(nomd5);
	}

    public String getMyid() {
        return myid;
    }

    public void setMyid(String myid) {
        this.myid = myid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getRetlistid() {
        return retlistid;
    }

    public void setRetlistid(String retlistid) {
        this.retlistid = retlistid;
    }

    public String getRetnotlistid() {
        return retnotlistid;
    }

    public void setRetnotlistid(String retnotlistid) {
        this.retnotlistid = retnotlistid;
    }

    public String getNewsetid() {
        return newsetid;
    }

    public void setNewsetid(String newsetid) {
        this.newsetid = newsetid;
    }

    public String getNotfoundsetid() {
        return notfoundsetid;
    }

    public void setNotfoundsetid(String notfoundsetid) {
        this.notfoundsetid = notfoundsetid;
    }

    public ServiceParam getClientQueueElement() {
        return clientQueueElement;
    }
    
    public void setClientQueueElement(ServiceParam element) {
        this.clientQueueElement = element;
    }

    public String getFilestodoid() {
        return filestodoid;
    }

    public void setFilestodoid(String filestodoid) {
        this.filestodoid = filestodoid;
    }

    public String getTraversecountid() {
        return traversecountid;
    }

    public void setTraversecountid(String traversecountid) {
        this.traversecountid = traversecountid;
    }

}
