package com.Rest.CategoryProduct.kafka.product;

import com.Rest.CategoryProduct.Entity.Product;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductKafkaProducer {

    private final KafkaTemplate<String, Product> kafkaTemplate;

    public ProductKafkaProducer(KafkaTemplate<String, Product> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendProduct(Product product){
        kafkaTemplate.send("product-topic", product);
    }
}
