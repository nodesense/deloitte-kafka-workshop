// EmailConsumer.java
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

    public static void main(String[] args) throws Exception{

        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(GROUP_ID_CONFIG, "email-consumer");
        props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG,  EmailJsonDeserializer.class);

       // props.put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, Email> consumer = new KafkaConsumer<>(props);

        consumer.subscribe( singletonList(TOPIC));

        System.out.println("Email Consumer starting!");

        while(true) {

            ConsumerRecords<String, Email> records = consumer.poll(ofSeconds(1));
            if (records.count() == 0)
                continue;


            for (ConsumerRecord<String, Email> record : records) {
                 Email email = record.value();

                System.out.printf("Email received %s %s %s",
                                            email.getId(),
                                            email.getSubject(),
                                            email.getContent());

//                String value = record.value();
//                // manually converting to email object
//                Email e = Email.fromJson(value);
//                System.out.println("Email id is " + e.getId());
//
//                System.out.println("Got email string " + value);

                System.out.printf("offset= %d, key= %s, value= %s\n", record.offset(), record.key(), record.value());
            }
        }
    }
}
