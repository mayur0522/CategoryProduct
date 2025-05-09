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
    @Counted(value = "category_get_single_count", description = "Count of get single category calls")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    @Tag(name = "Retrieve single category", description = "Used to get the single category")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id){
        meterRegistry.counter("get_category_requests_total", "endpoint", "/category/{id}", "method", "GET").increment();
        Category status = categorySer.getCategoryById(id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    //Get All category
    @Counted(value = "category.get.all.count", description = "Count of get all category calls")
    @GetMapping
    @PreAuthorize("hasRole('Admin')")
    @Tag(name = "Retrieve all category", description = "Used to get all the category")
    public ResponseEntity<List<Category>> getAllCategory(){
        List<Category> category = categorySer.getAllCategory();
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @Timed(value = "category.create.time", description = "Time taken to create a category")
    @PostMapping
    @Tag(name = "Create new category", description = "It is used to create new category")
    public ResponseEntity<String> createCategory(@RequestBody Category category){
        String status = categorySer.insertCategory(category);
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    @Counted(value = "category.update.count", description = "Count of update category calls")
    @PutMapping("/{id}")
    @Tag(name = "Update category", description = "It is used to update an existing category")
    public ResponseEntity<String> updateCategory(@RequestBody Category category, @PathVariable Long id){
        String status = categorySer.updateCategory(category,id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @Counted(value = "category.delete.count", description = "Count of delete category calls")
    @DeleteMapping("/{id}")
    @Tag(name = "Delete category", description = "It is used to delete category")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id){
            String status = categorySer.deleteCategory(id);
            return new ResponseEntity<>(status, HttpStatus.OK);
    }

/*    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = Exception.class)
    public String generalExceptionHandler(){
        return "Exception is occurred";
    }*/

}