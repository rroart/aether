package roart.common.communication.integration.spring;

import java.util.Random;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

import roart.common.communication.integration.model.IntegrationCommunication;

public class Spring extends IntegrationCommunication {

    private RabbitTemplate template = rabbitTemplate();

    public Spring(String myname, Class myclass, String service, ObjectMapper mapper, boolean send, boolean receive, boolean sendreceive, String connection, boolean retrypoll) {
        super(myname, myclass, service, mapper, send, receive, sendreceive, connection, retrypoll);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(connection);
        if (sendreceive) {
            AmqpAdmin admin = new RabbitAdmin(connectionFactory);
            admin.declareQueue(new Queue(getSendService()));
            admin.declareQueue(new Queue(getReceiveService()));
        } else {
            if (send) {
                AmqpAdmin admin = new RabbitAdmin(connectionFactory);
                admin.declareQueue(new Queue(getSendService()));
            }
            if (receive) {
                AmqpAdmin admin = new RabbitAdmin(connectionFactory);
                admin.declareQueue(new Queue(getReceiveService()));
            }
        }
        //connectionFactory.setUsername("guest");
        //connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }

    public void send(String string) {
        template.convertAndSend(getSendService(), string);
    }

    public String[] receiveString() {
        String string = null;
        while (string == null) {
            Object object = template.receiveAndConvert(getReceiveService(), 1000);
            if (object == null || object instanceof String) {
                string = (String) object;
            } else {
                string = new String((byte[]) object);
            }
            if (!retrypoll) {
                break;
            }
        }
        if (string == null) {
            return new String[0];
        }
        return new String[] { string };
    }

    public void simpleRequest() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames("myq");
        container.setMessageListener(new MessageListenerAdapter(new HelloWorldHandler()));

        Random r = new Random();
        int account = r.nextInt((99999 - 1) + 1) + 1;
        String message = "Request balance for account " + account;
        System.out.println("" + template);
        //System.out.println("" + queue);
        //this.template.convertAndSend(queue.getName(), message);
        System.out.println(" [x] Sent '" + message + "'");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("end");
    }

    public class HelloWorldHandler {

        public void handleMessage(String text) {
            System.out.println("Received: " + text);
        }

    }

    @Override
    public void destroy() {
        destroyTmp();
    }

    protected void destroyTmp() {
        if (sendreceive) {
            CachingConnectionFactory connectionFactory =
                    new CachingConnectionFactory(connection);
            AmqpAdmin admin = new RabbitAdmin(connectionFactory);
            admin.deleteQueue(getReceiveService());
        }
    }

}
