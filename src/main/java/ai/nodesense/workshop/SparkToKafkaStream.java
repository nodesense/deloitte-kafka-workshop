package ai.nodesense.workshop;

import java.util.*;
import java.util.regex.Pattern;

import lombok.val;
import org.apache.avro.ipc.specific.Person;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;
import scala.Tuple2;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.streaming.Durations;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import java.io.Serializable;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.MapFunction;

/**
 * Consumes messages from one or more topics in Kafka and does wordcount.
 * Usage: JavaDirectKafkaWordCount <brokers> <groupId> <topics>
 *   <brokers> is a list of one or more Kafka brokers
 *   <groupId> is a consumer group name to consume from topics
 *   <topics> is a list of one or more kafka topics to consume from
 *
 * Example:
 *    $ bin/run-example streaming.JavaDirectKafkaWordCount broker1-host:port,broker2-host:port \
 *      consumer-group topic1,topic2
 */


/*
kafka-topics --zookeeper 116.203.61.206:2181 --create --topic greetings --replication-factor 1 --partitions 3

kafka-console-producer --broker-list 116.203.61.206:9092 --topic greetings

 */

public final class SparkToKafkaStream {
    private static final Pattern SPACE = Pattern.compile(" ");
    public static String BOOTSTRAP_SERVERS = "116.203.61.206:9092";
    public static String GROUP_ID = "word-spark-streaming";
    public static String TOPIC_ID = "greetings";


    public static Seq<Integer> convertListToSeq(List<Integer> inputList) {
        return JavaConverters.asScalaIteratorConverter(inputList.iterator()).asScala().toSeq();
    }


    public static void main(String[] args) throws Exception {

        //StreamingExamples.setStreamingLogLevels();

        String brokers = BOOTSTRAP_SERVERS;
        String groupId = GROUP_ID;
        String topics = TOPIC_ID;

        // Create context with a 2 seconds batch interval
        SparkConf sparkConf = new SparkConf()
                .setMaster("local[1]")
                .setAppName("JavaDirectKafkaWordCount");

        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));

        JavaSparkContext spark = jssc.sparkContext();
        SQLContext sqlContext = new SQLContext(spark);

        Set<String> topicsSet = new HashSet<>(Arrays.asList(topics.split(",")));
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        List<String> numbers = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            numbers.add("" + i);
        }

        val numbersRDD = jssc.sparkContext().parallelize(numbers,
                4);


        Dataset<Row> numbersDF = sqlContext.createDataFrame(numbersRDD, String.class);

        numbersDF
                 .writeStream()
                 .format("kafka")
                 .option("kafka.bootstrap.servers", BOOTSTRAP_SERVERS)
                 .option("topic", TOPIC_ID)
                 .start();

        // Start the computation
        jssc.start();
        jssc.awaitTermination();
    }
}