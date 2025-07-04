package com.Rest.CategoryProduct.pubsub.brand;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.stereotype.Service;

@Service
public class BrandPubSubPublisher {

    private final PubSubTemplate pubSubTemplate;

    public BrandPubSubPublisher(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    public void publish(String message){
        pubSubTemplate.publish("yoj-brand-topic",message);
    }
}
