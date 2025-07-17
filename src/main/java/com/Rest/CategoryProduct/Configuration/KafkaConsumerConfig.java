package com.Rest.CategoryProduct.Configuration;

import com.Rest.CategoryProduct.Entity.Brand;
import com.Rest.CategoryProduct.Entity.Category;
import com.Rest.CategoryProduct.Entity.Product;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    // 👇 Common consumer config for all types
    private Map<String, Object> baseConsumerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return config;
    }

    // Product Consumer Config
    @Bean
    public ConsumerFactory<String, Product> productConsumerFactory() {
        Map<String, Object> config = new HashMap<>(baseConsumerConfig());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<Product> deserializer = new JsonDeserializer<>(Product.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Product> productKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Product> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productConsumerFactory());
        return factory;
    }

    // Category Consumer Config
    @Bean
    public ConsumerFactory<String, Category> categoryConsumerFactory() {
        Map<String, Object> config = new HashMap<>(baseConsumerConfig());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<Category> deserializer = new JsonDeserializer<>(Category.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Category> categoryKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Category> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(categoryConsumerFactory());
        return factory;
    }

    // Brand Consumer Config
    @Bean
    public ConsumerFactory<String, Brand> brandConsumerFactory() {
        Map<String, Object> config = new HashMap<>(baseConsumerConfig());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<Brand> deserializer = new JsonDeserializer<>(Brand.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Brand> brandKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Brand> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(brandConsumerFactory());
        return factory;
    }
}
