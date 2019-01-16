package ai.nodesense.workshop;
import java.util.*;

import ai.nodesense.workshop.models.Invoice;
import kafka.serializer.DefaultDecoder;
import kafka.serializer.StringDecoder;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.spark.SparkConf;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.*;
//import org.apache.spark.streaming.kafka.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import scala.Tuple2;

import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

public class SparkOrder {
        public static String BOOTSTRAP_SERVERS = "116.203.31.40:9092";
        // FIXME: Always check
        public static String SCHEMA_REGISTRY = "http://116.203.31.40:8081"; //default
         public static String TOPIC = "invoices";



        public static void main(String[] args) throws  Exception {

                // Create context with a 2 seconds batch interval
                SparkConf sparkConf = new SparkConf()
                                                .setMaster("local[2]")
                                                .setAppName("KafkaStream12");
            JavaSparkContext sc = new JavaSparkContext(sparkConf);

            JavaStreamingContext streamingContext = new JavaStreamingContext(sc, Durations.seconds(2));


                Map<String, Object> kafkaParams = new HashMap<>();
                kafkaParams.put("bootstrap.servers", BOOTSTRAP_SERVERS);
                kafkaParams.put("key.deserializer", StringDeserializer.class);
                kafkaParams.put("value.deserializer", StringDeserializer.class);

            kafkaParams.put("key.serializer", StringSerializer.class);
            kafkaParams.put("value.serializer", StringSerializer.class);

                kafkaParams.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
                kafkaParams.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
//           kafkaParams.put(KEY_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
  //          kafkaParams.put(VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");

            kafkaParams.put("group.id", "spark-stream-3");
                kafkaParams.put("auto.offset.reset", "latest");
                kafkaParams.put("enable.auto.commit", false);
                kafkaParams.put("schema.registry.url", SCHEMA_REGISTRY);


                //Collection<String> topics = Arrays.asList(TOPIC);
            Set<String> topics = Collections.singleton(TOPIC);


                JavaInputDStream<ConsumerRecord<String, String>> stream =
                        KafkaUtils.createDirectStream(
                                streamingContext,
                                LocationStrategies.PreferConsistent(),
                                ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
                        );



            stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));

            stream.foreachRDD(rdd -> {
                System.out.println("Output " + rdd.take(5));
            });


            stream.context().start();
            stream.context().awaitTermination();
        }
}

