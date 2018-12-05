// InvoiceTransactionalProducer.java
package ai.nodesense.workshop.invoice;

import ai.nodesense.workshop.models.Invoice;
import ai.nodesense.workshop.models.Order;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;

// FIXME: abortTransaction not happening for orders
public class InvoiceTransactionalProducer {

    static String INVOICE_TOPIC = "invoices";

    static String ORDER_TOPIC = "orders";

    public static void main(String[] args) throws Exception,  ExecutionException, InterruptedException {

        long events = 1;
        // FIXME: Always check
        String schemaUrl = "http://localhost:8081"; //default
        //String schemaUrl = "http://localhost:8091";

        Properties props = new Properties();
        // hardcoding the Kafka server URI for this example
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 2);
        props.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("schema.registry.url", schemaUrl);
        props.put("transactional.id", "order-invoice-transaction-20");



        Producer<String, Object> producer = new KafkaProducer<String, Object>(props);

        producer.initTransactions();
        Random rnd = new Random();
        producer.beginTransaction();
        for (long nEvents = 0; nEvents < events; nEvents++) {
            Invoice invoice = InvoiceGenerator.getNextRandomInvoice();


            Order order1 = new Order();
            order1.setName("Phone 1");
            order1.setId("1234");
            order1.setCategory("Mobile");
            order1.setOrderDate(1234124132341L);
            order1.setCustomerId("234");
            order1.setCountry("IN");
            order1.setState("UP");
            order1.setInvoiceId("1234556");
            order1.setPrice(10);
            order1.setQty(2);

            Order order2 = new Order();
            order2.setName("Phone 2");
            order2.setId("1235");
            order2.setCategory("Mobile");
            order2.setOrderDate(1234124132341L);
            order2.setCustomerId("234");
            order2.setCountry("IN");
            order2.setState("UP");
            order2.setInvoiceId("1234556");
            order2.setPrice(25);
            order2.setQty(4);


            invoice.setAmount(120);
            invoice.setQty(6);

            ProducerRecord<String, Object> order1Record = new ProducerRecord<String, Object>(ORDER_TOPIC,      order1.getId(),
                    order1);

            producer.send(order1Record);

            ProducerRecord<String, Object> order2Record = new ProducerRecord<String, Object>(ORDER_TOPIC,      order2.getId(),
                    order2);

            producer.send(order2Record);

            Thread.sleep(5000);
            // Throws exception, comment out and enable
            if (true){
                producer.abortTransaction();
                throw new Exception("CRASH");
            }


            // Invoice ID as key
            ProducerRecord<String, Object> invoiceRecord = new ProducerRecord<String, Object>(INVOICE_TOPIC,
                                                                            invoice.getState(),
                                                                           invoice);
            producer.send(invoiceRecord);

            System.out.println("Sent Invoice" + invoice);


            Thread.sleep(2500);
        }

        producer.commitTransaction();

    }
}
