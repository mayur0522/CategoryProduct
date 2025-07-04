package com.Rest.CategoryProduct.pubsub.brand;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "pubsub.enabled", havingValue = "true")
public class GcpBrandPubSubPublisher implements BrandPubSubPublisher{
    private final PubSubTemplate pubSubTemplate;

    public GcpBrandPubSubPublisher(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    public void publish(String message){
        pubSubTemplate.publish("yoj-brand-topic",message);
    }
}
