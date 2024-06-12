package com.tecnocampus.groupfautorentapi.persistence.jdbc;

import com.tecnocampus.groupfautorentapi.domain.Booking;
import com.tecnocampus.groupfautorentapi.utilities.NotFoundException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookingRepositoryDB implements IBookingRepository {

    private final JdbcClient jdbcClient;

    public BookingRepositoryDB(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Booking> getBookingList() {
        return jdbcClient.sql("SELECT * FROM bookings")
                .query(Booking.class)
                .list();
    }

    public void addBooking(Booking booking) {
        int result = jdbcClient.sql("INSERT INTO bookings(id,firstDate, finalDate, purchaseDate, carID, customerID,rental,deposit,status)values (?,?,?,?,?,?,?,?,?)")
                .params(List.of(booking.getId(), booking.getFirstDate(), booking.getFinalDate(), booking.getPurchaseDate(), booking.getCarId(), booking.getCustomerId(), booking.getRental(), booking.getDeposit(), booking.getStatus().toString()))
                .update();

    }

    public void deleteAllBookings() {
        jdbcClient.sql("DELETE FROM bookings").update();

    }

    public void deleteBookingById(String bookingID) throws Exception {
        if (jdbcClient.sql("DELETE FROM bookings WHERE id =?").param(bookingID).update() == 0) {
            throw new NotFoundException();
        }
    }

    public List<Booking> getReservation(String carId) {
        return jdbcClient.sql("SELECT * FROM bookings WHERE carID =?")
                .param(carId)
                .query(Booking.class).list();
    }

    public Booking getBookingById(String bookingID) throws Exception {
        try {
            return jdbcClient.sql("SELECT * FROM bookings where id =?")
                    .params(bookingID)
                    .query(Booking.class).list().get(0);

        } catch (Exception e) {
            throw new NotFoundException();
        }
    }

    public void updateBookingStatus(String bookingID, String status) throws NotFoundException {
        if (jdbcClient.sql("UPDATE bookings SET status = ? WHERE id =?").params(List.of(status, bookingID)).update() == 0)
            throw new NotFoundException();

    }
}
