package ai.nodesense.workshop.invoice;

import ai.nodesense.workshop.models.Invoice;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;


import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;


public class InvoiceProducer {

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

        String topic = "invoices";

        Producer<String, Invoice> producer = new KafkaProducer<String, Invoice>(props);

        Random rnd = new Random();
        for (long nEvents = 0; nEvents < events; nEvents++) {
            Invoice order = InvoiceGenerator.getNextRandomInvoice();

            // Using IP as key, so events from same IP will go to same partition
            ProducerRecord<String, Invoice> record = new ProducerRecord<String, Invoice>(topic, order.getId().toString(), order);
            producer.send(record).get();
            System.out.println("Sent ProductOrder" + order);
            Thread.sleep(5000);
        }

    }
}
