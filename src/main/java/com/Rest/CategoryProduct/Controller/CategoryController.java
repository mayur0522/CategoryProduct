package com.Rest.CategoryProduct.Controller;

import com.Rest.CategoryProduct.Entity.Category;
import com.Rest.CategoryProduct.Service.CategoryService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final MeterRegistry meterRegistry;

    public CategoryController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Autowired
    private CategoryService categorySer;

    //Get single category
    @Counted(value = "category.read.single.count", description = "Count of get single category calls")
    @Timed(value = "category.read.single.time", description = "Time taken for getting a single category")    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    @Tag(name = "Retrieve single category", description = "Used to get the single category")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id){
        Category status = categorySer.getCategoryById(id);
//        meterRegistry.counter("get_category_requests_total", "endpoint", "/category/{id}", "method", "GET").increment();
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    //Get All category
    @Counted(value = "category.read.all.count", description = "Count of get all categories calls")
    @Timed(value = "category.read.all.time", description = "Time taken for getting all categories")    @GetMapping
    @PreAuthorize("hasRole('Admin')")
    @Tag(name = "Retrieve all category", description = "Used to get all the category")
    public ResponseEntity<List<Category>> getAllCategory(){
//        meter registry
//        meterRegistry.counter("category_requests_total", "endpoint", "/category", "method", "GET").increment();
        List<Category> category = categorySer.getAllCategory();
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @Counted(value = "category.create.count", description = "Count of category creation calls")
    @Timed(value = "category.create.time", description = "Time taken for category creation")
    @PostMapping
    @Tag(name = "Create new category", description = "It is used to create new category")
    public ResponseEntity<String> createCategory(@RequestBody Category category){
//        meterRegistry.counter("category_requests_total", "endpoint", "/category", "method", "POST").increment();
        String status = categorySer.insertCategory(category);
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    @Counted(value = "category.update.count", description = "Count of category update calls")
    @Timed(value = "category.update.time", description = "Time taken for category update")
    @PutMapping("/{id}")
    @Tag(name = "Update category", description = "It is used to update an existing category")
    public ResponseEntity<String> updateCategory(@RequestBody Category category, @PathVariable Long id){
//        meterRegistry.counter("category_requests_total", "endpoint", "/category/{id}", "method", "PUT").increment();
        String status = categorySer.updateCategory(category,id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @Counted(value = "category.delete.count", description = "Count of category delete calls")
    @Timed(value = "category.delete.time", description = "Time taken for category deletion")
    @DeleteMapping("/{id}")
    @Tag(name = "Delete category", description = "It is used to delete category")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id){
//        meterRegistry.counter("category_requests_total", "endpoint", "/category/{id}", "method", "DELETE").increment();
        String status = categorySer.deleteCategory(id);
            return new ResponseEntity<>(status, HttpStatus.OK);
    }

/*    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = Exception.class)
    public String generalExceptionHandler(){
        return "Exception is occurred";
    }*/

}