package com.Rest.CategoryProduct.Controller;

import com.Rest.CategoryProduct.Entity.Product;
import com.Rest.CategoryProduct.Service.ProductService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final MeterRegistry meterRegistry;
    @Autowired
    private ProductService productService;

    public ProductController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id){
        meterRegistry.counter("product_requests_total", "operation", "get_single", "method", "GET").increment();
        Product status = productService.getProductById(id);
        return new ResponseEntity<>(status,HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(){
        meterRegistry.counter("product_requests_total", "operation", "get_all", "method", "GET").increment();
        List<Product> status = productService.getAllProducts();
        return  new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody Product product){
        meterRegistry.counter("product_requests_total", "operation", "create", "method", "POST").increment();
        String status = productService.insertProduct(product);
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@RequestBody Product product,@PathVariable Long id){
        meterRegistry.counter("product_requests_total", "operation", "update", "method", "PUT").increment();
        String status = productService.updateProduct(product,id);
        return  new ResponseEntity<>(status, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id){
        meterRegistry.counter("product_requests_total", "operation", "delete", "method", "DELETE").increment();
        String status = productService.deleteProduct(id);
            return new ResponseEntity<>(status, HttpStatus.OK);
    }

/*    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = Exception.class)
    public String generalExceptionHandler(){
        return "Exception is occurred";
    }*/
}
