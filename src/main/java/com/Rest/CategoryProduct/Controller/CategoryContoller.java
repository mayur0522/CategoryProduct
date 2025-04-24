package com.Rest.CategoryProduct.Controller;

import com.Rest.CategoryProduct.Entity.Category;
import com.Rest.CategoryProduct.Service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryContoller {

    @Autowired
    private CategoryService categorySer;

    //Get single category
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    @Tag(name = "Retrieve single category", description = "Used to get the single category")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id){
        Category status = categorySer.getCategoryById(id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    //Get All category
    @GetMapping
    @PreAuthorize("hasRole('Admin')")
    @Tag(name = "Retrieve all category", description = "Used to get all the category")
    public ResponseEntity<List<Category>> getAllCategory(){
        List<Category> category = categorySer.getAllCategory();
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PostMapping
    @Tag(name = "Create new category", description = "It is used to create new category")
    public ResponseEntity<String> createCategory(@RequestBody Category category){
        String status = categorySer.insertCategory(category);
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Tag(name = "Update category", description = "It is used to update an existing category")
    public ResponseEntity<String> updateCategory(@RequestBody Category category, @PathVariable Long id){
        String status = categorySer.updateCategory(category,id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Tag(name = "Delete category", description = "It is used to delete category")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id){
            String status = categorySer.deleteCategory(id);
            return new ResponseEntity<>(status, HttpStatus.OK);
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(value = Exception.class)
//    public String generalExceptionHandler(){
//        return "Exception is occurred";
//    }

}