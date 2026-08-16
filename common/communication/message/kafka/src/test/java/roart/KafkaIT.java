package roart;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import tools.jackson.databind.ObjectMapper;

import roart.common.communication.message.kafka.Kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//@SpringBootTest(classes = Kafka2IT.class)
//@SpringJUnitConfig(Kafka2IT.class)
@SpringJUnitConfig
@EmbeddedKafka(partitions = 1, topics = {"my-new-topic"})
public class KafkaIT {

    String BROKER_ADDR = "localhost:9092";

    @BeforeEach
    public void before(@Autowired EmbeddedKafkaBroker embeddedKafka) {
        System.out.println("before");
        BROKER_ADDR = embeddedKafka.getBrokersAsString();
    }

    // not @Test
    public void snot(@Autowired EmbeddedKafkaBroker embeddedKafka) throws Exception{
        System.out.println("embeddedKafka.getBrokersAsString() = " + embeddedKafka.getBrokersAsString());

        //Assign topicName to string variable
        String topicName = "my-new-topic";

        // create instance for properties to access producer configs
        Properties props = new Properties();

        //Assign localhost id
        props.put("bootstrap.servers", BROKER_ADDR);

        //Set acknowledgements for producer requests.
        props.put("acks", "all");

        //If the request fails, the producer can automatically retry,
        props.put("retries", 0);

        //Specify buffer size in config
        props.put("batch.size", 16384);

        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);

        props.put("key.serializer",
           "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
           "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer
           <String, String>(props);

        for(int i = 0; i < 10; i++)
           producer.send(new ProducerRecord<String, String>(topicName,
              Integer.toString(i), Integer.toString(i)));
                 System.out.println("Message sent successfully");
                 producer.close();
     }

    //@Test
    public void s(@Autowired EmbeddedKafkaBroker embeddedKafka) throws Exception{
        System.out.println("embeddedKafka.getBrokersAsString() = " + embeddedKafka.getBrokersAsString());

        //Assign topicName to string variable
        String topicName = "my-new-topic";

        /*
        // create instance for properties to access producer configs
        Properties props = new Properties();

        //Assign localhost id
        props.put("bootstrap.servers", BROKER_ADDR);

        //Set acknowledgements for producer requests.
        props.put("acks", "all");

        //If the request fails, the producer can automatically retry,
        props.put("retries", 0);

        //Specify buffer size in config
        props.put("batch.size", 16384);

        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);

        props.put("key.serializer",
           "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
           "org.apache.kafka.common.serialization.StringSerializer");
         */
        Map<String, Object> props = KafkaTestUtils.producerProps(embeddedKafka);
        props.put("key.serializer",
           "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
           "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);

        for(int i = 0; i < 10; i++)
            producer.send(new ProducerRecord<>(topicName, Integer.toString(i), Integer.toString(i)));
        System.out.println("Message sent successfully");
        producer.close();
        System.out.println("sended");
    }

    @Test
    public void t(@Autowired EmbeddedKafkaBroker embeddedKafka) throws Exception {
        // This test will produce messages and then consume them with a timeout so it doesn't run forever.
        String topicName = "my-new-topic";

        // Produce messages first
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafka);
        producerProps.put("key.serializer",
           "org.apache.kafka.common.serialization.StringSerializer");

        producerProps.put("value.serializer",
           "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<>(producerProps);
        for (int i = 0; i < 10; i++) {
            producer.send(new ProducerRecord<>(topicName, Integer.toString(i), Integer.toString(i)));
        }
        System.out.println("sent1");
        producer.flush();
        producer.close();
        System.out.println("sent2");

        // Create a consumer using KafkaTestUtils and DefaultKafkaConsumerFactory
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafka);
        consumerProps.put("key.deserializer",
           "org.apache.kafka.common.serialization.StringDeserializer");

        consumerProps.put("value.deserializer",
           "org.apache.kafka.common.serialization.StringDeserializer");
        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        Consumer<String, String> consumer = cf.createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, topicName);

        // Poll for records with a total timeout (e.g., 5 seconds)
        long end = System.currentTimeMillis() + Duration.ofSeconds(5).toMillis();
        int totalCount = 0;
        while (System.currentTimeMillis() < end && totalCount < 10) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
                totalCount++;
            }
        }

        consumer.close();

        if (totalCount == 0) {
            throw new AssertionError("No records consumed from embedded Kafka topic " + topicName);
        }

        System.out.println("Consumed records: " + totalCount);
    }

    Kafka k2;

    @BeforeEach
    public void b() {
        boolean retrypoll = true;
        System.out.println("beforeEach" + BROKER_ADDR);
        k2 = new Kafka("KAFKA", String.class, "tasks", new ObjectMapper(), true, true, false, BROKER_ADDR, retrypoll);
    }

    @Order(0)
    //@Test
    public void v1() {
        k2.send("s");
        System.out.println("sent");
    }

    @Order(1)
    //@Test
    public void v2() {
        System.out.println("received");
        String[] s = k2.receiveString();
        System.out.println(s);
        System.out.println("received2");
    }

}
