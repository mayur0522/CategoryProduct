package com.Rest.CategoryProduct.kafka.brand;

import com.Rest.CategoryProduct.Entity.Brand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BrandKafkaConsumer {
    @KafkaListener(
            topics = "brand-topic",
            groupId = "ecommerce-group",
            containerFactory = "brandKafkaListenerFactory"
    )
    public void listenBrand(Brand brand) {
        log.info("Received Brand: (Through kafka): " + brand);
    }
}
