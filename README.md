
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