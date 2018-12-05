package ai.nodesense.workshop.order;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class OrderProducer {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        long events = 1000;
        String schemaUrl = "http://localhost:8081";

        Properties props = new Properties();
        // hardcoding the Kafka server URI for this example
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("schema.registry.url", schemaUrl);

        String topic = "orders";

        Producer<String, Order> producer = new KafkaProducer<String, Order>(props);

        Random rnd = new Random();
        for (long nEvents = 0; nEvents < events; nEvents++) {
            Order order = OrderGenerator.getNextRandomOrder();

            // Using IP as key, so events from same IP will go to same partition
            ProducerRecord<String, Order> record = new ProducerRecord<String, Order>(topic, order.getId().toString(), order);
            producer.send(record).get();
            System.out.println("Sent ProductOrder" + order);
            Thread.sleep(5000);
        }

    }
}
