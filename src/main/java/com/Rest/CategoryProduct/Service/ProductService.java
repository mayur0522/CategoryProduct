package com.Rest.CategoryProduct.Service;

import com.Rest.CategoryProduct.Entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {

    public Product getProductById(Long id);

    public List<Product> getAllProducts();

    public String insertProduct(Product product);

    public String updateProduct(Product product,Long id);

    public String deleteProduct(Long id);

}
