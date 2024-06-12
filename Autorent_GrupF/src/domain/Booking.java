package domain;

import utilities.*;
import application.dto.BookingDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Booking {

    private LocalDateTime firstDate;
    private LocalDateTime finalDate;
    private LocalDateTime purchaseDate;
    private String customerId;
    private String carId;
    private BoookingStatus status;
    private String id;
    private double rental;
    private double deposit;
    //rental es paga quan faig el booking
    //diposit quan faig vaig a buscar el cotxe
    // crear un procediment que serveixi per dir que el client ha anat a buscar el cotxe i donar el diposit
    // i el dia que de tornar el cotxe se li torna a l'usuari el diposit descontan el mal


    public Booking(BookingDTO bookingDTO) throws Exception {
        if (bookingDTO.getCustomerId() == null || bookingDTO.getCarId() == null || bookingDTO.getPurchaseDate() == null || bookingDTO.getFirstDate() == null || bookingDTO.getFinalDate() == null || bookingDTO.getStatus() == null || bookingDTO.getId() == null)
            throw new InvalidParamsException();
        this.purchaseDate = bookingDTO.getPurchaseDate();
        this.firstDate = bookingDTO.getFirstDate();
        this.finalDate = bookingDTO.getFinalDate();
        this.carId = bookingDTO.getCarId();
        this.customerId = bookingDTO.getCustomerId();
        id = bookingDTO.getId();
        this.status = BoookingStatus.CONFIRMED;

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
        status = BoookingStatus.IN_PROGRESS;
    }

    public void updateToCompleted() {
        status = BoookingStatus.COMPLETED;
    }

    public void updateToCanceled() {
        status = BoookingStatus.CANCELED;
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

    public BoookingStatus getStatus() {
        return status;
    }
}