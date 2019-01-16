package ai.nodesense.workshop;

        import java.util.HashMap;
        import java.util.HashSet;
        import java.util.Arrays;
        import java.util.Map;
        import java.util.Set;
        import java.util.regex.Pattern;

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

public final class SparkKafkaWordCount {
    private static final Pattern SPACE = Pattern.compile(" ");
    public static String BOOTSTRAP_SERVERS = "116.203.61.206:9092";
    public static String GROUP_ID = "word-spark-streaming";
    public static String TOPIC_ID = "greetings";

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

    Set<String> topicsSet = new HashSet<>(Arrays.asList(topics.split(",")));
    Map<String, Object> kafkaParams = new HashMap<>();
    kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
    kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    // Create direct kafka stream with brokers and topics
    JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(
            jssc,
            LocationStrategies.PreferConsistent(),
            ConsumerStrategies.Subscribe(topicsSet, kafkaParams));

    // Get the lines, split them into words, count the words and print
    JavaDStream<String> lines = messages.map(ConsumerRecord::value)
                                         .map (v -> {
                                             System.out.println("Word is " + v);
                                             return v;
                                         });
    JavaDStream<String> words = lines.flatMap(x -> Arrays.asList(SPACE.split(x)).iterator());
    JavaPairDStream<String, Integer> wordCounts = words.mapToPair(s -> new Tuple2<>(s, 1))
            .reduceByKey((i1, i2) -> i1 + i2);
    wordCounts.print();

    messages.foreachRDD(r -> {
        System.out.println("*** got an RDD, size = " + r.count());
        r.foreach(s -> System.out.println(s));
        if (r.count() > 0) {
            // let's see how many partitions the resulting RDD has -- notice that it has nothing
            // to do with the number of partitions in the RDD used to publish the data (4), nor
            // the number of partitions of the topic (which also happens to be four.)
            System.out.println("*** " + r.getNumPartitions() + " partitions");
            r.glom().foreach(a -> System.out.println("*** partition size = " + a.size()));
        }
    });


        wordCounts.foreachRDD(r -> {
        System.out.println("*** Word Count got an RDD, size = " + r.count());
        r.foreach(s -> System.out.println(s));
        if (r.count() > 0) {
            // let's see how many partitions the resulting RDD has -- notice that it has nothing
            // to do with the number of partitions in the RDD used to publish the data (4), nor
            // the number of partitions of the topic (which also happens to be four.)
            System.out.println("*** Word Count" + r.getNumPartitions() + " partitions");
            r.glom().foreach(a -> System.out.println("*** partition size = " + a.size()));
        }
    });


        // Start the computation
    jssc.start();
    jssc.awaitTermination();
    }
}