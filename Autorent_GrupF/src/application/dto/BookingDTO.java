package application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import domain.*;
import utilities.*;

public class BookingDTO {


    private LocalDateTime firstDate;
    private LocalDateTime finalDate;
    private LocalDateTime purchaseDate;
    private String customerId;
    private String carId;
    private BoookingStatus status;
    private String id;
    private double rental;
    private double deposit;


    public BookingDTO(String customerId, String carId, String firstDate, String finalDate, String purchaseDate, String status, String id, Double rental, Double deposit) throws Exception {

        if (customerId == null || carId == null || firstDate == null || finalDate == null)
            throw new InvalidParamsException();
        this.customerId = customerId;
        this.carId = carId;
        this.purchaseDate = purchaseDate != null ? convertStringToLocalDateTime(purchaseDate) : LocalDate.now().atStartOfDay();
        checkDate(convertStringToLocalDateTime(firstDate), convertStringToLocalDateTime(finalDate));
        this.firstDate = convertStringToLocalDateTime(firstDate);
        this.finalDate = convertStringToLocalDateTime(finalDate);
        this.status = status != null ? BoookingStatus.valueOf(status) : BoookingStatus.CONFIRMED;
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.rental = rental != null ? rental : 0.0;
        this.deposit = deposit != null ? deposit : 0.0;
    }

    private BoookingStatus convertToBookingStatus(String status) throws Exception {
        if (status.equalsIgnoreCase("CONFIRMED")) return BoookingStatus.CONFIRMED;
        else if (status.equalsIgnoreCase("INPROGRESS")) return BoookingStatus.IN_PROGRESS;
        else if (status.equalsIgnoreCase("COMPLETED")) return BoookingStatus.COMPLETED;
        else if (status.equalsIgnoreCase("CANCELED")) return BoookingStatus.CANCELED;
        else throw new InvalidParamsException();
    }

    private LocalDateTime convertStringToLocalDateTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public BookingDTO(Booking booking) throws Exception {
        if (booking == null) throw new InvalidParamsException();
        this.firstDate = booking.getFirstDate();
        this.finalDate = booking.getFinalDate();
        this.purchaseDate = booking.getPurchaseDate();
        this.id = booking.getId();
        this.customerId = booking.getCustomerId();
        this.carId = booking.getCarId();
        this.status = booking.getStatus();
        this.deposit = booking.getDeposit();
        this.rental = booking.getRental();
    }

    private void checkDate(LocalDateTime firstDate, LocalDateTime finalDate) throws InvalidParamsException {
        long daysDifference = ChronoUnit.DAYS.between(firstDate, finalDate);
        if (daysDifference < 1 || daysDifference > 20 || firstDate.isBefore(purchaseDate) || finalDate.isBefore(firstDate))
            throw new InvalidParamsException();
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

    public String getCustomerId() {
        return customerId;
    }

    public String getCarId() {
        return carId;
    }

    public BoookingStatus getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public double getRental() {
        return rental;
    }

    public double getDeposit() {
        return deposit;
    }

}
