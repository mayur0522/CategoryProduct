package com.Rest.CategoryProduct.Controller;

import com.Rest.CategoryProduct.Entity.Brand;
import com.Rest.CategoryProduct.Service.BrandService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    private final MeterRegistry meterRegistry;
    @Autowired
    private BrandService brandService;

    public BrandController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    //Get single category
    @Counted(value = "brand.read.single.count", description = "Count of get single brand calls")
    @Timed(value = "brand.read.single.time", description = "Time taken for getting a single brand")
    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable Long id){
        meterRegistry.counter("brand_requests_total", "operation", "get_single", "method", "GET").increment();
        Brand status = brandService.getBrandById(id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    //Get All category
    @Counted(value = "brand.read.all.count", description = "Count of get all brands calls")
    @Timed(value = "brand.read.all.time", description = "Time taken for getting all brands")
    @GetMapping
    public ResponseEntity<List<Brand>> getAllBrand(){
        meterRegistry.counter("brand_requests_total", "operation", "get_all", "method", "GET").increment();
        List<Brand> brands = brandService.getAllBrand();
        return new ResponseEntity<>(brands, HttpStatus.OK);
    }

    @Counted(value = "brand.create.count", description = "Count of brand creation calls")
    @Timed(value = "brand.create.time", description = "Time taken for brand creation")
    @PostMapping
    public ResponseEntity<String> createBrand(@RequestBody Brand brand){
        meterRegistry.counter("brand_requests_total", "operation", "create", "method", "POST").increment();
        String status = brandService.insertBrand(brand);
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

/*    @PostMapping
    public ResponseEntity<String> createBrand(@RequestBody Map<String, Object> rb){
        String name = (String) rb.get("brandName");
        Long categoryId = Long.valueOf(rb.get("categoryId").toString());
        String status = brandService.insertBrand(name, categoryId);
        return  new ResponseEntity<>(status, HttpStatus.CREATED);
    }*/

    @Counted(value = "brand.update.count", description = "Count of brand update calls")
    @Timed(value = "brand.update.time", description = "Time taken for brand update")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateBrand(@RequestBody Brand brand, @PathVariable Long id){
        meterRegistry.counter("brand_requests_total", "operation", "update", "method", "PUT").increment();
        String status = brandService.updateBrand(brand,id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @Counted(value = "brand.delete.count", description = "Count of brand delete calls")
    @Timed(value = "brand.delete.time", description = "Time taken for brand deletion")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable Long id){
        meterRegistry.counter("brand_requests_total", "operation", "delete", "method", "DELETE").increment();
        String status = brandService.deleteBrand(id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

/*    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = Exception.class)
    public String generalExceptionHandler(){
        return "Exception is occurred";
    }*/
}
