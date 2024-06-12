package com.tecnocampus.groupfautorentapi.domain;

import com.tecnocampus.groupfautorentapi.application.dto.BookingDTO;
import com.tecnocampus.groupfautorentapi.utilities.InvalidParamsException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;

public class Booking {

    private LocalDateTime firstDate;
    private LocalDateTime finalDate;
    private LocalDateTime purchaseDate;
    private String customerId;
    private String carId;
    private BookingStatus status;
    private String id;
    private double rental;
    private double deposit;

    public Booking(BookingDTO bookingDTO) throws Exception {
        if (bookingDTO.getCustomerId() == null || bookingDTO.getCarId() == null || bookingDTO.getPurchaseDate() == null || bookingDTO.getFirstDate() == null || bookingDTO.getFinalDate() == null || bookingDTO.getStatus() == null || bookingDTO.getId() == null)
            throw new InvalidParamsException();
        this.purchaseDate = bookingDTO.getPurchaseDate();
        this.firstDate = bookingDTO.getFirstDate();
        this.finalDate = bookingDTO.getFinalDate();
        this.carId = bookingDTO.getCarId();
        this.customerId = bookingDTO.getCustomerId();
        id = bookingDTO.getId();
        this.status = BookingStatus.CONFIRMED;

    }

    public void calculateRentalAndDeposit(Car car) {
        rental = car.calculatePriceForDays(getFirstDate(), getFinalDate());
        deposit = car.getCategory().getDeposit();
    }

    private LocalDateTime convertStringToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00");
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
        return localDateTime;
    }

    private void checkDate(LocalDateTime firstDate, LocalDateTime finalDate) throws InvalidParamsException {
        long daysDifference = ChronoUnit.DAYS.between(firstDate, finalDate);
        if (daysDifference < 1 || daysDifference > 20 || firstDate.isBefore(purchaseDate) || finalDate.isBefore(firstDate))
            throw new InvalidParamsException();
    }

    public void updateToInProgress() {
        status = BookingStatus.IN_PROGRESS;
    }

    public void updateToCompleted() {
        status = BookingStatus.COMPLETED;
    }

    public void updateToCanceled() {
        status = BookingStatus.CANCELED;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCarId() {
        return carId;
    }

    public LocalDateTime getFirstDate() {
        return firstDate;
    }

    public LocalDateTime getFinalDate() {
        return finalDate;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public double getRental() {
        return rental;
    }

    public double getDeposit() {
        return deposit;
    }

    public String getId() {
        return id;
    }

    public BookingStatus getStatus() {
        return status;
    }
}