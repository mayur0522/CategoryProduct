package com.Rest.CategoryProduct.kafka.product;

import com.Rest.CategoryProduct.Entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

//@Service
@Slf4j
@Component
public class ProducerKafkaConsumer2 {

    @KafkaListener(topics = "product-topic", groupId = "product-group", containerFactory = "productKafkaListenerFactory")
    public  void consumer2(Product product){
        log.info("Received message from product (Through Kafka - product-group): "+product);
    }
}
