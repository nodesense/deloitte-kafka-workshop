package ai.nodesense.workshop.order;

import ai.nodesense.workshop.models.Order;

import java.util.Random;


public class OrderGenerator {


    static Random random = new Random();


    static int[] categories = {1, 2, 3, 4};

    static int[] customerIds = {1000, 2000, 3000, 4000, 5000, 6000};

    static String[] customerNames = {"Krish", "Gayathri", "Nila", "Venkat", "Hari", "Ravi"};

    static String[] productNames = {"iPhone", "Moto G", "One Plus", "Samsung Nexus", "Google Pixel", "Vivo"};
    static int[] productIds = {11, 22, 33, 44, 55, 66};


    static int[] stateIds = {10, 20, 30, 40};


    public static Order getNextRandomOrder() {

        String categoryId = "" + customerIds[random.nextInt(customerIds.length)];
        String stateId = "" + customerIds[random.nextInt(customerIds.length)];

        String customerId = "" + random.nextInt(100);

        int productIndex = random.nextInt(productIds.length);

        int productId = productIds[productIndex];

        String productName = productNames[productIndex];

        String id = "" + random.nextInt(1000000);


        Order order = new Order();

        order.setId(id);
        order.setCustomerId(customerId);
        order.setName(productName);
        order.setQty(random.nextInt(5) + 1);
        order.setCategory(categoryId);
        order.setState(stateId);
        order.setOrderDate(System.currentTimeMillis());

        return order;
    }

}