// EmailGenerator.java
package ai.nodesense.workshop.email;

import static java.lang.Math.random;
import static java.lang.Math.round;

public class EmailGenerator {
    // Create randome email
    public static Email createEmail() {

        String otp = String.valueOf(round(random() * 1000));
        String mobileNumber = "9" + String.valueOf(round(random() * 1000000000));
        String id = "9" + String.valueOf(round(random() * 1000000000));

        String transactionNumber = "9" + String.valueOf(round(random() * 1000000000));

        String message = otp + " is your OTP for transaction " + transactionNumber + "";


        Email email = new Email();
        email.setContent(message);
        email.setId(id);
        email.setSubject("Kafka Email " + transactionNumber);
        email.setTo("someone@example.com");
        email.setFrom("sender@example.com");

        return email;
    }

}
