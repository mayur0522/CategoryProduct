package com.Rest.CategoryProduct.Configuration;

import com.Rest.CategoryProduct.Entity.Brand;
import com.Rest.CategoryProduct.Entity.Category;
import com.Rest.CategoryProduct.Entity.Product;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    // 🔁 Common base config
    private Map<String, Object> baseConfigs() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProps;
    }

    // Kafka Config for Product
    @Bean
    public ProducerFactory<String, Product> productProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseConfigs());
    }

    @Bean
    public KafkaTemplate<String, Product> productKafkaTemplate() {
        return new KafkaTemplate<>(productProducerFactory());
    }

    // Kafka Config for Category
    @Bean
    public ProducerFactory<String, Category> categoryProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseConfigs());
    }

    @Bean
    public KafkaTemplate<String, Category> categoryKafkaTemplate() {
        return new KafkaTemplate<>(categoryProducerFactory());
    }

    // Kafka Config for Brand
    @Bean
    public ProducerFactory<String, Brand> brandProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseConfigs());
    }

    @Bean
    public KafkaTemplate<String, Brand> brandKafkaTemplate() {
        return new KafkaTemplate<>(brandProducerFactory());
    }
}
