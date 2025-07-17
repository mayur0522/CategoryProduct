package com.Rest.CategoryProduct.kafka.product;

import com.Rest.CategoryProduct.Entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

//@Service
@Slf4j
@Component
public class ProductKafkaConsumer {

    @KafkaListener(topics = "product-topic", groupId = "ecommerce-group", containerFactory = "productKafkaListenerFactory")
    public void consumer(Product product){
        log.info("Received message from product (Through Kafka - ecommerce-group): "+product);
    }
}
