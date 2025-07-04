package com.Rest.CategoryProduct.pubsub.product;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "pubsub.enabled", havingValue = "true")
public class GcpProductPubSubSubscriber implements ProductPubSubSubscriber{

    private final PubSubTemplate pubSubTemplate;

    public GcpProductPubSubSubscriber(PubSubTemplate pubSubTemplate) {
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

    public void handleMessage(String message) {
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
