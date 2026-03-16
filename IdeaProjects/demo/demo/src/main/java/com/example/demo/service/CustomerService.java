package com.example.demo.service;

import com.example.demo.model.Customer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final List<Customer> customers = new ArrayList<>();

    public Customer createCustomer(Customer customer) {
        customers.add(customer);
        return customer;
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    public Optional<Customer> getCustomerByEmail(String email) {
        return customers.stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Customer updateCustomer(String email, Customer updatedCustomer) {
        // Email-ə görə axtarış etmək daha məntiqlidir, çünki ID rolunu o oynayır
        Optional<Customer> existingCustomer = getCustomerByEmail(email);

        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
            customer.setName(updatedCustomer.getName());
            customer.setAge(updatedCustomer.getAge());
            customer.setAddress(updatedCustomer.getAddress());
            // Email dəyişdirilməməlidir və ya xüsusi məntiqlə dəyişməlidir
            return customer;
        }
        return null;
    }

    public boolean deleteCustomer(String email) {
        return customers.removeIf(c -> c.getEmail().equalsIgnoreCase(email));
    }
}