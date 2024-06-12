package application.dto;

import domain.*;
import utilities.*;

import java.util.UUID;

public class CarDTO {


    private String licencePlateNumber;
    private String brand;
    private String model;
    private CarType category;
    private String id;


    public CarDTO(String licencePlateNumber, String brand, String model, String category) throws Exception {
        if (licencePlateNumber == null || brand == null || model == null || category == null)
            throw new InvalidParamsException();
        this.licencePlateNumber = licencePlateNumber;
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
        licencePlateNumber = car.getLicencePlateNumber();
        brand = car.getBrand();
        model = car.getModel();
        category = car.getCategory();
        id = car.getId();
    }

    public String getLicencePlateNumber() {
        return licencePlateNumber;
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
