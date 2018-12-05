package ai.nodesense.workshop.email;



import java.util.Properties;

import static java.lang.Math.random;
import static java.lang.Math.round;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class EmailGenerator {
    public static Email createEmail() {


        String otp = String.valueOf(round(random() * 1000));
        String mobileNumber = "9" + String.valueOf(round(random() * 1000000000));
        String id = "9" + String.valueOf(round(random() * 1000000000));

        String transactionNumber = "9" + String.valueOf(round(random() * 1000000000));

        String message = otp + " is your OTP for transaction " + transactionNumber + "";
//
//        sms.setId(id);
//        sms.setMobileNumber(mobileNumber);
//        sms.setCategory("OTP");
//        sms.setMessage(message);

        Email email = new Email();
        //id, mobileNumber, message, "OTP");
        email.setContent("Hello from Kafka");
        email.setId(id);
        email.setSubject("Kafka Email");
        email.setTo("someone@example.com");
        email.setFrom("sender@example.com");

        return email;
    }

}
