package com.tecnocampus.groupfautorentapi.persistence.jdbc;

import com.tecnocampus.groupfautorentapi.domain.Booking;

import java.util.List;

public interface IBookingRepository {

    public List<Booking> getBookingList();

    public void addBooking(Booking booking);

    public void deleteAllBookings();

    public void deleteBookingById(String id) throws Exception;

    public List<Booking> getReservation(String carId);

    public Booking getBookingById(String bookingID) throws Exception;
}
