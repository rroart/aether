package roart.queue;

import roart.common.model.FileObject;
import roart.common.service.ServiceParam;

@Deprecated
public class TraverseQueueElement {

    private String myid;
    private FileObject fileobject;
    private ServiceParam clientQueueElement;

    // for Jackson
    public TraverseQueueElement() {
        super();
    }

    public TraverseQueueElement(String myid, FileObject filename, ServiceParam element) {

        this.setMyid(myid);
        this.fileobject = filename;
        this.setClientQueueElement(element);
    }

    public String getMyid() {
        return myid;
    }

    public void setMyid(String myid) {
        this.myid = myid;
    }

    public FileObject getFileobject() {
        return fileobject;
    }

    public void setFileobject(FileObject filename) {
        this.fileobject = filename;
    }

    public ServiceParam getClientQueueElement() {
        return clientQueueElement;
    }

    public void setClientQueueElement(ServiceParam element) {
        this.clientQueueElement = element;
    }

}
