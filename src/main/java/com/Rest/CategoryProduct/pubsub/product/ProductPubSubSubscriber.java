package com.Rest.CategoryProduct.pubsub.product;
public interface ProductPubSubSubscriber {
    public void subscribeToProductEvents();

    void handleMessage(String message);
}
