package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    // Dependency Injection
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    private void checkApiKey(String key) {
        if (!"123456".equals(key)) {
            throw new RuntimeException("UNAUTHORIZED");
        }
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestHeader("API-Key") String key, @Valid @RequestBody Customer customer) {
        checkApiKey(key);
        Customer created = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{email}")
    public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email) {
        return customerService.getCustomerByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{email}")
    public ResponseEntity<Customer> updateCustomer(@RequestHeader("API-Key") String key,
                                                   @PathVariable String email,
                                                   @Valid @RequestBody Customer customer) {
        checkApiKey(key);
        Customer updated = customerService.updateCustomer(email, customer);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteCustomer(@RequestHeader("API-Key") String key, @PathVariable String email) {
        checkApiKey(key);
        boolean removed = customerService.deleteCustomer(email);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Exception Handler-lər eyni qalır...
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(f -> errors.put(f.getField(), f.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleAuthError(RuntimeException ex) {
        if ("UNAUTHORIZED".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API Key");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}