package com.tecnocampus.groupfautorentapi.application;

import com.tecnocampus.groupfautorentapi.application.dto.BookingDTO;
import com.tecnocampus.groupfautorentapi.domain.Booking;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.tecnocampus.groupfautorentapi.domain.Car;
import com.tecnocampus.groupfautorentapi.domain.Customer;
import com.tecnocampus.groupfautorentapi.utilities.InvalidParamsException;
import com.tecnocampus.groupfautorentapi.persistence.jdbc.BookingRepositoryDB;
import com.tecnocampus.groupfautorentapi.persistence.jdbc.CarRepositoryDB;
import com.tecnocampus.groupfautorentapi.persistence.jdbc.CustomerRepositoryDB;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookingController {

    private final BookingRepositoryDB bookingRepositoryDB;
    private final CustomerRepositoryDB customerRepositoryDB;
    private final CarRepositoryDB carRepositoryDB;

    public BookingController(BookingRepositoryDB bookingRepositoryDB, CustomerRepositoryDB customerRepositoryDB, CarRepositoryDB carRepositoryDB) {
        this.bookingRepositoryDB = bookingRepositoryDB;
        this.customerRepositoryDB = customerRepositoryDB;
        this.carRepositoryDB = carRepositoryDB;
    }

    public BookingDTO createBooking(String customerId, String carId, BookingDTO bookingToCreate) throws Exception {
        Customer customer = customerRepositoryDB.getCustomerById(customerId);
        checkCarAvailability(bookingToCreate.getFirstDate(), bookingToCreate.getFinalDate(), (ArrayList<Booking>) bookingRepositoryDB.getReservation(carId));
        Booking booking = new Booking(bookingToCreate);
        booking.calculateRentalAndDeposit(carRepositoryDB.getCarById(carId));
        customer.increaseAmountExpended(booking.getRental());
        customerRepositoryDB.updateCustomerExpendedAmount(customerId, customer.getAmountExpended());
        bookingRepositoryDB.addBooking(booking);
        return new BookingDTO(booking);
    }

    public List<BookingDTO> getAllBookings() throws Exception {
        List<Booking> bookings = bookingRepositoryDB.getBookingList();
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
        bookingRepositoryDB.deleteAllBookings();
    }

    public BookingDTO getBookingById(String id) throws Exception {
        Booking booking = bookingRepositoryDB.getBookingById(id);
        return new BookingDTO(booking);
    }

    public void deleteBookingById(String id) throws Exception {
        updateCanceled(id);
        bookingRepositoryDB.deleteBookingById(id);
    }

    private void checkCarAvailability(LocalDateTime firstDate, LocalDateTime lastDate, ArrayList<Booking> reservations) throws InvalidParamsException {
        for (Booking reservation : reservations) {
            if (!(lastDate.isBefore(reservation.getFirstDate()) || firstDate.isAfter(reservation.getFinalDate()))) {
                throw new InvalidParamsException();
            }
        }
    }

    public BookingDTO updateInProgress(String id) throws Exception {
        Booking booking = bookingRepositoryDB.getBookingById(id);
        booking.updateToInProgress();
        bookingRepositoryDB.updateBookingStatus(booking.getId(), booking.getStatus().toString());
        Customer customer = customerRepositoryDB.getCustomerById(booking.getCustomerId());
        customer.increaseAmountExpended(booking.getDeposit());
        customerRepositoryDB.updateCustomerExpendedAmount(customer.getCostumerID(), customer.getAmountExpended());
        return new BookingDTO(booking);
    }

    public BookingDTO updateCompleted(String id, double damage) throws Exception {
        Booking booking = bookingRepositoryDB.getBookingById(id);
        Car car = carRepositoryDB.getCarById(booking.getCarId());
        Customer customer = customerRepositoryDB.getCustomerById(booking.getCustomerId());
        booking.updateToCompleted();
        customer.decreaseAmountExpended(car.getCategory().calculateDeposit(damage));
        bookingRepositoryDB.updateBookingStatus(booking.getId(), booking.getStatus().toString());
        customerRepositoryDB.updateCustomerExpendedAmount(customer.getCostumerID(), customer.getAmountExpended());

        return new BookingDTO(booking);
    }

    public double updateCanceled(String id) throws Exception {
        Booking booking = bookingRepositoryDB.getBookingById(id);
        Customer customer = customerRepositoryDB.getCustomerById(booking.getCustomerId());
        booking.updateToCanceled();
        bookingRepositoryDB.updateBookingStatus(booking.getId(), booking.getStatus().toString());
        return checkCancelDates(booking, customer);

    }

    public double checkCancelDates(Booking booking, Customer customer) throws Exception {
        long hourDifference = ChronoUnit.HOURS.between(booking.getPurchaseDate(), booking.getFirstDate());
        if (hourDifference <= 24) {
            customer.increaseAmountExpended(booking.getRental());
            customerRepositoryDB.updateCustomerExpendedAmount(customer.getCostumerID(), customer.getAmountExpended());
            return booking.getRental();
        }
        customer.decreaseAmountExpended(booking.getRental());
        customerRepositoryDB.updateCustomerExpendedAmount(customer.getCostumerID(), customer.getAmountExpended());
        return 0;
    }

}


