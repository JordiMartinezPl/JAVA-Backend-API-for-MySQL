package com.tecnocampus.groupfautorentapi.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tecnocampus.groupfautorentapi.domain.Booking;
import com.tecnocampus.groupfautorentapi.domain.BookingStatus;
import com.tecnocampus.groupfautorentapi.utilities.InvalidParamsException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class BookingDTO {


    private LocalDateTime firstDate;
    private LocalDateTime finalDate;
    private LocalDateTime purchaseDate;
    private String customerId;
    private String carId;
    private BookingStatus status;
    private String id;
    private double rental;
    private double deposit;


    @JsonCreator
    public BookingDTO(@JsonProperty("customerId") String customerId,
                      @JsonProperty("carId") String carId,
                      @JsonProperty("firstDate") String firstDate,
                      @JsonProperty("finalDate") String finalDate,
                      @JsonProperty("purchaseDate") String purchaseDate,
                      @JsonProperty("status") String status,
                      @JsonProperty("id") String id,
                      @JsonProperty("rental") Double rental,
                      @JsonProperty("deposit") Double deposit) throws Exception {

        if (customerId == null || carId == null || firstDate == null || finalDate == null) throw new InvalidParamsException();
        this.customerId = customerId;
        this.carId = carId;
        checkInitialization(status, id, rental, deposit,purchaseDate);
        checkDate(convertStringToLocalDateTime(firstDate), convertStringToLocalDateTime(finalDate));

    }

    private BookingStatus convertToBookingStatus(String status) throws Exception{
        if(status.equalsIgnoreCase("CONFIRMED"))return BookingStatus.CONFIRMED;
        else if (status.equalsIgnoreCase("INPROGRESS"))return BookingStatus.IN_PROGRESS;
        else if (status.equalsIgnoreCase("COMPLETED"))return BookingStatus.COMPLETED;
        else if (status.equalsIgnoreCase("CANCELED"))return BookingStatus.CANCELED;
        else throw  new InvalidParamsException();
    }
    private LocalDateTime convertStringToLocalDateTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    public BookingDTO (Booking booking)throws Exception{
        if(booking==null)throw new InvalidParamsException();
        copyBookingIds(booking);
        copyBookingDates(booking);
        this.status= booking.getStatus();
        this.deposit = booking.getDeposit();
        this.rental = booking.getRental();
    }
    private void checkDate(LocalDateTime firstDate, LocalDateTime finalDate)throws InvalidParamsException{
        long daysDifference =ChronoUnit.DAYS.between(firstDate,finalDate);
        if(daysDifference < 1 || daysDifference > 20|| firstDate.isBefore(purchaseDate)||finalDate.isBefore(firstDate)) throw new InvalidParamsException();
        this.firstDate = (firstDate);
        this.finalDate = (finalDate);
    }

    private void checkInitialization(String status, String id, Double rental, Double deposit,String purchaseDate){
        this.status = status != null ? BookingStatus.valueOf(status) : BookingStatus.CONFIRMED;
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.rental = rental != null ? rental : 0.0;
        this.deposit = deposit != null ? deposit : 0.0;
        this.purchaseDate = purchaseDate != null ? convertStringToLocalDateTime(purchaseDate) : LocalDate.now().atStartOfDay();
    }

    private void copyBookingDates(Booking booking){
        this.firstDate = booking.getFirstDate();
        this.finalDate = booking.getFinalDate();
        this.purchaseDate = booking.getPurchaseDate();
    }

    private void copyBookingIds(Booking booking){
        this.id = booking.getId();
        this.customerId = booking.getCustomerId();
        this.carId = booking.getCarId();

    }

    public LocalDateTime getFirstDate() {return firstDate;}
    public LocalDateTime getFinalDate() {return finalDate;}
    public LocalDateTime getPurchaseDate() {return purchaseDate;}
    public String getCustomerId() {return customerId;}
    public String getCarId() {return carId;}
    public BookingStatus getStatus() {return status;}
    public String getId() {return id;}
    public double getRental() {return rental;}
    public double getDeposit() {return deposit;}

}
