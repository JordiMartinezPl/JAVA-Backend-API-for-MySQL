package persistence;

import domain.Booking;
import utilities.*;

import java.util.ArrayList;
import java.util.List;


public class BookingRepository {

    static List<Booking> bookingList = new ArrayList<>();

    public List<Booking> getBookingList() {
        return bookingList;
    }


    public void addBooking(Booking booking) {
        bookingList.add(booking);
    }

    public void deleteAllBookings() {
        bookingList = new ArrayList<>();
    }

    public void deleteBookingById(String id) throws Exception {
        boolean found = false;
        for (Booking b : new ArrayList<>(bookingList)) {
            if (b.getId().equals(id))
                bookingList.remove(b);
            found = true;
        }
        if (!found) throw new NotFoundException();
    }

    public static List<Booking> getReservation(String carId) {
        List<Booking> reservation = new ArrayList<>();
        for (Booking b : bookingList) {
            if (b.getCarId().equals(carId)) reservation.add(b);

        }
        return reservation;
    }

    public Booking getBookingById(String bookingID) throws Exception {
        for (Booking b : bookingList) {
            if (b.getId().equals(bookingID))
                return b;
        }
        throw new NotFoundException();
    }
}

