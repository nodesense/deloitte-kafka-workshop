package ai.nodesense.workshop.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import java.io.IOException;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Data;
import lombok.Builder;

import java.io.IOException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "EmailBuilder")
@JsonDeserialize(builder = Email.EmailBuilder.class)
@ToString
public class Email {
    private String id;
    private String from;
    private String to;
    private String subject;
    private String content;

    @JsonPOJOBuilder(withPrefix = "")
    public static class EmailBuilder {
        // Lombok will add constructor, setters, build method
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    public String toJSON() throws IOException {
        return  objectMapper.writeValueAsString(this);
    }

    public static  Email fromJson(String json) throws  IOException {
        return objectMapper.readValue(json, Email.class);
    }

}
