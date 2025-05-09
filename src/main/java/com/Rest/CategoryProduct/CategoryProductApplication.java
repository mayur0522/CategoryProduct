package com.Rest.CategoryProduct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class CategoryProductApplication {
	public static void main(String[] args) {
		SpringApplication.run(CategoryProductApplication.class, args);
	}
}