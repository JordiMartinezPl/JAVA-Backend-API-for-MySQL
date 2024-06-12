package com.tecnocampus.groupfautorentapi.persistence;

import com.tecnocampus.groupfautorentapi.domain.Car;
import com.tecnocampus.groupfautorentapi.utilities.NotFoundException;

import java.util.ArrayList;
import java.util.List;

public class CarRepository {

    static List<Car> carList = new ArrayList<>();

    public List<Car> getCarList() {
        return carList;
    }

    public void addCar(Car car) {
        carList.add(car);
    }

    public void deleteAllCars() {
        carList = new ArrayList<>();
    }

    public Car getCarById(String carID) throws NotFoundException {
        for (Car c : carList) {
            if (c.getId().equals(carID))
                return c;
        }
        throw new NotFoundException();
    }

    public void deleteCarById(String id) throws NotFoundException {
        boolean found = false;
        for (Car c : new ArrayList<>(carList)) {
            if (c.getId().equals(id))
                carList.remove(c);
            found = true;
        }
        if (!found) throw new NotFoundException();
    }
}
