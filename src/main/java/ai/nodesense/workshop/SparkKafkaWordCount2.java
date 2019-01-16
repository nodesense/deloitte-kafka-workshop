package ai.nodesense.workshop;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;

import java.util.*;
import java.util.regex.Pattern;

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

public final class SparkKafkaWordCount2 {
    private static final Pattern SPACE = Pattern.compile(" ");
    public static String BOOTSTRAP_SERVERS = "116.203.31.40:9092";
    public static String GROUP_ID = "word-spark-streaming";
    public static String TOPIC_ID = "greetings";

    public static void main(String[] args) throws Exception {


        //StreamingExamples.setStreamingLogLevels();

        String brokers = BOOTSTRAP_SERVERS;
        String groupId = GROUP_ID;
        String topics = TOPIC_ID;

        // Create context with a 2 seconds batch interval
        SparkConf sparkConf = new SparkConf()
                                    .setMaster("local[2]")
                                    .setAppName("JavaDirectKafkaWordCount");

        JavaSparkContext sc = new JavaSparkContext(sparkConf);

    }
}