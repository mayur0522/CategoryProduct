package com.Rest.CategoryProduct.kafka.category;

import com.Rest.CategoryProduct.Entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryKafkaConsumer {

    @KafkaListener(
            topics = "category-topic",
            groupId = "ecommerce-group",
            containerFactory = "categoryKafkaListenerFactory"
    )
    public void listenCategory(Category category) {
        log.info("Received Category (Through kafka): " + category);
    }
}