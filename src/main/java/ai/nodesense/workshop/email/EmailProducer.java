package ai.nodesense.workshop.email;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

import static java.lang.Math.random;
import static java.lang.Math.round;

import static java.lang.Math.round;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class EmailProducer {

    public static String TOPIC = "emails";
    public static void main(String[] args) throws Exception {


        Properties props = new Properties();

        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ACKS_CONFIG, "all");
        props.put(RETRIES_CONFIG, 0);
        props.put(BATCH_SIZE_CONFIG, 16000);
        props.put(LINGER_MS_CONFIG, 100);
        props.put(BUFFER_MEMORY_CONFIG, 33554432);
        props.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        props.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonPOJOSerializer.class);

        //props.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, Email> producer = new KafkaProducer<>(props);

        long t1 = System.currentTimeMillis();

        int i = 0;
        for(; i < 10; i++) {

            String key = String.valueOf(round(random() * 1000));

            Email email = EmailGenerator.createEmail();


            producer.send(new ProducerRecord<>(TOPIC, key, email));
        }
        System.out.println("Sent " + i + " emails " + (System.currentTimeMillis() - t1 + " ms"));

        producer.close();
    }
}
