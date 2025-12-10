package roart.common.communication.message.model;

import roart.common.communication.model.Communication;

import tools.jackson.databind.ObjectMapper;

public abstract class MessageCommunication extends Communication {

    public MessageCommunication(String myname, Class myclass, String service, ObjectMapper mapper, boolean send, boolean receive, boolean sendreceive, String connection, boolean retrypoll) {
        super(myname, myclass, service, mapper, send, receive, sendreceive, connection, retrypoll);
    }

}
