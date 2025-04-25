package com.Rest.CategoryProduct.pubsub;

import org.springframework.stereotype.Component;

@Component
public class ProductSubscription {

    public void receiveMessage(String message){
        System.out.println("Received message from product service : "+ message);
    }
}
