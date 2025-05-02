package com.Rest.CategoryProduct.pubsub;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.stereotype.Service;

@Service
public class CategoryPubSubPublisher {
    private final PubSubTemplate pubSubTemplate;

    public CategoryPubSubPublisher(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    public void publish(String message){
        pubSubTemplate.publish("yoj-category-topic", message);
    }
}
