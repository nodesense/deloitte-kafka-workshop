// InvoiceStreamConsumer.java

package ai.nodesense.workshop.invoice;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;


import ai.nodesense.workshop.models.Invoice;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import java.util.Properties;

public class InvoiceStreamConsumer {

    static  String bootstrapServers = "localhost:9092";
    //FIXME: chance schema url
    static String schemaUrl = "http://localhost:8081";

    public static Properties getConfiguration() {


        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "product-invoice-stream");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "product-invoice-stream-client");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        //streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        //streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);

        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);

        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);


        props.put("schema.registry.url", schemaUrl);
        return props;
    }


    public static void main(final String[] args) throws Exception {
        System.out.println("Running Invoice Stream");

        Properties props = getConfiguration();

        //Serdes ==> SerializationDeserialization

        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

        final Serde<Invoice> InvoiceAvroSerde = new SpecificAvroSerde<>();

        // When you want to override serdes explicitly/selectively
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
                schemaUrl);
        //
        InvoiceAvroSerde.configure(serdeConfig, true); // `true` for record keys


        // In the subsequent lines we define the processing topology of the Streams application.
        final StreamsBuilder builder = new StreamsBuilder();
        // a Stream is a consumer
        final KStream<String, Invoice> invoiceStream = builder.stream("invoices");

        invoiceStream.foreach(new ForeachAction<String, Invoice>() {
            @Override
            public void apply(String key, Invoice invoice) {
                System.out.println("Invoice Key " + key + " Invalue id  " + invoice.getId() + ":" + invoice.getAmount() );
                System.out.println("received invoice " + invoice);
            }
        });

        KStream<String, Invoice> invoiceQtyGt3Stream = invoiceStream
                                                      .filter((key, invoice) ->  invoice.getQty() > 3);

        KStream<String, Invoice> invoiceQtyGt3WithDiscountStream = invoiceQtyGt3Stream
                .map((key, invoice)-> { // transform
                    // immutable
                    invoice.setAmount(invoice.getAmount() - 100); //mutatble, bad one
                    return new KeyValue<>(key,invoice);
                });

        // Dropping invoice object, return key,value (string)
        KStream<String, String> invoiceQtyGt3WithDiscountKeyValueStream = invoiceQtyGt3WithDiscountStream
                .map((key, invoice)-> { // transform
                    return new KeyValue<>(key,invoice.getAmount().toString());
                });

        // publish to a new topic
        // (id, 100/150)
        invoiceQtyGt3WithDiscountKeyValueStream
                .to("streams-qty-amount-str", Produced.with(stringSerde, stringSerde));


        // Dropping invoice object, return key,value (string)
        KStream<String, Long> invoiceQtyGt3WithDiscountKeyValueLongStream = invoiceQtyGt3WithDiscountStream
                .map((key, invoice)-> { // transform
                    return new KeyValue<>(key,invoice.getAmount().longValue());
                });

        // publish to a new topic
        // (id, 100/150)
        invoiceQtyGt3WithDiscountKeyValueLongStream
                .to("streams-qty-amount-long", Produced.with(stringSerde, longSerde));



        invoiceQtyGt3WithDiscountStream.foreach(new ForeachAction<String, Invoice>() {
            @Override
            public void apply(String key, Invoice invoice) {
                System.out.println("Invoice Qty > 3 & - 100 " + key + " In value id  " + invoice.getId() + ":" + invoice.getAmount() );

            }
        });

        // collection of streams put together
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);


        // streams.cleanUp();
        streams.start();

        System.out.println("Stream started");

        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

}