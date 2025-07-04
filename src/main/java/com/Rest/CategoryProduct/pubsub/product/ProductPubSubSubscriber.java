package com.Rest.CategoryProduct.pubsub.product;

import com.google.api.client.util.Value;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class ProductPubSubSubscriber {

    private final PubSubTemplate pubSubTemplate;

    public ProductPubSubSubscriber(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    @PostConstruct
    public void subscribeToProductEvents() {
        // Subscribe to the Pub/Sub topic and process the message
        pubSubTemplate.subscribe("yoj-product-topic-sub", (BasicAcknowledgeablePubsubMessage message) -> {
            String payload = new String(message.getPubsubMessage().getData().toByteArray());
            handleMessage(payload);
            message.ack(); // Acknowledge the message after processing
        });
    }

    private void handleMessage(String message) {
        // Log and process the message
        try {
            JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

            String eventType = jsonObject.get("event").getAsString();

//            if ("PRODUCT_CREATED".equals(eventType)) {
//                // Handle the product created event
//                System.out.println("Handling event: " + eventType + ", Product ID: " + productId + ", Product Name: " + productName);
//            }

            switch (eventType){
                case "GET_All_PRODUCTS":
                    int count = jsonObject.has("Count") ? jsonObject.get("Count").getAsInt() : 0;
                    System.out.println("Handling event: " + eventType + ", Product Count: "+count);
                    break;
                case "PRODUCT_CREATED":
                case "GET_PRODUCT":
                case "PRODUCT_UPDATED":
                case "PRODUCT_DELETED":
                    long productId = jsonObject.has("id") ? jsonObject.get("id").getAsLong() : -1;
                    String productName = jsonObject.has("name") ? jsonObject.get("name").getAsString() : "N/A";
                    System.out.println("Handling event: " + eventType + ", Product ID: " + productId + ", Product Name: " + productName);
                    break;
                default:
                    System.out.println("Unhandled event type: " + eventType);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
}
