package com.tecnocampus.groupfautorentapi.api;
import com.tecnocampus.groupfautorentapi.application.BookingController;
import com.tecnocampus.groupfautorentapi.application.dto.BookingDTO;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookingRestController {


    @PostMapping("/booking")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingToCreate) throws Exception {
        BookingDTO bookingDTO = new BookingController().createBooking(bookingToCreate.getCustomerId(), bookingToCreate.getCarId(), bookingToCreate);
        return new ResponseEntity<>(bookingDTO, HttpStatus.CREATED);
    }

    @GetMapping("/booking")
    public List<BookingDTO> getAllBookings() throws Exception {
        return new BookingController().getAllBookings();
    }

    @DeleteMapping("/booking")
    public void deleteAllBooking() {
        new BookingController().deleteAllBookings();
    }

    @GetMapping("/booking/{id}")
    public BookingDTO getBookingById(@PathVariable String id) throws Exception {
        return new BookingController().getBookingById(id);
    }

    @DeleteMapping("/booking/{id}")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void deleteBookingById(@PathVariable String id) throws Exception {
        new BookingController().deleteBookingById(id);
    }

    @PostMapping("/booking/{id}/return")
    public ResponseEntity<BookingDTO> returnCar(@PathVariable String id, @RequestBody String jsonString) throws Exception {
        JSONObject json = new JSONObject(jsonString);
        Double damage = json.getDouble("condition");
        BookingDTO bookingDTO = new BookingController().updateCompleted(id, damage);
        return new ResponseEntity<>(bookingDTO, HttpStatus.ACCEPTED);

    }

    @PostMapping("/booking/{id}/pickup")
    public ResponseEntity<BookingDTO> pickUpCar(@PathVariable String id) throws Exception {
        BookingDTO bookingDTO = new BookingController().updateInProgress(id);
        return new ResponseEntity<>(bookingDTO, HttpStatus.ACCEPTED);
    }


}
