package com.Rest.CategoryProduct.pubsub.common;

import com.Rest.CategoryProduct.pubsub.brand.BrandPubSubSubscriber;
import com.Rest.CategoryProduct.pubsub.category.CategoryPubSubSubscriber;
import com.Rest.CategoryProduct.pubsub.product.ProductPubSubSubscriber;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "pubsub.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpPubSubSubscriber implements BrandPubSubSubscriber, CategoryPubSubSubscriber, ProductPubSubSubscriber {
    @Override
    public void subscribeToBrandEvents() {
        System.out.println("Pub/Sub disabled — skipping Brand subscription.");
    }

    @Override
    public void subscribeToCategoryEvents() {
        System.out.println("Pub/Sub disabled — skipping Category subscription.");
    }

    @Override
    public void subscribeToProductEvents() {
        System.out.println("Pub/Sub disabled — skipping Product subscription.");
    }

    @Override
    public void handleMessage(String message) {
        System.out.println("Pub/Sub disabled — skipping handling message: " + message);
    }
}
