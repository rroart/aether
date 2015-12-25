package roart.queue;

import roart.model.FileObject;
import roart.model.SearchDisplay;

public class TraverseQueueElement {

	private String myid;
	private String filename;
    private ClientQueueElement clientQueueElement;
        private String retlistid = null;
	private String retnotlistid = null;
        private String newsetid = null; 
	private String notfoundsetid;
	private String filestodoid;

    public TraverseQueueElement() {
	}
	
	public TraverseQueueElement(String myid, String filename, ClientQueueElement element, String retlistid, String retnotlistid, String newsetid, String notfoundsetid, String filestodosetid) {

	    this.setMyid(myid);
	    this.filename = filename;
	    this.setClientQueueElement(element);
		this.setRetlistid(retlistid);
		this.setRetnotlistid(retnotlistid);
		this.setNewsetid(newsetid);
		this.setNotfoundsetid(notfoundsetid);
		this.setFilestodoid(filestodosetid);
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

    public ClientQueueElement getClientQueueElement() {
        return clientQueueElement;
    }
    
    public void setClientQueueElement(ClientQueueElement element) {
        this.clientQueueElement = element;
    }

    public String getFilestodoid() {
        return filestodoid;
    }

    public void setFilestodoid(String filestodoid) {
        this.filestodoid = filestodoid;
    }

}
