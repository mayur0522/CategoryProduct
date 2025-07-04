package com.Rest.CategoryProduct.pubsub.category;
public interface CategoryPubSubSubscriber {
    public void subscribeToCategoryEvents();
    void handleMessage(String message);
}
