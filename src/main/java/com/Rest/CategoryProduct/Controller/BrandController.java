package com.Rest.CategoryProduct.Controller;

import com.Rest.CategoryProduct.Entity.Brand;
import com.Rest.CategoryProduct.Service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    //Get single category
    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable Long id){
        Brand status = brandService.getBrandById(id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    //Get All category
    @GetMapping
    public ResponseEntity<List<Brand>> getAllBrand(){
        List<Brand> brands = brandService.getAllBrand();
        return new ResponseEntity<>(brands, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createBrand(@RequestBody Brand brand){
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

    @PutMapping("/{id}")
    public ResponseEntity<String> updateBrand(@RequestBody Brand brand, @PathVariable Long id){
        String status = brandService.updateBrand(brand,id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable Long id){
        String status = brandService.deleteBrand(id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

/*    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = Exception.class)
    public String generalExceptionHandler(){
        return "Exception is occurred";
    }*/
}
