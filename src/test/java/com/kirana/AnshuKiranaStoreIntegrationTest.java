package com.kirana;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
public class AnshuKiranaStoreIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("1. Store Configuration API should return store details and villages")
    void testGetStoreConfig() throws Exception {
        mockMvc.perform(get("/api/store/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Anshu Kirana Store")))
                .andExpect(jsonPath("$.data.currency", is("₹")))
                .andExpect(jsonPath("$.data.serviceableVillages", hasItem("Repudi Village")));
    }

    @Test
    @DisplayName("2. Categories API should return active grocery categories")
    void testGetCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", not(empty())));
    }

    @Test
    @DisplayName("3. Products API should return paginated products and support search")
    void testGetProductsAndSearch() throws Exception {
        // All active products
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", not(empty())));

        // Keyword search for 'Rice'
        mockMvc.perform(get("/api/products?keyword=Rice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content[0].nameEn", containsStringIgnoringCase("Rice")));
    }

    @Test
    @DisplayName("4. Customer & Admin Login should authenticate with valid credentials")
    void testAdminAndCustomerLogin() throws Exception {
        // Admin login test
        String adminLoginJson = "{\"identifier\":\"admin@anshukirana.com\",\"password\":\"admin123\"}";
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminLoginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.user.roles", hasItem("ROLE_ADMIN")));

        // Customer login test
        String customerLoginJson = "{\"identifier\":\"9876543211\",\"password\":\"customer123\"}";
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerLoginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.user.fullName", is("Venkat Rao")));
    }

    @Test
    @DisplayName("5. Customer Registration should create user and return JWT")
    void testCustomerRegistration() throws Exception {
        String randomPhone = "9" + (int)(Math.random() * 900000000 + 100000000);
        String registerJson = "{"
                + "\"fullName\":\"Subba Reddy\","
                + "\"phone\":\"" + randomPhone + "\","
                + "\"email\":\"subba." + randomPhone + "@village.com\","
                + "\"password\":\"pass12345\","
                + "\"preferredLanguage\":\"te\""
                + "}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.user.fullName", is("Subba Reddy")));
    }
}
