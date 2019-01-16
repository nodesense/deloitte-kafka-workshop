## KAFKA - Message Broker

- Broker
- Consumer
- Producer
- Topics

- Replication - 3/5 

- Events [Append only logs] - File System

- Zookeeper (topics, leader election, offset)
- Connect
- Streaming - Data Processing - almost real time
              AVG, Sum, Join etc Cleaning, etc

- Throughputs 
           - Small Dataset
           - Medium Dataset
           - Large Dataset
           
   Operators
          - Sum, Avg, Max, Min
          - Groupby
          - JOINs
          - Windows/TimeStamp
          
Kafka - JVM
Lang: Scala/Java/JVM
Other: REST Proxy (Python, JavaScript etc)

## SPARK

Spark Core - Analytics engine - RDD
Spark SQL - Analytics engine - DataFrame, Dataset
Spark MLIB - Machine Learning - Distributed
Spark Streaming - Near REAL TIME DATA (Batch processing) - 1 sec batch

Lang: Scala/Java/JVM, Python, R


- Distributed Analytics Engine [1 node, 2 node, N nodes]
- MAP Reducer (not like Hadoop way)

- On Failure, it does recomputation
- No Replica

- 100000 records to be processed
- 1 node (1 system) - 1000 records per second (avg) 
            - 100 seconds to complete
            
- 2 nodes (2 system) - each 1000 records per second (avg) 
            - 50 seconds to complete
            
- 10 nodes (10 system) - each 1000 records per second (avg) 
            - 10 seconds to complete
            
- Memory - RAM to store the temp results