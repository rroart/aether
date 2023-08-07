package roart.queue;

import roart.common.model.FileObject;
import roart.common.service.ServiceParam;

@Deprecated
public class ListQueueElement {
    private FileObject fileObject;
    private String myid;
    private ServiceParam element;
    
    // for Jackson
    public ListQueueElement() {
        super();
    }
    public ListQueueElement(FileObject fileObject, String myid, ServiceParam element) {
        super();
        this.fileObject = fileObject;
        this.myid = myid;
        this.element = element;
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

}
