package com.Rest.CategoryProduct.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Category product application")
                        .version("1.0")
                        .description("REST API application build using Spring Boot - performing CRUD operation for Category, Brand and Product\n"));
    }
}
