// InvoiceGenerator.java
package ai.nodesense.workshop.invoice;

import java.util.Random;

import ai.nodesense.workshop.models.Invoice;

import java.util.Random;
import java.util.UUID;


// Helper to create a invoice randomly
public class InvoiceGenerator {
    static Random random = new Random();
    static int[] categories = {1, 2, 3, 4};
    static int[] customerIds = {1000, 2000, 3000, 4000, 5000, 6000};
    static String[] customerNames = {"Krish", "Gayathri", "Nila", "Venkat", "Hari", "Ravi"};

    static String[] stateIds = {"KA", "TN", "KL", "MH", "DL", "AP"};


    public static Invoice getNextRandomInvoice() {

        String categoryId = "" + customerIds[random.nextInt(customerIds.length)];
        String stateId = "" + stateIds[random.nextInt(stateIds.length)];

        String customerId = UUID.randomUUID().toString();


        String id = UUID.randomUUID().toString();


        Invoice invoice = new Invoice();

        invoice.setId(id);
        invoice.setCustomerId(customerId);

        invoice.setQty(random.nextInt(5) + 1);
        invoice.setAmount(random.nextInt(5000) + 100);
        invoice.setCountry("IN");
        invoice.setInvoiceDate(System.currentTimeMillis());
        invoice.setState(stateId);

        return invoice;
    }

}