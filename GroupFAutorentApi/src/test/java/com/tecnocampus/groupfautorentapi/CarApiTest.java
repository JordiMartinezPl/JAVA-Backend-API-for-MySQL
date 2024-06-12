package com.tecnocampus.groupfautorentapi;

import com.tecnocampus.groupfautorentapi.application.dto.CarDTO;
import com.tecnocampus.groupfautorentapi.application.dto.CustomerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CarApiTest {

    @Autowired
    private MockMvc mockMvc;

    private CustomerDTO customerDTO;
    private CarDTO carDTO;

    @BeforeEach
    public void setUp() throws Exception {
        String customerJson = "{ \"fullName\": \"John Doe\", \"address\": \"123 Main St\", \"dateBirth\": \"2000-01-01\" }";
        String json = mockMvc.perform(MockMvcRequestBuilders.post("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson)).andReturn().getResponse().getContentAsString();

        customerDTO = new ObjectMapper().readValue(json, CustomerDTO.class);


        String carJson = "{ \"licencePlateNumber\": \"ABC123\", \"brand\": \"Toyota\", \"model\": \"Corolla\", \"category\": \"ECONOMY\" }";
        json = mockMvc.perform(MockMvcRequestBuilders.post("/car")
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJson)).andReturn().getResponse().getContentAsString();
        carDTO = new ObjectMapper().readValue(json, CarDTO.class);
    }

    @Test
    public void testDeleteCar() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/car/{id}", carDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.get("/car/{id}", carDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testReadCar() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/car/{id}", carDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carDTO.getId()));
    }


    @Test
    public void testValidCarRegistration() throws Exception {
        String carJson = "{ \"licencePlateNumber\": \"CDR123\", \"brand\": \"Toyota\", \"model\": \"Corolla\", \"category\": \"MINI\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("category", new TypeSafeMatcher<String>() {

                    @Override
                    public void describeTo(Description description) {}

                    @Override
                    protected boolean matchesSafely(String item) {
                        return Arrays.asList("Economy", "Luxury", "Family", "Mini").stream()
                                .anyMatch(value -> value.equalsIgnoreCase(item));

                    }
                }));
    }

    @Test
    public void testInvalidCarRegistrationMissingFields() throws Exception {
        String carJson = "{ \"licencePlate\": \"ABC123\", \"brand\": \"Toyota\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().is4xxClientError());
    }
}
