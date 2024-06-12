package com.tecnocampus.groupfautorentapi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tecnocampus.groupfautorentapi.domain.Car;
import com.tecnocampus.groupfautorentapi.domain.CarType;
import com.tecnocampus.groupfautorentapi.utilities.InvalidParamsException;

import java.util.UUID;

public class CarDTO {


    private String licensePlateNumber;
    private String brand;
    private String model;
    private CarType category;
    private String id;


    public CarDTO(@JsonProperty("licencePlateNumber") String licensePlateNumber,
                  @JsonProperty("brand") String brand,
                  @JsonProperty("model") String model,
                  @JsonProperty("category") String category) throws Exception {
        if (licensePlateNumber == null || brand == null || model == null || category == null)
            throw new InvalidParamsException();
        this.licensePlateNumber = licensePlateNumber;
        this.brand = brand;
        this.model = model;
        checkCategory(category);
        this.id = UUID.randomUUID().toString();
    }

    private void checkCategory(String category) throws Exception {//CAMBIAR
        if (category.equalsIgnoreCase("economy")) this.category = CarType.ECONOMY;
        else if (category.equalsIgnoreCase("luxury")) this.category = CarType.LUXURY;
        else if (category.equalsIgnoreCase("family")) this.category = CarType.FAMILY;
        else if (category.equalsIgnoreCase("mini")) this.category = CarType.MINI;
        else throw new InvalidParamsException();
    }

    public CarDTO(Car car) throws Exception {
        if (car == null) throw new InvalidParamsException();
        licensePlateNumber = car.getLicensePlateNumber();
        brand = car.getBrand();
        model = car.getModel();
        category = car.getCategory();
        id = car.getId();
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public CarType getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

}
