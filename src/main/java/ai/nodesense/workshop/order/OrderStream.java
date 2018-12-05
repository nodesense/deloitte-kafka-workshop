package ai.nodesense.workshop.order;

import ai.nodesense.workshop.models.Order;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class OrderStream {

    public static Properties getConfiguration() {
        final String bootstrapServers = "localhost:9092";
        String schemaUrl = "http://localhost:8081";

        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "product-orders-stream");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "product-orders-stream-client");
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
        System.out.println("Running ProductOrder Stream");

        Properties props = getConfiguration();

        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

        final Serde<Order> OrderAvroSerde = new SpecificAvroSerde<>();

        // When you want to override serdes explicitly/selectively
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
                "http://localhost:8081");
        //
        OrderAvroSerde.configure(serdeConfig, true); // `true` for record keys


        // In the subsequent lines we define the processing topology of the Streams application.
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, Order> productOrderStream = builder.stream("orders");

        productOrderStream.foreach(new ForeachAction<String, Order>() {
            @Override
            public void apply(String key, Order value) {
                System.out.println("yeah " + key + " Value is  " + value );
            }
        });

        KStream<String, Order> onlyGt2Stream = productOrderStream.filter((key, order) ->  order.getQty() > 2);

        KGroupedStream<String, Order> categoryGroupStream = productOrderStream.groupBy(
                (key, order) -> order.getCategory()
        );

        final KTable<String, Long> categoriesOrderCounts = categoryGroupStream.count();



        categoriesOrderCounts.toStream().foreach(new ForeachAction<String, Long>() {
            @Override
            public void apply(String key, Long value) {
                System.out.println("\u001B[43m" + "***Category " + key + " Count is  " + value );
            }
        });

        categoriesOrderCounts.toStream().to("streams-category-count-output", Produced.with(stringSerde, longSerde));


        onlyGt2Stream.foreach(new ForeachAction<String, Order>() {
            @Override
            public void apply(String key, Order value) {
                System.out.println("\u001B[43m" + "yeah > 2 only " + key + " Value is  " + value );
            }
        });

        KStream<String, Order>[] orderBranchesStream = productOrderStream.branch(
                (key, value) -> value.getQty() <= 1, /* first predicate  */
                (key, value) -> value.getQty() > 1 &&  value.getQty() <= 3, /* second predicate */
                (key, value) -> true                 /* third predicate  */
        );

        orderBranchesStream[0].foreach(new ForeachAction<String, Order>() {
            @Override
            public void apply(String key, Order value) {
                System.out.println("branch <= 1 only " + key + " Value is  " + value );
            }
        });


        orderBranchesStream[1].foreach(new ForeachAction<String, Order>() {
            @Override
            public void apply(String key, Order value) {
                System.out.println("branch > 1  & <= 3 only " + key + " Value is  " + value );
            }
        });

        orderBranchesStream[2].foreach(new ForeachAction<String, Order>() {
            @Override
            public void apply(String key, Order value) {
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