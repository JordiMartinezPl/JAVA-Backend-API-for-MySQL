package com.tecnocampus.groupfautorentapi.persistence.jdbc;

import com.tecnocampus.groupfautorentapi.domain.Customer;
import com.tecnocampus.groupfautorentapi.utilities.NotFoundException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerRepositoryDB implements ICustomerRepository {

    private final JdbcClient jdbcClient;

    public CustomerRepositoryDB(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return jdbcClient.sql("SELECT *  FROM customers").query(Customer.class).list();
    }

    @Override
    public void addCustomer(Customer customer) {
        jdbcClient.sql("INSERT INTO customers (id,fullName,address,dateBirth,amountExpended) values (?,?,?,?,?)")
                .params(List.of(customer.getCostumerID(), customer.getName(), customer.getAddress(), customer.getDateBirth(), customer.getAmountExpended()))
                .update();
    }

    @Override
    public void deleteCustomerById(String id) throws NotFoundException {
        if (jdbcClient.sql("DELETE FROM customers where id =:id ").param("id", id).update() == 0) {
            throw new NotFoundException();
        }
    }

    @Override
    public Customer getCustomerById(String customerID) throws Exception {
        try {
            return jdbcClient.sql("SELECT * FROM customers where id =?")
                    .param(customerID)
                    .query(Customer.class).list().get(0);
        } catch (Exception e) {
            throw new NotFoundException();
        }
    }

    public void updateCustomerExpendedAmount(String id, double expendedAmount) throws NotFoundException {
        if (jdbcClient.sql("UPDATE customers SET amountExpended = ? WHERE id =?").params(List.of(expendedAmount, id)).update() == 0)
            throw new NotFoundException();

    }


    @Override
    public void deleteAllCustomers() {
        jdbcClient.sql("DELETE FROM customers").update();
    }
}
