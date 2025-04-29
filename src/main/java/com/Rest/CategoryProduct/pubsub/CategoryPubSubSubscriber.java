package com.Rest.CategoryProduct.pubsub;

import com.google.api.client.util.Value;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class CategoryPubSubSubscriber {
    private final PubSubTemplate pubSubTemplate;

    public CategoryPubSubSubscriber(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    @PostConstruct
    public void subscribeToCategoryEvents() {
        // Subscribe to the Pub/Sub topic and processing the message
        pubSubTemplate.subscribe("yoj-category-topic-sub", (BasicAcknowledgeablePubsubMessage message) -> {
            String payload = new String(message.getPubsubMessage().getData().toByteArray());
            handleMessage(payload);
            message.ack(); // Acknowledging the message after processing
        });
    }

    private void handleMessage(String message) {
        // Log and process the message
        try {
            JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

            String eventType = jsonObject.get("event").getAsString();

//            if ("CATEGORY_CREATED".equals(eventType)) {
//                // Handle the product created event
//                System.out.println("Handling event: " + eventType + ", Category ID: " + categoryId + ", Category Name: " + categoryName);
//            }

            switch (eventType){
                case "GET_All_CATEGORIES":
                    int count = jsonObject.has("Count") ? jsonObject.get("Count").getAsInt() : 0;
                    System.out.println("Handling event: " + eventType + ", Category Count: "+count);
                    break;
                case "CATEGORY_CREATED":
                case "GET_CATEGORY":
                case "CATEGORY_UPDATED":
                case "CATEGORY_DELETED":
                    long categoryId = jsonObject.get("id").getAsLong();
                    String categoryName = jsonObject.get("name").getAsString();
                    System.out.println("Handling event: " + eventType + ", Category ID: " + categoryId + ", Category Name: " + categoryName);
                    break;
                default:
                    System.out.println("Unhandled event type: " + eventType);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
}
