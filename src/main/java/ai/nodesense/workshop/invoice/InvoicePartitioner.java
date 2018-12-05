package ai.nodesense.workshop.invoice;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

// Writing custom partitioner
// USA
// IN
// UK etc
public class InvoicePartitioner implements  Partitioner {
    @Override
    public void configure(Map<String, ?> configs) {
        // configure from props
        // initialize
        System.out.println("Invoice configure ");
        System.out.println(configs);
    }

    // this is called by producer
    @Override
    public int partition(String topic,
                         Object key,
                         byte[] keyBytes,
                         Object value,
                         byte[] valueBytes,
                         Cluster cluster) {

        // return an int which is a partition number
        return 0;
    }

    @Override
    public void close() {
        // cleanup
    }
}
