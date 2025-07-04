package com.Rest.CategoryProduct.pubsub.common;

import com.Rest.CategoryProduct.pubsub.brand.BrandPubSubPublisher;
import com.Rest.CategoryProduct.pubsub.category.CategoryPubSubPublisher;
import com.Rest.CategoryProduct.pubsub.product.ProductPubSubPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "pubsub.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpPubSubPublisher implements BrandPubSubPublisher, CategoryPubSubPublisher, ProductPubSubPublisher {
    @Override
    public void publish(String message) {
        System.out.println("Pub/Sub is disabled. Skipping publish: " + message);
    }
}
