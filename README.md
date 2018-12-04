
# Topics

```bash
kafka-topics --zookeeper localhost:2181 --create --topic greetings --replication-factor 1 --partitions 3

kafka-topics --list --zookeeper localhost:2181


kafka-topics --describe --zookeeper localhost:2181 --topic greetings
```
---

# Producer

```
    kafka-console-producer --broker-list localhost:9092 --topic greetings
```

```
kafka-console-producer  --broker-list localhost:9092 \
  --topic wordcount \
  --property "parse.key=true" \
  --property "key.separator=:"
```

# Consumer

```
    kafka-console-consumer --bootstrap-server localhost:9092 --topic greetings
```
    
```
    kafka-console-consumer --bootstrap-server localhost:9092 --topic greetings --from-beginning
```
```
   kafka-console-consumer --bootstrap-server localhost:9092 --topic greetings --partition 0 --from-beginning
```
```
   kafka-console-consumer --bootstrap-server localhost:9092 --topic greetings --partition 2 --offset 2
```

# Consumer with Serializer
```
kafka-console-consumer --bootstrap-server localhost:9092 \
    --topic wordcount \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```

# Consumer group command

```
kafka-console-consumer --formatter "kafka.coordinator.group.GroupMetadataManager\$OffsetsMessageFormatter" --bootstrap-server kafka:9092 --topic __consumer_offsets
```


```
kafka-run-class.sh kafka.admin.ConsumerGroupCommand --bootstrap-server kafka:9092 --group my-group --describe
```