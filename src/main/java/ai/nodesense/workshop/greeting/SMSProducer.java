// SMSProducer.java
package ai.nodesense.workshop.greeting;

// Key Value Producer

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

import static java.lang.Math.random;
import static java.lang.Math.round;
import static org.apache.kafka.clients.producer.ProducerConfig.*;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.RETRIES_CONFIG;

public class SMSProducer {
    public static String BOOTSTRAP_SERVERS = "localhost:9092";
    public static String TOPIC = "sms";

    public static String[] categories = new String[] {
           "OTP",
            "AD",
            "ALERT",
            "BANK",
            "TV",
            "Mobile"
    };

    public static void main(String[] args) throws  Exception {
        System.out.println("Welcome to producer");

        Properties props = new Properties();

        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ACKS_CONFIG, "all");
        props.put(RETRIES_CONFIG, 0);
        props.put(BATCH_SIZE_CONFIG, 16000);
        props.put(LINGER_MS_CONFIG, 100);
        props.put(BUFFER_MEMORY_CONFIG, 33554432);
        props.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // Key as string, value as string
        Producer<String, String> producer = new KafkaProducer<>(props);


        for (String category:categories) {
            // producer record, topic, key (category), value (message)
            // send message, not waiting for ack
            String key = category;

            String message = "Message " + key  + " : " + String.valueOf(round(random() * 1000));

            // Sync await,
            RecordMetadata meta = producer
                    .send(new ProducerRecord<>(TOPIC, key, message))
                    .get();

            System.out.printf("SMS %s sent\n", message);
            Thread.sleep(10); // Demo only,
        }

        producer.close();


    }
}
