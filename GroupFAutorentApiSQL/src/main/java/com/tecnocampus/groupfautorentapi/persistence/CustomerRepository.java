package com.tecnocampus.groupfautorentapi.persistence;

import com.tecnocampus.groupfautorentapi.domain.Customer;
import com.tecnocampus.groupfautorentapi.utilities.NotFoundException;

import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {
    static List<Customer> customerList = new ArrayList<>();

    public List<Customer> getAllCustomers() {
        return customerList;
    }

    public void addCustomer(Customer customer) {
        customerList.add(customer);
    }

    public void deleteCustomerById(String id) throws NotFoundException {
        boolean found = false;
        for (Customer c : new ArrayList<>(customerList)) {
            if (c.getCostumerID().equals(id)) {
                customerList.remove(c);
                found = true;
                break;
            }
        }
        if (!found) throw new NotFoundException();

    }

    public Customer getCustomerById(String customerID) throws Exception {
        for (Customer c : customerList) {
            if (c.getCostumerID().equals(customerID))
                return c;
        }
        throw new NotFoundException();
    }

    public void deleteAllCustomers() {
        customerList = new ArrayList<>();
    }
}
