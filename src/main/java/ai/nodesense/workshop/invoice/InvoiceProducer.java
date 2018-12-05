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

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        long events = 10;
        // FIXME: Always check
        String schemaUrl = "http://localhost:8081"; //default
        //String schemaUrl = "http://localhost:8091";

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
            Invoice invoice = InvoiceGenerator.getNextRandomInvoice();

            // Invoice ID as key
            ProducerRecord<String, Invoice> record = new ProducerRecord<String, Invoice>(topic,
                                                                            invoice.getState(),
                                                                            invoice);
            producer.send(record).get();
            System.out.println("Sent Invoice" + invoice);
            Thread.sleep(5000);
        }

    }
}
