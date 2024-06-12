package com.tecnocampus.groupfautorentapi.api;

import com.tecnocampus.groupfautorentapi.application.CustomerController;
import com.tecnocampus.groupfautorentapi.application.dto.CustomerDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class CustomerRestController {
    private final CustomerController customerController;

    public CustomerRestController(CustomerController customerController) {
        this.customerController = customerController;
    }

    @PostMapping("/customer")
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerToCreate) throws Exception {
        CustomerDTO createdCustomer = customerController.createNewCustomer(customerToCreate);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @GetMapping("/customer")
    public List<CustomerDTO> customerDTOList() throws Exception {
        return customerController.getAllCustomers();

    }

    @DeleteMapping("/customer")
    public void deleteAllCustomer() {
        customerController.deleteAllCustomers();
    }

    @PostMapping("/customer/{id}")
    public CustomerDTO updateCustomer(@PathVariable String id, @RequestBody CustomerDTO customerToUpdate) throws Exception {
        return customerController.updateCustomer(id, customerToUpdate);
    }

    @DeleteMapping("/customer/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomerById(@PathVariable String id) throws Exception {
        customerController.deleteCustomerById(id);
    }

    @GetMapping("/customer/{id}")
    public CustomerDTO getCustomerById(@PathVariable String id) throws Exception {
        return customerController.getCustomerById(id);
    }


}
