> mysql -uroot

> GRANT ALL PRIVILEGES ON *.* TO 'kafka'@'localhost' IDENTIFIED BY 'pw';

> create database kafkadb;

> use kafkadb;

> create table products (id int, name varchar(255), price int, create_ts timestamp DEFAULT CURRENT_TIMESTAMP , update_ts timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP );

>  insert into products (id, name, price) values(1,'Phone 1', 1000);
>  insert into products (id, name, price) values(2,'Phone 2', 2000);
>  select * from products;

exit mysql

> cd gopal
> touch gopal_products_mysql_source.json
> nano gopal_products_mysql_source.json

> {{paste content below to json file}}

>  confluent load gopal_products_mysql_source -d gopal_products_mysql_source.json

> kafka-console-consumer --bootstrap-server localhost:9092 --topic mysql-products --from-beginning


let it run on a command prompt

kafka-avro-console-consumer --bootstrap-server localhost:9092 --topic mysql-products --from-beginning


other command prompt

> mysql -uroot
  > use kafkadb;
  > 


---
> touch gopal-mysql-file-sink.properties

name=gopal-mysql-file-sink
connector.class=FileStreamSink
tasks.max=1
file=/root/gopal/products-file.txt
topics=mysql-products


> confluent load gopal-mysql-file-sink -d gopal-mysql-file-sink.properties
    
    

confluent status gopal-mysql-file-sink

tail products-file.txt



{
        "name": "gopal_products_mysql_source",
        "config": {
                "_comment": "The JDBC connector class. Don't change this if you want to use the JDBC Source.",
                "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",

                "_comment": "How to serialise the value of keys - here use the Confluent Avro serialiser. Note that the JDBC Source Connector always returns null for the key ",
                "key.converter": "io.confluent.connect.avro.AvroConverter",

                "_comment": "Since we're using Avro serialisation, we need to specify the Confluent schema registry at which the created schema is to be stored. NB Schema Registry and Avro serialiser are both part of Confluent Open Source.",
                "key.converter.schema.registry.url": "http://localhost:8081",

                "_comment": "As above, but for the value of the message. Note that these key/value serialisation settings can be set globally for Connect and thus omitted for individual connector configs to make them shorter and clearer",
                "value.converter": "io.confluent.connect.avro.AvroConverter",
                "value.converter.schema.registry.url": "http://localhost:8081",


                "_comment": " --- JDBC-specific configuration below here  --- ",
                "_comment": "JDBC connection URL. This will vary by RDBMS. Consult your manufacturer's handbook for more information",
                "connection.url": "jdbc:mysql://localhost:3306/kafkadb?user=kafka&password=pw",

                "_comment": "Which table(s) to include",
                "table.whitelist": "products",

                "_comment": "Pull all rows based on an timestamp column. You can also do bulk or incrementing column-based extracts. For more information, see http://docs.confluent.io/current/connect/connect-jdbc/docs/source_config_options.html#mode",
                "mode": "timestamp",

                "_comment": "Which column has the timestamp value to use?  ",
                "timestamp.column.name": "update_ts",

                "_comment": "If the column is not defined as NOT NULL, tell the connector to ignore this  ",
                "validate.non.null": "false",

                "_comment": "The Kafka topic will be made up of this prefix, plus the table name  ",
                "topic.prefix": "mysql-"
        }
}











----------------

Select text using mouse in putty, it copies to clipboard 
right click it shall paste

confluent start
confluent stop

  confluent list connectors
  
  to know running connectors,
    
    confluent status connectors

3 VM Instances  Please use putty to login if you use windows..

System-Name:kafka-hyd     116.203.29.163	      username: root     password: Kafka@123
System-Name:kafka-delhi     159.69.212.47	      username: root   password: Kafka@123 
System-Name:kafka-blr     159.69.212.16      username: root     password: Kafka@123


SystemName: kafka-1   116.203.31.40 username: root, password: Kafka@123

For mac,  > ssh root@159.69.212.16

CPU (Bottleneck)
 cores 
 SPEED 1.8 GHZ, 2.4
 RAM DDR III, DDR 4
 HDD SPEED
 Network

 MYSQL
 MongoDB
 Cassandra

 
 Source connector read from file, publish to topic
  Keep watching for file change, any additions are notified

> cd ~
> mkdir gopal
> cd gopal
> touch gopal-file-source.properties
> touch gopal-file-sink.properties
> nano gopal-file-source.properties
  
    To save file, Press Ctrl + O
    To exit the nano, Press Ctrl + X
    
> touch input-file.txt
> echo "line 1" >> input-file.txt

    
    
confluent load gopal-file-source -d gopal-file-source.properties
confluent status connectors
confluent status gopal-file-source
confluent load gopal-file-sink -d gopal-file-sink.properties




    kafka-console-consumer --bootstrap-server localhost:9092 --topic gopal-file-topic --from-beginning



name=gopal-file-source
connector.class=FileStreamSource
tasks.max=1
file=/root/gopal/input-file.txt
topic=gopal-file-topic


---
  
  Sink connect, subscribe from topic(s), write to a file
  
name=gopal-file-sink
connector.class=FileStreamSink
tasks.max=1
file=/root/gopal/output-file.txt
topics=gopal-file-topic
