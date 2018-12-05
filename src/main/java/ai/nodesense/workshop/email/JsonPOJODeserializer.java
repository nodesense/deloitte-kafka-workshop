package ai.nodesense.workshop.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JsonPOJODeserializer implements Deserializer<Email> {
    private ObjectMapper objectMapper = new ObjectMapper();

    private Class<Email> tClass = Email.class;

    /**
     * Default constructor needed by Kafka
     */
    public JsonPOJODeserializer() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
        System.out.println("PRops are " + props);
        tClass = (Class<Email>) props.get("value.deserializer");
    }

    @Override
    public Email deserialize(String topic, byte[] bytes) {
        if (bytes == null)
            return null;

        Email data;
        try {
            System.out.println("Bytes " +  new String(bytes, StandardCharsets.UTF_8));
            //System.out.println("Class is " + SMS.toString());

            data = objectMapper.readValue(bytes, Email.class);
        } catch (Exception e) {
            System.out.println("Error while parsing ");
            throw new SerializationException(e);
        }

        return data;
    }

    @Override
    public void close() {

    }
}