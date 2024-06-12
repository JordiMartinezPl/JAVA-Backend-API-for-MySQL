package com.tecnocampus.groupfautorentapi;

import com.tecnocampus.groupfautorentapi.application.dto.CustomerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CustomerApiTest {

    @Autowired
    private MockMvc mockMvc;
    @Test
    public void testValidCustomerRegistration() throws Exception {
        String customerJson = "{ \"fullName\": \"John Doe\", \"address\": \"123 Main St\", \"dateBirth\": \"2000-01-20\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("amountExpended").exists());
    }

    @Test
    public void testInvalidCustomerRegistrationUnderage() throws Exception {
        String customerJson = "{ \"fullName\": \"Jane Doe\", \"address\": \"123 Main St\", \"dateBirth\": \"10/01/2010\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testInvalidCustomerRegistrationMissingFields() throws Exception {
        String customerJson = "{ \"fullName\": \"John Doe\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testReadCustomer() throws Exception {

        String customerJson = "{ \"fullName\": \"John Doe\", \"address\": \"123 Main St\", \"dateBirth\": \"2000-12-10\" }";

        String json=mockMvc.perform(MockMvcRequestBuilders.post("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson)).andReturn().getResponse().getContentAsString();

        CustomerDTO customerDTO = new ObjectMapper().readValue(json, CustomerDTO.class);
        String id = customerDTO.getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/customer/{id}", customerDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerDTO.getId()));
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        String customerJson = "{ \"fullName\": \"John Doe\", \"address\": \"123 Main St\", \"dateBirth\": \"2000-01-20\" }";

        String json=mockMvc.perform(MockMvcRequestBuilders.post("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson)).andReturn().getResponse().getContentAsString();

        CustomerDTO customerDTO = new ObjectMapper().readValue(json, CustomerDTO.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/customer/{id}", customerDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.get("/customer/{id}", customerDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}

