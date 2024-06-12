package com.tecnocampus.groupfautorentapi;

import static org.hamcrest.Matchers.is;

import com.tecnocampus.groupfautorentapi.application.dto.BookingDTO;
import com.tecnocampus.groupfautorentapi.application.dto.CarDTO;
import com.tecnocampus.groupfautorentapi.application.dto.CustomerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.tecnocampus.groupfautorentapi.domain.BookingStatus;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookingApiTest {
    @Autowired
    private MockMvc mockMvc;
    private CustomerDTO customerDTO;
    private CarDTO carDTO;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00");

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
    public void testValidBooking() throws Exception {
        LocalDateTime firstDate = LocalDate.now().plusDays(2).atStartOfDay();
        LocalDateTime endDate = firstDate.plusDays(5);

        String bookingJson = String.format("{ \"customerId\": \"%s\", \"carId\": \"%s\", \"firstDate\": \"%s\", \"finalDate\": \"%s\" }",
                customerDTO.getId(), carDTO.getId(), firstDate.format(formatter), endDate.format(formatter));
        BookingDTO bookingDTO = new ObjectMapper().readValue(bookingJson, BookingDTO.class);


        mockMvc.perform(MockMvcRequestBuilders.post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void testInvalidBookingInvalidCar() throws Exception {
        LocalDateTime startDate = LocalDate.now().plusDays(2).atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(5);
        String bookingJson = String.format("{ \"customerId\": \"%s\", \"carId\": \"%s\", \"firstDate\": \"%s\", \"finalDate\": \"%s\" }",
                customerDTO.getId(), "bla bla bla", startDate.format(formatter), endDate.format(formatter));


        mockMvc.perform(MockMvcRequestBuilders.post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testInvalidBookingExceedsMaxDays() throws Exception {
        LocalDateTime startDate = LocalDate.now().plusDays(2).atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(21); // 21 days, which exceeds the limit

        String bookingJson = String.format("{ \"customerId\": \"%s\", \"carId\": \"%s\", \"firstDate\": \"%s\", \"finalDate\": \"%s\" }",
                customerDTO.getId(), carDTO.getId(), startDate.format(formatter), endDate.format(formatter));


        mockMvc.perform(MockMvcRequestBuilders.post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testBookingWithPastStartDate() throws Exception {
        LocalDateTime startDate = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(5);

        String bookingJson = String.format("{ \"customerId\": \"%s\", \"carId\": \"%s\", \"firstDate\": \"%s\", \"finalDate\": \"%s\" }",
                customerDTO.getId(), carDTO.getId(), startDate.format(formatter), endDate.format(formatter));


        mockMvc.perform(MockMvcRequestBuilders.post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testPricingCalculationEconomy() throws Exception {
        LocalDateTime startDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY).plusDays(7).atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(6); // Includes Friday, Saturday, and Sunday

        String bookingJson = String.format("{ \"customerId\": \"%s\", \"carId\": \"%s\", \"firstDate\": \"%s\", \"finalDate\": \"%s\" }",
                customerDTO.getId(), carDTO.getId(), startDate.format(formatter), endDate.format(formatter));

        mockMvc.perform(MockMvcRequestBuilders.post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rental").value(232.5/*240.0*/)); // Assuming Economy: 4 weekdays * 30 + 3 weekend * 30 * 1.25
        // Verify the amount expended is updated
        System.out.println(customerDTO.toString());
        mockMvc.perform(MockMvcRequestBuilders.get("/customer/{id}", customerDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountExpended", is(232.5)));
    }


    @Test
    public void testPricingCalculationLuxury() throws Exception {
        LocalDateTime startDate = LocalDate.now().with(DayOfWeek.MONDAY).plusDays(7).atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(6); // Includes Friday, Saturday, and Sunday

        String carJson = "{ \"licencePlateNumber\": \"LUX123\", \"brand\": \"Mercedes\", \"model\": \"S-Class\", \"category\": \"LUXURY\" }";
        String json = mockMvc.perform(MockMvcRequestBuilders.post("/car")
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJson)).andReturn().getResponse().getContentAsString();

        carDTO = new ObjectMapper().readValue(json, CarDTO.class);


        String bookingJson = String.format("{ \"customerId\": \"%s\", \"carId\": \"%s\", \"firstDate\": \"%s\", \"finalDate\": \"%s\" }",
                customerDTO.getId(), carDTO.getId(), startDate.format(formatter), endDate.format(formatter));


        mockMvc.perform(MockMvcRequestBuilders.post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rental").value(620.0)); // 4 weekdays * 80 + 3 weekend * 80 * 1.25

        // Verify the amount expended is updated

        mockMvc.perform(MockMvcRequestBuilders.get("/customer/{id}", customerDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountExpended", is(620.0)));
    }

    @Test
    public void testPricingCalculationFamily() throws Exception {
        LocalDateTime startDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY).plusDays(7).atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(6); // Includes Friday, Saturday, and Sunday

        String carJson = "{ \"licencePlateNumber\": \"FAM123\", \"brand\": \"Mercedes\", \"model\": \"S-Class\", \"category\": \"FAMILY\" }";
        String json = mockMvc.perform(MockMvcRequestBuilders.post("/car")
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJson)).andReturn().getResponse().getContentAsString();

        carDTO = new ObjectMapper().readValue(json, CarDTO.class);


        String bookingJson = String.format("{ \"customerId\": \"%s\", \"carId\": \"%s\", \"firstDate\": \"%s\", \"finalDate\": \"%s\" }",
                customerDTO.getId(), carDTO.getId(), startDate.format(formatter), endDate.format(formatter));


        mockMvc.perform(MockMvcRequestBuilders.post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rental").value(387.5)); // 3 weekdays * 50 + 3 weekend * 50 * 1.25

        // Verify the amount expended is updated
        mockMvc.perform(MockMvcRequestBuilders.get("/customer/{id}", customerDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountExpended", is(387.5)));
    }

    @Test
    public void testPricingCalculationMini() throws Exception {
        LocalDateTime startDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY).plusDays(7).atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(6); // Includes Friday, Saturday, and Sunday

        String carJson = "{ \"licencePlateNumber\": \"MINI123\", \"brand\": \"Mercedes\", \"model\": \"S-Class\", \"category\": \"MINI\" }";
        String json = mockMvc.perform(MockMvcRequestBuilders.post("/car")
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJson)).andReturn().getResponse().getContentAsString();

        carDTO = new ObjectMapper().readValue(json, CarDTO.class);

        String bookingJson = String.format("{ \"customerId\": \"%s\", \"carId\": \"%s\", \"firstDate\": \"%s\", \"finalDate\": \"%s\" }",
                customerDTO.getId(), carDTO.getId(), startDate.format(formatter), endDate.format(formatter));


        mockMvc.perform(MockMvcRequestBuilders.post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rental").value(155.0)); // 3 weekdays * 20 + 3 weekend * 20 * 1.25

        // Verify the amount expended is updated
        mockMvc.perform(MockMvcRequestBuilders.get("/customer/{id}", customerDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountExpended", is(155.0)));
    }


    @Test
    public void testCarAvailabilityOverlap() throws Exception {
        LocalDateTime startDate = LocalDate.now().plusDays(2).atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(5);

        String bookingJson = String.format("{ \"customerId\": \"%s\", \"carId\": \"%s\", \"firstDate\": \"%s\", \"finalDate\": \"%s\" }",
                customerDTO.getId(), carDTO.getId(), startDate.format(formatter), endDate.format(formatter));

        // First booking
        mockMvc.perform(MockMvcRequestBuilders.post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isCreated());

        // Attempt overlapping booking
        LocalDateTime overlapStartDate = startDate.plusDays(3);
        LocalDateTime overlapEndDate = endDate.plusDays(4);
        String overlapBookingJson = String.format("{ \"customerId\": \"%s\", \"carId\": \"%s\", \"firstDate\": \"%s\", \"finalDate\": \"%s\" }",
                customerDTO.getId(), carDTO.getId(), overlapStartDate.format(formatter), overlapEndDate.format(formatter));


        mockMvc.perform(MockMvcRequestBuilders.post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(overlapBookingJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testBookingCancellationWithinAllowedTimeframe() throws Exception {
        LocalDateTime startDate = LocalDate.now().plusDays(2).atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(5);

        String bookingJson = String.format("{ \"customerId\": \"%s\", \"carId\": \"%s\", \"firstDate\": \"%s\", \"finalDate\": \"%s\" }",
                customerDTO.getId(), carDTO.getId(), startDate.format(formatter), endDate.format(formatter));

        String json = mockMvc.perform(MockMvcRequestBuilders.post("/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson)).andReturn().getResponse().getContentAsString();

        System.out.println(json);

        BookingDTO bookingDTO = new ObjectMapper().readValue(json, BookingDTO.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/booking/{id}", bookingDTO.getId()))
                .andExpect(status().is4xxClientError());//
    }

    @Test
    public void testBookingCancellationLessThan24HoursBeforeStart() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().plus(23, ChronoUnit.HOURS);
        LocalDateTime endDate = startDate.plusDays(5);
        String bookingJson = String.format("{ \"customerId\": \"%s\", \"carId\": \"%s\", \"firstDate\": \"%s\", \"finalDate\": \"%s\" }",
                customerDTO.getId(), carDTO.getId(), startDate.format(formatter), endDate.format(formatter));


        String json = mockMvc.perform(MockMvcRequestBuilders.post("/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson)).andReturn().getResponse().getContentAsString();

        BookingDTO bookingDTO = new ObjectMapper().readValue(json, BookingDTO.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/booking/{id}", bookingDTO.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testCarPickup() throws Exception {
        // Create booking
        LocalDate startDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY).plusDays(7);
        LocalDate endDate = startDate.plusDays(6);
        String bookingJson = String.format("{ \"customerId\": \"%s\", \"carId\": \"%s\", \"firstDate\": \"%s\", \"finalDate\": \"%s\" }",
                customerDTO.getId(), carDTO.getId(), startDate.format(formatter), endDate.format(formatter));

        String json = mockMvc.perform(MockMvcRequestBuilders.post("/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson)).andReturn().getResponse().getContentAsString();

        BookingDTO bookingDTO = new ObjectMapper().readValue(json, BookingDTO.class);

        // Verify the amount expended is updated
        mockMvc.perform(MockMvcRequestBuilders.get("/customer/{id}", customerDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountExpended", is(232.5)));

        // Create pick up car
        json = mockMvc.perform(MockMvcRequestBuilders.post("/booking/{id}/pickup", bookingDTO.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson)).andReturn().getResponse().getContentAsString();

        bookingDTO = new ObjectMapper().readValue(json, BookingDTO.class);

        assertThat(bookingDTO.getDeposit()).isEqualTo(500);
        assertThat(bookingDTO.getStatus() == BookingStatus.IN_PROGRESS);

        // Verify the amount expended is updated
        mockMvc.perform(MockMvcRequestBuilders.get("/customer/{id}", customerDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountExpended", is(732.5)));
    }

    @Test
    public void testCarReturn() throws Exception {

        LocalDateTime startDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY).plusDays(7).atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(6);
        String bookingJson = String.format("{ \"customerId\": \"%s\", \"carId\": \"%s\", \"firstDate\": \"%s\", \"finalDate\": \"%s\" }",
                customerDTO.getId(), carDTO.getId(), startDate.format(formatter), endDate.format(formatter));

        String json = mockMvc.perform(MockMvcRequestBuilders.post("/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson)).andReturn().getResponse().getContentAsString();

        BookingDTO bookingDTO = new ObjectMapper().readValue(json, BookingDTO.class);

        // Verify the amount expended is updated
        mockMvc.perform(MockMvcRequestBuilders.get("/customer/{id}", customerDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountExpended", is(232.5)));

        // Create pick up car
        json = mockMvc.perform(MockMvcRequestBuilders.post("/booking/{id}/pickup", bookingDTO.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson)).andReturn().getResponse().getContentAsString();

        bookingDTO = new ObjectMapper().readValue(json, BookingDTO.class);

        assertThat(bookingDTO.getDeposit()).isEqualTo(500);
        assertThat(bookingDTO.getStatus() == BookingStatus.IN_PROGRESS);

        // Verify the amount expended is updated
        mockMvc.perform(MockMvcRequestBuilders.get("/customer/{id}", customerDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountExpended", is(732.5)));


        // Return car
        json = mockMvc.perform(MockMvcRequestBuilders.post("/booking/{id}/return", bookingDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"condition\": 20}"))
                .andReturn().getResponse().getContentAsString();

        bookingDTO = new ObjectMapper().readValue(json, BookingDTO.class);

        //assertThat(bookingDTO.getStatus().equalsIgnoreCase("COMPLETED"));
        assertThat(bookingDTO.getStatus() == BookingStatus.COMPLETED);

        // Verify the amount expended is updated
        mockMvc.perform(MockMvcRequestBuilders.get("/customer/{id}", customerDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountExpended", is(232.5 + 500 * 0.8)));
    }


    @Test
    public void testCarReturnLowerDamage() throws Exception {
        // Create booking
        LocalDateTime startDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY).plusDays(7).atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(6);
        String bookingJson = String.format("{ \"customerId\": \"%s\", \"carId\": \"%s\", \"firstDate\": \"%s\", \"finalDate\": \"%s\" }",
                customerDTO.getId(), carDTO.getId(), startDate.format(formatter), endDate.format(formatter));

        String json = mockMvc.perform(MockMvcRequestBuilders.post("/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson)).andReturn().getResponse().getContentAsString();

        BookingDTO bookingDTO = new ObjectMapper().readValue(json, BookingDTO.class);

        // Verify the amount expended is updated
        mockMvc.perform(MockMvcRequestBuilders.get("/customer/{id}", customerDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountExpended", is(232.5)));

        // Create pick up car
        json = mockMvc.perform(MockMvcRequestBuilders.post("/booking/{id}/pickup", bookingDTO.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson)).andReturn().getResponse().getContentAsString();

        bookingDTO = new ObjectMapper().readValue(json, BookingDTO.class);

        assertThat(bookingDTO.getDeposit()).isEqualTo(500);
        assertThat(bookingDTO.getStatus() == BookingStatus.IN_PROGRESS);

        // Verify the amount expended is updated
        mockMvc.perform(MockMvcRequestBuilders.get("/customer/{id}", customerDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountExpended", is(732.5)));


        // Return car
        json = mockMvc.perform(MockMvcRequestBuilders.post("/booking/{id}/return", bookingDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"condition\": 70}"))
                .andReturn().getResponse().getContentAsString();

        bookingDTO = new ObjectMapper().readValue(json, BookingDTO.class);

        assertThat(bookingDTO.getStatus() == BookingStatus.COMPLETED);

        // Verify the amount expended is updated
        mockMvc.perform(MockMvcRequestBuilders.get("/customer/{id}", customerDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountExpended", is(232.5 + 500 * 0.3)));
    }
}
