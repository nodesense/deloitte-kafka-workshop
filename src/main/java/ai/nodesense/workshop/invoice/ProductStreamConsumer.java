// InvoiceStreamConsumer.java

package ai.nodesense.workshop.invoice;

import ai.nodesense.workshop.models.Invoice;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class ProductStreamConsumer {

   // public static  String BOOTSTRAP_SERVERS = "localhost:9092";
    public static String BOOTSTRAP_SERVERS = "116.203.31.40:9092";
    //FIXME: chance schema url
    static String schemaUrl = "http://116.203.31.40:8081";

    public static Properties getConfiguration() {


        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "product-invoice-stream");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "product-invoice-stream-client");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        //streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        //streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);

        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);

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

        final Serde<GenericRecord> InvoiceAvroSerde = new GenericAvroSerde();

        // When you want to override serdes explicitly/selectively
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
                schemaUrl);
        //
        InvoiceAvroSerde.configure(serdeConfig, true); // `true` for record keys


        // In the subsequent lines we define the processing topology of the Streams application.
        final StreamsBuilder builder = new StreamsBuilder();
        // a Stream is a consumer
        final KStream<String, GenericRecord> productStream = builder.stream("mysql-products");

        productStream.foreach(new ForeachAction<String, GenericRecord>() {
            @Override
            public void apply(String key, GenericRecord product) {
                //product.getSchema().getFields().get(0).

                for(Schema.Field field:product.getSchema().getFields()) {

                }

                String producId = product.get("id").toString();
                String producPrice = product.get("price").toString();
                Schema.Field nameField = product.getSchema().getField("name");

                if (nameField != null) {
                    String productName = product.get("name").toString();
                    System.out.println("name is "+ productName );
                } else {
                    System.out.println("name not found");
                }

                System.out.printf("product data %s %s\n" ,producId, producPrice );
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