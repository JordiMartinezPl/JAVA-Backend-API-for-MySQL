package application.dto;

import domain.*;
import utilities.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class CustomerDTO {

    private String fullName;
    private String address;
    private LocalDate dateBirth;
    private String id;
    private Double amountExpended;

    public CustomerDTO() {
    }

    public CustomerDTO(String fullName, String address, String dateBirth) throws Exception {
        if (fullName == null || address == null || dateBirth == null) throw new InvalidParamsException();
        checkAge(dateBirth);
        this.fullName = fullName;
        this.address = address;
        this.id = UUID.randomUUID().toString();
    }

    private void checkAge(String date) throws InvalidParamsException {
        LocalDate actualDate = LocalDate.now();
        LocalDate dateBirth = convertStringToLocalDate(date);
        Period age = Period.between(dateBirth, actualDate);
        if (age.getYears() < 18) {
            throw new InvalidParamsException();
        }
        this.dateBirth = dateBirth;
    }

    private LocalDate convertStringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateLocalDate = LocalDate.parse(date, formatter);
        return dateLocalDate;
    }

    public CustomerDTO(Customer customer) {
        fullName = customer.getName();
        address = customer.getAddress();
        dateBirth = customer.getDateBirth();
        id = customer.getCostumerID();
        amountExpended = customer.getAmountExpended();
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getDateBirth() {
        return dateBirth;
    }

    public String getId() {
        return id;
    }

    public Double getAmountExpended() {
        return amountExpended;
    }
}
