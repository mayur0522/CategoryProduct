package com.Rest.CategoryProduct.kafka.category;

import com.Rest.CategoryProduct.Entity.Category;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CategoryKafkaProducer {
    private final KafkaTemplate<String, Category> kafkaTemplate;

    public CategoryKafkaProducer(KafkaTemplate<String, Category> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCategory(Category category){
        kafkaTemplate.send("category-topic",category);
    }
}
