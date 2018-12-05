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

    public static Properties getConfiguration() {
        final String bootstrapServers = "localhost:9092";
        String schemaUrl = "http://localhost:8081";

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

        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

        final Serde<Invoice> InvoiceAvroSerde = new SpecificAvroSerde<>();

        // When you want to override serdes explicitly/selectively
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
                "http://localhost:8081");
        //
        InvoiceAvroSerde.configure(serdeConfig, true); // `true` for record keys


        // In the subsequent lines we define the processing topology of the Streams application.
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, Invoice> productInvoiceStream = builder.stream("product.orders");

        productInvoiceStream.foreach(new ForeachAction<String, Invoice>() {
            @Override
            public void apply(String key, Invoice value) {
                System.out.println("yeah " + key + " Value is  " + value );
            }
        });

        KStream<String, Invoice> onlyGt2Stream = productInvoiceStream.filter((key, order) ->  order.getQty() > 2);

        KGroupedStream<String, Invoice> categoryGroupStream = productInvoiceStream.groupBy(
                (key, invoice) -> invoice.getState()
        );

        final KTable<String, Long> statesInvoiceCounts = categoryGroupStream.count();



        statesInvoiceCounts.toStream().foreach(new ForeachAction<String, Long>() {
            @Override
            public void apply(String key, Long value) {
                System.out.println("\u001B[43m" + "***Category " + key + " Count is  " + value );
            }
        });

        statesInvoiceCounts.toStream().to("streams-category-count-output", Produced.with(stringSerde, longSerde));


        onlyGt2Stream.foreach(new ForeachAction<String, Invoice>() {
            @Override
            public void apply(String key, Invoice value) {
                System.out.println("\u001B[43m" + "yeah > 2 only " + key + " Value is  " + value );
            }
        });

        KStream<String, Invoice>[] orderBranchesStream = productInvoiceStream.branch(
                (key, value) -> value.getQty() <= 1, /* first predicate  */
                (key, value) -> value.getQty() > 1 &&  value.getQty() <= 3, /* second predicate */
                (key, value) -> true                 /* third predicate  */
        );

        orderBranchesStream[0].foreach(new ForeachAction<String, Invoice>() {
            @Override
            public void apply(String key, Invoice value) {
                System.out.println("branch <= 1 only " + key + " Value is  " + value );
            }
        });


        orderBranchesStream[1].foreach(new ForeachAction<String, Invoice>() {
            @Override
            public void apply(String key, Invoice value) {
                System.out.println("branch > 1  & <= 3 only " + key + " Value is  " + value );
            }
        });

        orderBranchesStream[2].foreach(new ForeachAction<String, Invoice>() {
            @Override
            public void apply(String key, Invoice value) {
                System.out.println("branch others only " + key + " Value is  " + value );
            }
        });

        /*
        KStream<String, Integer> productOrdersQty = productOrders.mapValues(value -> value.getQty());

        KStream<String, Integer> transformed = productOrders.map(
                (key, value) -> KeyValue.pair(value.getName().toString(), value.getQty()));

        transformed.foreach(new ForeachAction<String, Integer>() {
            @Override
            public void apply(String key, Integer value) {
                System.out.println("Product to Qty  " + key + " Value is  " + value );
            }
        });
        */


        final KafkaStreams streams = new KafkaStreams(builder.build(), props);

        streams.cleanUp();
        streams.start();

        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

}