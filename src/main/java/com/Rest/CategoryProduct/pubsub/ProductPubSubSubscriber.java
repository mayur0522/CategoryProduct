package com.Rest.CategoryProduct.pubsub;

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

    @Value("${pubsub.subscription.product}")
    private String subscriptionName;

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
            long productId = jsonObject.get("id").getAsLong();
            String productName = jsonObject.get("name").getAsString();

            if ("PRODUCT_CREATED".equals(eventType)) {
                // Handle the product created event
                System.out.println("Handling event: " + eventType + ", Product ID: " + productId + ", Product Name: " + productName);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
}
