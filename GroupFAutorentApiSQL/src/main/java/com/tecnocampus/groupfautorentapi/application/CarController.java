package com.tecnocampus.groupfautorentapi.application;

import com.tecnocampus.groupfautorentapi.application.dto.CarDTO;
import com.tecnocampus.groupfautorentapi.domain.Car;
import com.tecnocampus.groupfautorentapi.persistence.jdbc.CarRepositoryDB;
import com.tecnocampus.groupfautorentapi.utilities.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarController {
    private final CarRepositoryDB carRepositoryDB;

    public CarController(CarRepositoryDB carRepositoryDB) {
        this.carRepositoryDB = carRepositoryDB;
    }

    public CarDTO createNewCar(CarDTO carToCreate) throws Exception {
        Car car = new Car(carToCreate);
        carRepositoryDB.addCar(car);
        return new CarDTO(car);
    }

    public List<CarDTO> getAllCar() throws Exception {
        List<Car> cars = carRepositoryDB.getCarList();
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
        carRepositoryDB.deleteAllCars();
    }

    public CarDTO getCar(String id) throws Exception {
        Car car = carRepositoryDB.getCarById(id);
        return new CarDTO(car);
    }

    public void deleteCarById(String id) throws NotFoundException {
        carRepositoryDB.deleteCarById(id);
    }


}