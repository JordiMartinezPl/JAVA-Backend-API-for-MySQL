package com.tecnocampus.groupfautorentapi.persistence.jdbc;

import com.tecnocampus.groupfautorentapi.domain.Customer;
import com.tecnocampus.groupfautorentapi.utilities.NotFoundException;

import java.util.List;

public interface ICustomerRepository {

    public List<Customer> getAllCustomers();

    public void addCustomer(Customer customer);

    public void deleteCustomerById(String id) throws NotFoundException;

    public Customer getCustomerById(String customerID) throws Exception;

    public void deleteAllCustomers();
}
