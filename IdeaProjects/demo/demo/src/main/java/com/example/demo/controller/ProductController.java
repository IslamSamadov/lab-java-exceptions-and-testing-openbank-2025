package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    private void validateApiKey(String apiKey) {
        if (!"123456".equals(apiKey)) {
            throw new RuntimeException("UNAUTHORIZED: Invalid or missing API-Key");
        }
    }

    @PostMapping
    public Product addProduct(@RequestHeader("API-Key") String key,@Valid @RequestBody Product product){
        validateApiKey(key);
        return productService.addProduct(product);
    }

    @GetMapping
    public List<Product> getProducts(){
        return productService.getAllProducts();
    }

    @GetMapping("/{name}")
    public Product getProductByName(@PathVariable String name){
        return productService.getProductByName(name);
    }

    @PutMapping("/{name}")
    public Product updateProduct(@PathVariable String name,@Valid @RequestBody Product product){
        return productService.updateProduct(name, product);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteProduct(@RequestHeader("API-Key") String key,@PathVariable String name){
        validateApiKey(key);
        productService.deleteProduct(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category")
    public List<Product> getProductsByCategory(@RequestParam String category){
        return productService.getProductsByCategory(category);
    }

    @GetMapping("/price")
    public List<Product> getProductsByPrice(@RequestParam double min, @RequestParam double max){
        return productService.getProductsByPriceRange(min, max);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeExceptions(RuntimeException ex) {
        if (ex.getMessage().startsWith("UNAUTHORIZED")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
