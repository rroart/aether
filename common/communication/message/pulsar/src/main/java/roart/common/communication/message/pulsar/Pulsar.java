package roart.common.communication.message.pulsar;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.Authentication;
import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionType;

import com.fasterxml.jackson.databind.ObjectMapper;

import roart.common.communication.message.model.MessageCommunication;
import roart.common.constants.Constants;

public class Pulsar extends MessageCommunication {

    String subscriptionName = "r";

    PulsarClient client = null;

    Consumer<byte[]> consumer;
    Producer<String> stringProducer = null;

    public Pulsar(String myname, Class myclass, String service, ObjectMapper mapper, boolean send, boolean receive, boolean sendreceive, String connection, boolean retrypoll) {
        super(myname, myclass, service, mapper, send, receive, sendreceive, connection, retrypoll);
        try {
            client = PulsarClient.builder()
                    .serviceUrl(connection)
                    .build();
        } catch (PulsarClientException e) {
            log.error(Constants.EXCEPTION, e);
        }
        if (send) {
            try {
                stringProducer = client.newProducer(Schema.STRING)
                        .topic(getSendService())
                        .create();
            } catch (PulsarClientException e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
        if (receive) {
            try {
                consumer = client.newConsumer()
                        .topic(getReceiveService())
                        .subscriptionType(SubscriptionType.Shared)
                        .subscriptionName(subscriptionName)
                        .subscribe();
            } catch (PulsarClientException e) {
                log.error(Constants.EXCEPTION, e);
            }
        }

    }

    public void send(String string) {
        try {
            stringProducer.send(string);
        } catch (PulsarClientException e) {
            log.error(Constants.EXCEPTION, e);
        }        

        try {
            stringProducer.close();
        } catch (PulsarClientException e) {
            log.error(Constants.EXCEPTION, e);
        }
    }

    public String[] receiveString() {
        String string = null;
        // Wait for a message

        try {
            Message msg;
            if (retrypoll) {
                msg = consumer.receive();
            } else {
                msg = consumer.receive(1, TimeUnit.SECONDS);
                if (msg == null) {
                    return new String[0];
                }
            }

            try {
                // Do something with the message
                string = new String(msg.getData());

                // Acknowledge the message so that it can be deleted by the message broker
                consumer.acknowledge(msg);
            } catch (Exception e) {
                // Message failed to process, redeliver later
                consumer.negativeAcknowledge(msg);
            }
        } catch (PulsarClientException e) {
            log.error(Constants.EXCEPTION, e);
        }

        return new String[] { string };

    }

    @Override
    public void destroy() {
        try {
            client.close();
            client.shutdown();
        } catch (PulsarClientException e) {
            log.error(Constants.EXCEPTION, e);
        }
        try (PulsarAdmin admin = PulsarAdmin.builder()
                .serviceHttpUrl(connection)
                .readTimeout(3, TimeUnit.SECONDS)
                .build()) {

            /*
             * When bug is present, this call will throw a TimeoutException (read-timeout) after 3 seconds
             */
            admin.topics().delete(getService());
        } catch (PulsarClientException | PulsarAdminException e) {
            log.error(Constants.EXCEPTION, e);
        }


    }

    @Override
    public void destroyTmp() {
        // TODO Auto-generated method stub
    }
}
