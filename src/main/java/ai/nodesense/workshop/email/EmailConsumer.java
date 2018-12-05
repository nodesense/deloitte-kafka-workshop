package ai.nodesense.workshop.email;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

import static java.time.Duration.ofSeconds;
import static java.util.Collections.singletonList;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

import static java.time.Duration.ofSeconds;

public class EmailConsumer {
    public static String TOPIC = "emails";

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(GROUP_ID_CONFIG, "a");
        props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG,  JsonPOJODeserializer.class);
       // props.put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

//        KafkaConsumer<String, SMS> consumer = new KafkaConsumer<>(props, new StringDeserializer(),
//                                                                         new JsonPOJODeserializer());
//

        KafkaConsumer<String, Email> consumer = new KafkaConsumer<>(props);

        consumer.subscribe( singletonList(TOPIC));

        System.out.println("Email Consumer starting!");

        while(true) {

            ConsumerRecords<String, Email> records = consumer.poll(ofSeconds(1));
            if (records.count() == 0)
                continue;

            for (ConsumerRecord<String, Email> record : records)
                System.out.printf("offset= %d, key= %s, value= %s\n", record.offset(), record.key(), record.value());

        }
    }
}
