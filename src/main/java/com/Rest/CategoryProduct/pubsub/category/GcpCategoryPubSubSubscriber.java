package com.Rest.CategoryProduct.pubsub.category;

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
public class GcpCategoryPubSubSubscriber implements CategoryPubSubSubscriber{
    private final PubSubTemplate pubSubTemplate;

    public GcpCategoryPubSubSubscriber(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    @PostConstruct
    public void subscribeToCategoryEvents() {
        // Subscribe to the Pub/Sub topic and processing the message
        pubSubTemplate.subscribe("yoj-category-topic-sub", (BasicAcknowledgeablePubsubMessage message) -> {
            String payload = new String(message.getPubsubMessage().getData().toByteArray());
            try {
                handleMessage(payload);
                message.ack(); // Acknowledging the message after processing
            } catch (Exception e) {
                log.error("Failed to process message ",e);
                message.nack();
            }
        });
    }

    public void handleMessage(String message) {
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
                    log.info("Handling event: " + eventType + ", Category Count: "+count);
                    break;
                case "CATEGORY_CREATED":
                case "GET_CATEGORY":
                case "CATEGORY_UPDATED":
                case "CATEGORY_DELETED":
                    long categoryId = jsonObject.get("id").getAsLong();
                    String categoryName = jsonObject.get("name").getAsString();
                    log.info("Handling event: " + eventType + ", Category ID: " + categoryId + ", Category Name: " + categoryName);
                    break;
                default:
                    log.warn("Unhandled event type: " + eventType);
            }
        } catch (Exception e) {
            log.error("Error processing message: " + e.getMessage());
        }
    }
}
