package com.tecnocampus.groupfautorentapi.application;

import com.tecnocampus.groupfautorentapi.application.dto.CustomerDTO;
import com.tecnocampus.groupfautorentapi.domain.Customer;
import com.tecnocampus.groupfautorentapi.persistence.CustomerRepository;

import java.util.ArrayList;
import java.util.List;

public class CustomerController {


    public CustomerDTO createNewCustomer(String fullName, String address, String dateBirth) throws Exception {
        Customer customer = new Customer(fullName, address, dateBirth);
        new CustomerRepository().addCustomer(customer);
        return new CustomerDTO(customer);
    }

    public CustomerDTO createNewCustomer(CustomerDTO customerDTO) throws Exception {
        Customer customer = new Customer(customerDTO);
        new CustomerRepository().addCustomer(customer);
        return new CustomerDTO(customer);
    }

    public List<CustomerDTO> getAllCustomers() throws Exception {
        List<Customer> customers = new CustomerRepository().getAllCustomers();
        return convertCustomerToDTO(customers);
    }

    private List<CustomerDTO> convertCustomerToDTO(List<Customer> list) throws Exception {
        List<CustomerDTO> customerDTOList = new ArrayList<>();
        for (Customer c : list) {
            customerDTOList.add(new CustomerDTO(c));
        }
        return customerDTOList;
    }

    public void deleteAllCustomers() {
        new CustomerRepository().deleteAllCustomers();
    }

    public CustomerDTO updateCustomer(String id, CustomerDTO customerDTO) throws Exception {
        Customer customer = new CustomerRepository().getCustomerById(id);
        customer.updateCustomer(customerDTO);
        return new CustomerDTO(customer);
    }

    public void deleteCustomerById(String id) throws Exception {
        new CustomerRepository().deleteCustomerById(id);
    }

    public CustomerDTO getCustomerById(String id) throws Exception {
        Customer customer = new CustomerRepository().getCustomerById(id);
        return new CustomerDTO(customer);
    }

}