package com.example.demo;

import com.example.demo.controller.ProductController;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
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

import java.util.List;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService  productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getProductsByPriceRange_Success() throws Exception {
        Product product = new Product("Laptop",2000.0,"Electronics",4);
        when(productService.getProductsByPriceRange(1000.0,3000.0)).thenReturn(List.of(product));

        mockMvc.perform(get("/api/products/price")
                        .param("min", "1000.0")
                        .param("max", "3000.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    public void deleteProduct_Unauthorized_Returns401() throws Exception {
        mockMvc.perform(delete("/api/products/iPhone")
                        .header("API-Key", "132234"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void addProduct_InvalidPrice_Returns400() throws Exception {
        Product invalidProduct = new Product("Pinky", -10.0, "Cat", 1);

        mockMvc.perform(post("/api/products")
                        .header("API-Key", "123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").exists());
    }

    @Test
    public void updateProduct_NotFound_Returns404() throws Exception {
        when(productService.updateProduct(eq("Unknown"), any(Product.class)))
                .thenThrow(new RuntimeException("Product not found"));

        Product product = new Product("Pinky", 10.0, "Cat", 1);

        mockMvc.perform(put("/api/products/Unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found"));
    }
}
