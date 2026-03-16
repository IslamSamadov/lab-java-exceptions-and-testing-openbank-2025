package com.example.demo.service;


import com.example.demo.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    private final Map<String , Product> products = new HashMap<>();

    public Product addProduct(Product product) {
        products.put(product.getName(), product);
        return product;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public Product getProductByName(String name){
        return products.get(name);
    }

    public Product updateProduct(String name, Product updatedProduct){
        products.put(name, updatedProduct);
        return updatedProduct;
    }

    public void deleteProduct(String name){
        products.remove(name);
    }

    public List<Product> getProductsByCategory(String category){
        return new ArrayList<>(products.values().stream().filter(p -> p != null && p.getCategory().equalsIgnoreCase(category)).toList());
    }

    public List<Product> getProductsByPriceRange(double min, double max){
        return new ArrayList<>(products.values().stream()
                .filter(p -> p != null && p.getPrice() >= min && p.getPrice() <= max).toList());
    }
}
