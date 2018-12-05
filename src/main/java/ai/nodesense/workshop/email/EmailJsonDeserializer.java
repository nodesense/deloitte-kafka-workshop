// EmailJsonDeserializer.java
package ai.nodesense.workshop.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;


// Convert the serialized json bytes   to Email Object
// Consumer
// consumer.poll().. pull kafka msg, then convert the content to Email object
public class EmailJsonDeserializer implements Deserializer<Email> {
    private ObjectMapper objectMapper = new ObjectMapper();

    private Class<Email> tClass = Email.class;

    /**
     * Default constructor needed by Kafka
     */
    public EmailJsonDeserializer() {
        System.out.println("EmailJsonDeserializer created");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
        System.out.println("PRops are " + props);
        tClass = (Class<Email>) props.get("value.deserializer");
    }

    @Override
    public Email deserialize(String topic, byte[] bytes) {
        System.out.println("EmailJsonDeserializer deserialize called ");
        if (bytes == null)
            return null;

         Email email;
        try {
            System.out.println("Bytes received " +  new String(bytes, StandardCharsets.UTF_8));
            //System.out.println("Class is " + SMS.toString());

            // convert bytes to Email Object
            email = objectMapper.readValue(bytes, Email.class);
        } catch (Exception e) {
            System.out.println("Error while parsing ");
            throw new SerializationException(e);
        }

        return email;
    }

    @Override
    public void close() {

    }
}