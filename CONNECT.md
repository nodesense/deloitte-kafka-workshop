```
confluent log connect
```

### show the log dir

```
confluent current
```

List all connectors

      confluent list connectors


elasticsearch-sink
  file-source
  file-sink
  jdbc-source
  jdbc-sink
  hdfs-sink
  s3-sink
  
    confluent load file-source

  
  edit file /root/confluent-5.0.1/etc/kafka/connect-file-source.properties
  
  modify file location file=/root/test.txt
  
  for i in {1..3}; do echo "log line $i"; done > test.txt

  confluent load file-source

  confluent status connectors

kafka-avro-console-consumer --bootstrap-server localhost:9092 --topic connect-test --from-beginning

File Sink

etc/kafka/connect-file-sink.properties

file=root/test.sink.txt

  confluent load file-sink

  confluent status file-sink

tail -f test.sink.txt

## HDFS Connect

kafka-avro-console-producer --broker-list localhost:9092 --topic test_hdfs \
--property value.schema='{"type":"record","name":"myrecord","fields":[{"name":"f1","type":"string"}]}'


hadoop fs -ls /topics/test_hdfs/partition=0


  confluent load hdfs-sink -d etc/kafka-connect-hdfs/quickstart-hdfs.properties

hdfs dfs -get /topics/test_hdfs/partition=0/test_hdfs+0+0000000000+0000000002.avro



for i in {4..1000}; do echo "log line $i"; done >> test.txt

unload connectors

confluent unload file-source
confluent unload file-sink

Stop connect

  confluent stop connect
  confluent stop
  
  Wipeout all data
  
    confluent destroy



confluent load jdbc-source -d kafka-connect-jdbc-source.json
  confluent status connectors

confluent load jdbc_source_mysql_foobar_01 -d kafka-connect-jdbc-source.json

confluent status jdbc_source_mysql_foobar_01


ssh root@116.203.30.219

confluent load file-sink-mysql-foobar -d kafka-connect-file-sink.json

https://www.confluent.io/blog/simplest-useful-kafka-connect-data-pipeline-world-thereabouts-part-1/

confluent load es-sink-mysql-foobar-01 -d kafka-connect-elasticsearch-sink.json

Elastic Search

wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | sudo apt-key add -

sudo apt-get install apt-transport-https

echo "deb https://artifacts.elastic.co/packages/6.x/apt stable main" | sudo tee -a /etc/apt/sources.list.d/elastic-6.x.list


1. Confluence Kafka
2. 

1. FILE SOURCE (Hello World)
2. MYSQL
3. Elastic Search
4. HDFS

Next Day

5. Apache Spark
6. Production/performance