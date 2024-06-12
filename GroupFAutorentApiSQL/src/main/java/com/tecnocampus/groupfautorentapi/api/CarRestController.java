package com.tecnocampus.groupfautorentapi.api;

import com.tecnocampus.groupfautorentapi.application.CarController;
import com.tecnocampus.groupfautorentapi.application.dto.CarDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CarRestController {

    private final CarController carController;

    public CarRestController(CarController carController) {
        this.carController = carController;
    }

    @PostMapping("/car")
    public ResponseEntity<CarDTO> createCar(@RequestBody CarDTO carToCreate) throws Exception {
        CarDTO createdCar = carController.createNewCar(carToCreate);
        return new ResponseEntity<>(createdCar, HttpStatus.CREATED);

    }

    @GetMapping("/car")
    public List<CarDTO> getAllCar() throws Exception {
        return carController.getAllCar();
    }

    @DeleteMapping("/car")
    public void deleteAllCar() {
        carController.deleteAllCars();
    }


    @GetMapping("/car/{id}")
    public CarDTO getCar(@PathVariable String id) throws Exception {
        return carController.getCar(id);
    }

    @DeleteMapping("/car/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCarById(@PathVariable String id) throws Exception {
        carController.deleteCarById(id);
    }
}
