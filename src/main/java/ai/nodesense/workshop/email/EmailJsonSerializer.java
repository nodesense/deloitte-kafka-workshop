//EmailJsonSerializer.java
package ai.nodesense.workshop.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

// Convert Email object (java object) to serialized format/JSON bytes
// called by the producer, when producer.send(.., email)
public class EmailJsonSerializer<T> implements Serializer<T> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Default constructor needed by Kafka
     */
    public EmailJsonSerializer() {
        System.out.println("EmailJsonSerializer object created ");
    }

    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, T emailObj) {
        System.out.println("EmailJsonSerializer serialize called ");

        if (emailObj == null)
            return null;

        try {
            // convert email to bytes
            byte[] bytes = objectMapper.writeValueAsBytes(emailObj);
            System.out.println("Bytes " + bytes);

            System.out.println("Bytes string " +  new String(bytes, StandardCharsets.UTF_8));
            return bytes;

        } catch (Exception e) {
            throw new SerializationException("Error serializing JSON message", e);
        }
    }

    @Override
    public void close() {
    }

}