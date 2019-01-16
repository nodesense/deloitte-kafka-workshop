Select text using mouse in putty, it copies to clipboard 
right click it shall paste

confluent start
confluent stop

  confluent list connectors
  
  to know running connectors,
    
    confluent status connectors

3 VM Instances  Please use putty to login if you use windows..

System-Name:kafka-hyd     116.203.29.163	       
System-Name:kafka-delhi     159.69.212.47	        
System-Name:kafka-blr     159.69.212.16      


SystemName: kafka-1   116.203.31.40  

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
