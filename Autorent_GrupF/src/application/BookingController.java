package application;

import utilities.*;
import domain.*;
import application.dto.*;
import persistence.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BookingController {


    public BookingDTO createBooking(String customerId, String carId, BookingDTO bookingToCreate) throws Exception {
        Customer customer = new CustomerRepository().getCustomerById(customerId);
        Car car = new CarRepository().getCarById(carId);
        checkCarDisponibility(bookingToCreate.getFirstDate(), bookingToCreate.getFinalDate(), (ArrayList<Booking>) BookingRepository.getReservation(carId));
        Booking booking = new Booking(bookingToCreate);
        booking.calculateRentalAndDeposit(car);
        customer.increaseAmountExpended(booking.getRental());
        new BookingRepository().addBooking(booking);
        return new BookingDTO(booking);

    }

    public List<BookingDTO> getAllBookings() throws Exception {
        List<Booking> bookings = new BookingRepository().getBookingList();
        return convertBookingsToDTO(bookings);
    }

    public List<BookingDTO> convertBookingsToDTO(List<Booking> bookings) throws Exception {
        List<BookingDTO> bookingDTO = new ArrayList<>();
        for (Booking b : bookings) {
            bookingDTO.add(new BookingDTO(b));
        }
        return bookingDTO;
    }

    public void deleteAllBookings() {
        new BookingRepository().deleteAllBookings();
    }

    public BookingDTO getBookingById(String id) throws Exception {
        Booking booking = new BookingRepository().getBookingById(id);
        return new BookingDTO(booking);
    }

    public void deleteBookingById(String id) throws Exception {
        updateCanceled(id);
        new BookingRepository().deleteBookingById(id);
    }

    private void checkCarDisponibility(LocalDateTime firstDate, LocalDateTime lastDate, ArrayList<Booking> reservations) throws InvalidParamsException {
        for (Booking reservation : reservations) {
            if (!(lastDate.isBefore(reservation.getFirstDate()) || firstDate.isAfter(reservation.getFinalDate()))) {
                throw new InvalidParamsException();
            }
        }
    }

    public BookingDTO updateInProgress(String id) throws Exception {
        Booking booking = new BookingRepository().getBookingById(id);
        booking.updateToInProgress();
        Customer customer = new CustomerRepository().getCustomerById(booking.getCustomerId());
        customer.increaseAmountExpended(booking.getDeposit());
        return new BookingDTO(booking);
    }

    public BookingDTO updateCompleted(String id, double damage) throws Exception {
        Booking booking = new BookingRepository().getBookingById(id);
        Car car = new CarRepository().getCarById(booking.getCarId());
        Customer customer = new CustomerRepository().getCustomerById(booking.getCustomerId());
        booking.updateToCompleted();
        customer.decreaseAmountExpended(car.getCategory().calculateDeposit(damage));
        return new BookingDTO(booking);
    }

    private double updateCanceled(String id) throws Exception {
        Booking booking = new BookingRepository().getBookingById(id);
        Customer customer = new CustomerRepository().getCustomerById(booking.getCustomerId());
        booking.updateToCanceled();
        return checkCancelDates(booking, customer);
    }

    private double checkCancelDates(Booking booking, Customer customer) {
        long hourDifference = ChronoUnit.HOURS.between(booking.getPurchaseDate(), booking.getFirstDate());
        if (hourDifference <= 24) {
            customer.increaseAmountExpended(booking.getRental());
            return booking.getRental();
        }
        customer.decreaseAmountExpended(booking.getRental());
        return 0;
    }

}


