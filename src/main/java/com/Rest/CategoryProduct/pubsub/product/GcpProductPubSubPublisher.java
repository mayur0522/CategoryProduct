package com.Rest.CategoryProduct.pubsub.product;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "pubsub.enabled", havingValue = "true")
public class GcpProductPubSubPublisher implements ProductPubSubPublisher{
    private final PubSubTemplate pubSubTemplate;

    public GcpProductPubSubPublisher(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    public void publish(String message){
        pubSubTemplate.publish("yoj-product-topic", message);
    }
}
