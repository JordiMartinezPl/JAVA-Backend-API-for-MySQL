package com.tecnocampus.groupfautorentapi.persistence.jdbc;

import com.tecnocampus.groupfautorentapi.domain.Car;
import com.tecnocampus.groupfautorentapi.utilities.NotFoundException;

import java.util.List;

public interface ICarRepository {

    public List<Car> getCarList();

    public void addCar(Car car);

    public void deleteAllCars();

    public Car getCarById(String carID) throws NotFoundException;

    public void deleteCarById(String id) throws NotFoundException;

}
