package com.tecnocampus.groupfautorentapi.persistence.jdbc;

import com.tecnocampus.groupfautorentapi.domain.Car;

import com.tecnocampus.groupfautorentapi.utilities.NotFoundException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CarRepositoryDB implements ICarRepository {

    private final JdbcClient jdbcClient;

    public CarRepositoryDB(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Car> getCarList() {
        return jdbcClient.sql("SELECT id,brand,model,licencePlateNumber,category FROM cars")
                .query(Car.class)
                .list();
    }

    public void addCar(Car car) {
        jdbcClient.sql("INSERT INTO cars(id,brand,model,licencePlateNumber,category) values (?,?,?,?,?)")
                .params(List.of(car.getId(), car.getBrand(), car.getModel(), car.getLicensePlateNumber(), car.getCategory().toString()))
                .update();
    }

    public void deleteAllCars() {
        jdbcClient.sql("DELETE FROM cars").update();
    }

    public Car getCarById(String carID) throws NotFoundException {
        try {
            return jdbcClient.sql("SELECT * FROM cars WHERE id=?")
                    .params(List.of(carID))
                    .query(Car.class).list().get(0);
        } catch (Exception e) {
            throw new NotFoundException();
        }
    }

    public void deleteCarById(String id) throws NotFoundException {
        if (jdbcClient.sql("DELETE FROM cars WHERE id=?").params(List.of(id)).update() == 0) {
            throw new NotFoundException();
        }
    }

}
