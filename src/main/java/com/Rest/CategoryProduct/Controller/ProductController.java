package com.Rest.CategoryProduct.Controller;

import com.Rest.CategoryProduct.Entity.Product;
import com.Rest.CategoryProduct.Service.ProductService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
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

    @Counted(value = "product.read.single.count", description = "Count of get single product calls")
    @Timed(value = "product.read.single.time", description = "Time taken for getting a single product")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id){
//        meterRegistry.counter("product_requests_total", "operation", "get_single", "method", "GET").increment();
        Product status = productService.getProductById(id);
        return new ResponseEntity<>(status,HttpStatus.OK);
    }

    @Counted(value = "product.read.all.count", description = "Count of get all products calls")
    @Timed(value = "product.read.all.time", description = "Time taken for getting all products")
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(){
//        meterRegistry.counter("product_requests_total", "operation", "get_all", "method", "GET").increment();
        List<Product> status = productService.getAllProducts();
        return  new ResponseEntity<>(status, HttpStatus.OK);
    }

    @Counted(value = "product.create.count", description = "Count of product creation calls")
    @Timed(value = "product.create.time", description = "Time taken for product creation")
    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody Product product){
//        meterRegistry.counter("product_requests_total", "operation", "create", "method", "POST").increment();
        String status = productService.insertProduct(product);
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    @Counted(value = "product.update.count", description = "Count of product update calls")
    @Timed(value = "product.update.time", description = "Time taken for product update")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@RequestBody Product product,@PathVariable Long id){
//        meterRegistry.counter("product_requests_total", "operation", "update", "method", "PUT").increment();
        String status = productService.updateProduct(product,id);
        return  new ResponseEntity<>(status, HttpStatus.OK);
    }

    @Counted(value = "product.delete.count", description = "Count of product delete calls")
    @Timed(value = "product.delete.time", description = "Time taken for product deletion")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id){
//        meterRegistry.counter("product_requests_total", "operation", "delete", "method", "DELETE").increment();
        String status = productService.deleteProduct(id);
            return new ResponseEntity<>(status, HttpStatus.OK);
    }

/*    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = Exception.class)
    public String generalExceptionHandler(){
        return "Exception is occurred";
    }*/
}
