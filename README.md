
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


# Setting up Kafka Cluster


Single Machine [127.0.0.1]
Broker 0
.../etc/kafka/server.properties

    Port => 9092
    broker id: 0
    log.dir /tmp/kafka.logs
    

Broker 1
.../etc/kafka/server1.properties

     
    broker.id=1    
    listeners=PLAINTEXT://:9093
    log.dirs=/tmp/kafka-logs-1

    

Broker 2
.../etc/kafka/server2.properties

    Port => 9094
    broker id: 2
    log.dir /tmp/kafka.logs-2
    
    


Broker 3
.../etc/kafka/server3.properties

    Port => 9095
    broker id: 3
    log.dir /tmp/kafka.logs-3
    
    

# MAC


bin/kafka-server-start etc/kafka/server-1.properties
bin/kafka-server-start etc/kafka/server-2.properties
bin/kafka-server-start etc/kafka/server-3.properties



1. Producer/Consumer connect to 12.34.56.78 broker
2. Producer/Consumer send meta data request to get all brokers based on topic
3. Broker responds with avaialble lead brokers (partition)
        Broker 1 (P0, LEAD) 12.34.56.80 
        Broker 2 (P2, LEAD)12.34.56.82
        Broker 3 (P3, LEAD) 12.34.56.85

        ISR (Brokers)
4. Producer/Consumer connect to respective brokers 
5. Send message to specific broker 12.34.56.80 



KEYS = [ "OTP", "AD","ALERT", "BANK","TV","Mobile"]
PARTITION 4

HASH(OTP) = 10 % 4 => 2 PARTITION ID
HASH(OTP) = 10 % 4 => 2  PARTITION ID
HASH(AD) = 8 % 4 => 0 PARTITION
ALERT = 7 % 4 => 3 PARITION


## Rebalanced Consumers with in Consumer Group


10K per second
1000K per second (festive)
10K per second

Brokers (B0, B1, B2, ..B9), ...B100 
Topic 
Partition (4, P0, P1, P2, P3)
Replication factor 3 

Consumer Group (OrderGroup)  (10K per second)
       Consumer 1 (3333 per second)
       Consumer 2 (3333 per second)
       Consumer 3 (3333 per second)
       
       .....
       ...
       Consumer 100 (3333 per second)
   
Scale down
   Consumer Group (OrderGroup)  (10K per second)

   Consumer 1 (3333 per second)
         Consumer 2 (3333 per second)
         Consumer 3 (3333 per second)
         
         
1. consumer send request to localhost:9092,localhost:9093
2.1 get meta data 
2.2 Consumer susbcribe for topic, get meta data about all brokers for given topic
3. 4 brokers avaialble (3 replicas)
4. Target a spcific broker in sync replicas for read request
     Auto rebalancing happening - Powered by ZooKeeper
     
     1. 1 Consumer, 4 partition (each has one partition)
     
     scale up
     2. 2 Consumers, 4 partition (two consumers each from 2 partition)
       (no need to do rebalance)
     3. 4 Consumers, 4 partition (1 consumer to 1 broker)
     
     scale down
     4. 1 Consumer, 4 brokers (each has one partition)
          
            
            
     5 Consumers 4 paritions
     
     1 consumar -> 1 partition
     
     1 consumer idle
     
     
     100 Consumers 4 paritions
     
     96 consumer idle
     
# To know number of messages

kafka-run-class kafka.tools.GetOffsetShell \
  --broker-list localhost:9092 \
  --topic sms --time -1 --offsets 1 | \
   awk -F  ":" '{sum += $3} END {print sum}'
   
   
 # Create models using avro tool command line
``` 
java -jar lib/avro-tools-1.8.2.jar compile schema src/main/resources/avro/order.avsc  src/main/java```
```



    kafka-console-consumer --bootstrap-server localhost:9092 --topic streams-qty-amount-str
 

kafka-console-consumer --bootstrap-server localhost:9092 --topic streams-qty-amount-long --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
 
 



COMPACTION


kafka-topics --delete --zookeeper localhost:2181  --topic test
kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test \
                                                                              --config min.cleanable.dirty.ratio=0.01 \
                                                                              --config cleanup.policy=compact \
                                                                              --config segment.ms=100 \
                                                                              --config delete.retention.ms=100
                                                                              
                                                                              
                                                                              
kafka-topics --delete --zookeeper localhost:2181  --topic test
kafka-topics  --create --zookeeper localhost:2181 \
                                  --partitions 1 \
                                  --replication-factor 1 \
                                  --topic test \
                                  --config cleanup.policy=delete \
                                  --config segment.ms=100 \
                                  --config delete.retention.ms=100
                                                                              
                
Run zookeeper shell                            

bin/zkCli.sh -server 127.0.0.1:2181
> help
> ls / 
>  create /zk_test my_data
> get /zk_test
set /zk_test junk
delete /zk_test
ls /brokers/ids
get /brokers/ids/0
ls /brokers/topics

zookeeper-shell localhost:2181 

<<< "ls /brokers/ids"


volume - 10 GB per 1000 ms 
         10000000 messages per 1000 ms

Good CPU (Kafka) - 1 Computer (16 core), 48 RAM

    stream.
        .filter (100 ms)
        .map (150 ms)
        
        .sort (2000 ms)
        .groupBy (3000 ms)
        .avg() (5000 ms)

            10000 ms+

            90% slower than the input


Good CPU (SPARK) - 10 Computers (16 core), 48 RAM

1 Machine

    stream.
        .filter (100 ms)
        .map (150 ms)
        .sort (2000 ms)
        .groupBy (3000 ms)
        .avg() (5000 ms)
        .ml(predict)
 

10 Machine  (SPARK)

    stream.
        .filter (10 ms)
        .map (15 ms)
        .sort (200 ms)
        .groupBy (300 ms)
        .avg() (500 ms)
 
 
 ### with formatter
 
 ```bash
 kafka-console-consumer --bootstrap-server localhost:9092 \
     --topic streams-state-invoices-count \
     --from-beginning \
     --formatter kafka.tools.DefaultMessageFormatter \
     --property print.key=true \
     --property print.value=true \
     --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
     --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```

# Query KAFKA Schema registry

http://localhost:8081/subjects/
http://localhost:8081/subjects/invoices-key/versions
http://localhost:8081/subjects/invoices-value/versions
http://localhost:8081/subjects/invoices-value/versions/1



# For order and invoice

    kafka-console-consumer --bootstrap-server localhost:9092 --topic invoices
    kafka-console-consumer --bootstrap-server localhost:9092 --topic orders
        
