package com.Rest.CategoryProduct.kafka.brand;

import com.Rest.CategoryProduct.Entity.Brand;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BrandKafkaProducer {
    private final KafkaTemplate<String, Brand> kafkaTemplate;

    public BrandKafkaProducer(KafkaTemplate<String, Brand> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBrand(Brand brand){
        kafkaTemplate.send("brand-topic",brand);
    }
}
