package com.example.demo;

import com.example.demo.controller.CustomerController;
import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;

import java.util.Optional;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createCustomer_Success() throws Exception {
        Customer customer = new Customer("Islam","islam@mail.com",20,"Sumqayit");
        when(customerService.createCustomer(any(Customer.class))).thenReturn(customer);

        mockMvc.perform(post("/api/customers")
                        .header("API-Key", "123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ali"));
    }

    @Test
    public void createCustomer_Unauthorized_Returns401() throws Exception {
        Customer customer = new Customer("Islam","islam@mail.com",20,"Sumqayit");
        when(customerService.createCustomer(any(Customer.class))).thenReturn(customer);

        mockMvc.perform(post("/api/customers")
                        .header("API-Key", "123456789")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid API Key"));
    }

    @Test
    public void getCustomerByEmail_NotFound_Returns404() throws Exception {
        when(customerService.getCustomerByEmail("not_existing_email@mail.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/customers/not_existing_email@mail.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createCustomer_InvalidData_Returns400() throws Exception {
        Customer invalidCustomer = new Customer("","not_an_email",10, "Baku");
        mockMvc.perform(post("/api/customers")
                .header("API-Key", "123456")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCustomer)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.name").exists())
        .andExpect(jsonPath("$.email").exists())
        .andExpect(jsonPath("$.age").exists());;
    }
}
