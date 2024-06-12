package com.tecnocampus.groupfautorentapi.application;

import com.tecnocampus.groupfautorentapi.application.dto.CarDTO;
import com.tecnocampus.groupfautorentapi.domain.Car;
import com.tecnocampus.groupfautorentapi.persistence.CarRepository;
import com.tecnocampus.groupfautorentapi.utilities.NotFoundException;
import java.util.ArrayList;
import java.util.List;


public class CarController {

    public CarDTO createNewCar(CarDTO carToCreate) throws Exception {
        Car car = new Car(carToCreate);
        new CarRepository().addCar(car);
        return new CarDTO(car);
    }

    public List<CarDTO> getAllCar() throws Exception {
        List<Car> cars = new CarRepository().getCarList();
        return convertCarToDTO(cars);
    }

    private List<CarDTO> convertCarToDTO(List<Car> cars) throws Exception {
        List<CarDTO> carsDTO = new ArrayList<>();
        for (Car car : cars) {
            carsDTO.add(new CarDTO(car));
        }
        return carsDTO;
    }

    public void deleteAllCars() {
        new CarRepository().deleteAllCars();
    }

    public CarDTO getCar(String id) throws Exception {
        Car car = new CarRepository().getCarById(id);
        return new CarDTO(car);
    }

    public void deleteCarById(String id) throws NotFoundException {
        new CarRepository().deleteCarById(id);
    }


}