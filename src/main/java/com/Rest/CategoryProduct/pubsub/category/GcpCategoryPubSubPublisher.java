package com.Rest.CategoryProduct.pubsub.category;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "pubsub.enabled", havingValue = "true")
public class GcpCategoryPubSubPublisher implements CategoryPubSubPublisher{
    private final PubSubTemplate pubSubTemplate;

    public GcpCategoryPubSubPublisher(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    public void publish(String message){
        pubSubTemplate.publish("yoj-category-topic", message);
    }
}
