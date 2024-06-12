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
    //rental es paga quan faig el booking
    //diposit quan faig vaig a buscar el cotxe
    // crear un procediment que serveixi per dir que el client ha anat a buscar el cotxe i donar el diposit
    // i el dia que de tornar el cotxe se li torna a l'usuari el diposit descontan el mal


    public Booking() {
    }

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

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public void setFirstDate(LocalDateTime firstDate) {
        this.firstDate = firstDate;
    }

    public void setFinalDate(LocalDateTime finalDate) {
        this.finalDate = finalDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setRental(double rental) {
        this.rental = rental;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}