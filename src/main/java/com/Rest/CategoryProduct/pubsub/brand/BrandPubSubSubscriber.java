package com.Rest.CategoryProduct.pubsub.brand;
public interface BrandPubSubSubscriber {
    public void subscribeToBrandEvents();
    void handleMessage(String message);
}
