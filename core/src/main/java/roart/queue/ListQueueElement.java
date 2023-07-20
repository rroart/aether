package roart.queue;

import roart.common.model.FileObject;
import roart.common.service.ServiceParam;

public class ListQueueElement {
    private FileObject fileObject;
    private String myid;
    private ServiceParam element;
    private String retlistid = null;
    private String retnotlistid = null;
    //List<ResultItem> retList = null;
    //List<ResultItem> retNotList = null;
    private String newsetid = null; 
    //  MySet<String> newset = null; 
    //  Map<String, HashSet<String>> dirset;
    private String notfoundsetid;
    //boolean reindex = false;
    //boolean calculatenewmd5;
    private String filestodosetid;
    private String traversecountid;
    private boolean nomd5;
    private String filesdonesetid;
    
    // for Jackson
    public ListQueueElement() {
        super();
    }
    public ListQueueElement(FileObject fileObject, String myid, ServiceParam element, String retlistid,
            String retnotlistid, String newsetid, String notfoundsetid, String filestodosetid, String traversecountid,
            boolean nomd5, String filesdonesetid) {
        super();
        this.fileObject = fileObject;
        this.myid = myid;
        this.element = element;
        this.retlistid = retlistid;
        this.retnotlistid = retnotlistid;
        this.newsetid = newsetid;
        this.notfoundsetid = notfoundsetid;
        this.filestodosetid = filestodosetid;
        this.traversecountid = traversecountid;
        this.nomd5 = nomd5;
        this.filesdonesetid = filesdonesetid;
    }
    public FileObject getFileObject() {
        return fileObject;
    }
    public void setFileObject(FileObject fileObject) {
        this.fileObject = fileObject;
    }
    public String getMyid() {
        return myid;
    }
    public void setMyid(String myid) {
        this.myid = myid;
    }
    public ServiceParam getElement() {
        return element;
    }
    public void setElement(ServiceParam element) {
        this.element = element;
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
    public String getFilestodosetid() {
        return filestodosetid;
    }
    public void setFilestodosetid(String filestodosetid) {
        this.filestodosetid = filestodosetid;
    }
    public String getTraversecountid() {
        return traversecountid;
    }
    public void setTraversecountid(String traversecountid) {
        this.traversecountid = traversecountid;
    }
    public boolean isNomd5() {
        return nomd5;
    }
    public void setNomd5(boolean nomd5) {
        this.nomd5 = nomd5;
    }
    public String getFilesdonesetid() {
        return filesdonesetid;
    }
    public void setFilesdonesetid(String filesdonesetid) {
        this.filesdonesetid = filesdonesetid;
    }

}
