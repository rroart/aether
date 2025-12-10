package roart.common.communication.integration.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.springrabbit.SpringRabbitMQEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import tools.jackson.databind.ObjectMapper;

import roart.common.communication.integration.model.IntegrationCommunication;
import roart.common.constants.Constants;

public class Camel extends IntegrationCommunication {

    CamelContext context;
    ProducerTemplate producer;
    ConsumerTemplate consumer;
    String vhost = "task";
    Endpoint endpoint;
    
    public Camel(String myname, Class myclass, String service, ObjectMapper mapper, boolean send, boolean receive, boolean sendreceive, String connection, boolean retrypoll) {
        super(myname, myclass, service, mapper, send, receive, sendreceive, connection, retrypoll);
        context = new DefaultCamelContext();
        context.start();
        ConnectionFactory connectionFactory = new CachingConnectionFactory();
        if (send) {
            endpoint = context.getEndpoint(connection + "/" + vhost + getSendService() + "?routingKey=camel&queues=" + getSendService());
            SpringRabbitMQEndpoint rabbitEndpoint = (SpringRabbitMQEndpoint) endpoint;
            rabbitEndpoint.setConnectionFactory(connectionFactory);
            producer = context.createProducerTemplate();
            producer.setDefaultEndpoint(endpoint);
        }
        if (receive) {
            consumer = context.createConsumerTemplate();
        }
        if (send) {
            AmqpAdmin admin = new RabbitAdmin(connectionFactory);
            admin.declareQueue(new Queue(getSendService()));
        }
        if (receive) {
            AmqpAdmin admin = new RabbitAdmin(connectionFactory);
            admin.declareQueue(new Queue(getReceiveService()));
        }
    }

    public void send(String s) {
        producer.sendBody(s);        
    }

    public String[] receiveString() {
        Endpoint endpoint = context.getEndpoint(connection + "/" + vhost + getReceiveService() + "?routingKey=camel&queues=" + getReceiveService());
        Exchange receive;
        if (retrypoll) {
            receive = consumer.receive(endpoint);
        } else {
            receive = consumer.receive(endpoint, 1000);
            if  (receive == null) {
                return new String[0];
            }
        }
        return new String[] { receive.getIn().getBody(String.class) };
    }

    public void destroy() {
        try {
            context.removeEndpoint(endpoint);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        context.stop();
    }

}
