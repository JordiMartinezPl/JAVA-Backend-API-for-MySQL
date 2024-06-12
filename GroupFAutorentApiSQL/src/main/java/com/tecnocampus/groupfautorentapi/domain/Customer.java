package com.tecnocampus.groupfautorentapi.domain;

import com.tecnocampus.groupfautorentapi.application.dto.CustomerDTO;
import com.tecnocampus.groupfautorentapi.utilities.InvalidParamsException;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Customer {

    private String fullName;
    private String address;
    private LocalDate dateBirth;
    private String id;
    private Double amountExpended = 0.0;

    public Customer() {
    }

    public Customer(CustomerDTO customerDTO) throws Exception {
        if (customerDTO.getFullName() == null || customerDTO.getAddress() == null || customerDTO.getDateBirth() == null)
            throw new InvalidParamsException();
        checkAge(customerDTO.getDateBirth());
        this.fullName = customerDTO.getFullName();
        this.address = customerDTO.getAddress();
        this.id = customerDTO.getId();

    }

    public Customer(String fullName, String address, String dateBirth) throws Exception {
        if (fullName == null || address == null || dateBirth == null) throw new InvalidParamsException();
        checkAge(convertStringToLocalDate(dateBirth));
        this.fullName = fullName;
        this.address = address;
        this.id = UUID.randomUUID().toString();
    }


    private LocalDate convertStringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dateLocalDate = LocalDate.parse(date, formatter);
        return dateLocalDate;
    }

    public void updateCustomer(CustomerDTO customerDTO) {
        this.address = customerDTO.getAddress();
        this.fullName = customerDTO.getFullName();
        this.dateBirth = customerDTO.getDateBirth();
    }

    public String getName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getDateBirth() {
        return dateBirth;
    }

    public String getCostumerID() {
        return id;
    }

    public Double getAmountExpended() {
        return amountExpended;
    }


    private void checkAge(LocalDate dateBirth) throws InvalidParamsException {
        LocalDate actualDate = LocalDate.now();
        Period age = Period.between(dateBirth, actualDate);
        if (age.getYears() < 18) throw new InvalidParamsException();
        this.dateBirth = dateBirth;
    }

    public void increaseAmountExpended(double price) {
        amountExpended += price;
    }

    public void decreaseAmountExpended(double price) {
        amountExpended -= price;
    }

}
