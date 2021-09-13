package roart.service;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.DeliverCallback;
//import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;
import org.apache.camel.component.amqp.AMQPComponent;

public class CamelIT {

    CamelContext context;

    @BeforeEach
    public void setup() {
        System.out.println("setup1");
        context = new DefaultCamelContext();
        AMQPComponent amqpComponent = AMQPComponent.amqpComponent("amqp://localhost:5672");
        context.addComponent("amqp", amqpComponent);
        try {
            context.addRoutes(new RouteBuilder() {
                public void configure() {
                    //errorHandler(deadLetterChannel("file:deadletter"));
                    errorHandler(deadLetterChannel("amqp:queue:dead"));
                    from("amqp:queue:doc")
                    .convertBodyTo(TikaQueueElement.class)
                    .to("amqp:queue:incomingDoc");
                    from("amqp:queue:incomingDoc")
                    //.convertBodyTo(TikaQueueElement.class)
                    .process(new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            //System.out.println("fail " + exchange.isFailed());
                            //System.out.println("trans " + exchange.isTransacted());
                            System.out.println("We just got: "
                                    + exchange.getIn().getBody(String.class));
                        }
                    })
                    //.convertBodyTo(TikaQueueElement.class)
                    //bean(TikaQueueElement.class, "doSomething(\"t\")") 
                    .to("jms:incomingOrders");       
                    from("amqp:queue:incomingDocNot").
                    process(new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            System.out.println("We just got not: "
                                    + exchange.getIn().getBody());
                        }
                    }).
                    to("amqp:queue:dead").
                    process(new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            System.out.println("exchange here");
                            System.out.println("We just got not: "
                                    + exchange.getIn().getBody());
                        }
                    });
                }
            });
            System.out.println("setup2");
            context.start();
            System.out.println("setup3");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("setup4");

    }

    /*
    @Before
    public void setup() {
        System.out.println("setup1");
        context = new DefaultCamelContext();
        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory("vm://localhost");
        context.addComponent("jms",
                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        try {
            context.addRoutes(new RouteBuilder() {
                public void configure() {
                    from("jms:doc")
                    .to("jms:incomingDoc");
                }
            });
            System.out.println("setup2");
            context.start();
            System.out.println("setup3");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("setup4");

    }
     */

    @Test
    public void test() {
        Endpoint endpoint = context.getEndpoint("amqp:queue:doc");
        Exchange exchange = endpoint.createExchange();
        // populate exchange with data
        //getProcessor().process(exchange);
        ProducerTemplate template = exchange.getContext().createProducerTemplate();
        template.sendBody("amqp:queue:doc", "<hello>world!</hello>");
        TikaQueueElement elem = new TikaQueueElement();
        elem.md5 = "1234";
        elem.filename = "file";
        template.sendBody("amqp:queue:doc", elem);
        template.sendBody("amqp:queue:doc", "<hello>again!</hello>");
        template.sendBody("amqp:queue:incomingDoc", "<hello>again again!</hello>");
        template.sendBody("amqp:queue:incomingDoc", elem);
        template.sendBody("amqp:queue:incomingDoc2", "<hello>again 2!</hello>");

        /*
        ConsumerTemplate template2 = exchange.getContext().createConsumerTemplate();
        Exchange str = template2.receive ("amqp:queue");  
        Message str2 = str.getMessage();
        System.out.println("msg " + str2.getBody());
         */
    }

    /*
    @Test
    public void test1() throws URISyntaxException, Exception {
        BrokerService broker = BrokerFactory.createBroker(new URI(
                "broker:(tcp://localhost:61616)"));
        //broker.start();
        javax.jms.Connection connection = null;
        try {
            // Producer
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            connection = connectionFactory.createConnection();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("customerQueue");
            String payload = "Important Task";
            Message msg = session.createTextMessage(payload);
            MessageProducer producer = session.createProducer(queue);
            System.out.println("Sending text '" + payload + "'");
            producer.send(msg);
            MessageConsumer consumer = session.createConsumer(queue);
            connection.start();
            TextMessage textMsg = (TextMessage) consumer.receive();
            System.out.println(textMsg);
            System.out.println("Received: " + textMsg.getText());
            session.close();
        } finally {
            if (connection != null) {
                connection.close();
            }
            broker.stop();
        }
    }
     */

    /*
    private final static String QUEUE_NAME = "hello";
    @Test
    public void test2() throws IOException, TimeoutException {
        com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

         channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "Hello World!";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
        }
        {
            //ConnectionFactory factory = new ConnectionFactory();
            //factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            };
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });

        }
   }
     */

    @AfterEach
    public void shutdown() {
        try {
            Thread.sleep(10000);
            context.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("stopped");
    }

    public class TikaQueueElement {

        public int size;
        public String dbfilename;
        public String filename;
        public String md5;
        @Handler 
        public String doSomething(String body) {
            // process the in body and return whatever you want 
            return "Bye World"; }
    }
}
