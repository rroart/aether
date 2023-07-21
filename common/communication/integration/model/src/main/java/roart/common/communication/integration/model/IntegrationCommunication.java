package roart.common.communication.integration.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import roart.common.communication.model.Communication;

public abstract class IntegrationCommunication extends Communication {

    public IntegrationCommunication(String myname, Class myclass, String service, ObjectMapper mapper, boolean send, boolean receive, boolean sendreceive, String connection, boolean retrypoll) {
        super(myname, myclass, service, mapper, send, receive, sendreceive, connection, retrypoll);
    }

}
