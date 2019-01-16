// InvoiceProducer.java
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
    public static String BOOTSTRAP_SERVERS = "116.203.31.40:9092";
    // FIXME: Always check
    public static String SCHEMA_REGISTRY = "http://116.203.31.40:8081"; //default
    public static String TOPIC = "invoices";

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        long events = 100;


        Properties props = new Properties();
        // hardcoding the Kafka server URI for this example
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("schema.registry.url", SCHEMA_REGISTRY);

        Producer<String, Invoice> producer = new KafkaProducer<String, Invoice>(props);

        Random rnd = new Random();
        for (long nEvents = 0; nEvents < events; nEvents++) {
            Invoice invoice = InvoiceGenerator.getNextRandomInvoice();

            // Invoice ID as key
            ProducerRecord<String, Invoice> record = new ProducerRecord<String, Invoice>(TOPIC,
                                                                            invoice.getState(),
                                                                            invoice);
            producer.send(record).get();
            System.out.println("Sent Invoice" + invoice);
            Thread.sleep(5000);
        }

    }
}
