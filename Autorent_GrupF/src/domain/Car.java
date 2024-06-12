package domain;

import utilities.*;
import application.dto.CarDTO;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Car {

    private String licencePlateNumber;
    private String brand;
    private String model;
    private CarType category;
    private String id;


    public Car(String licencePlateNumber, String brand, String model, String category) throws Exception {
        if (licencePlateNumber == null || brand == null || model == null || category == null)
            throw new InvalidParamsException();
        this.licencePlateNumber = licencePlateNumber;
        this.brand = brand;
        this.model = model;
        checkCategory(category);
        this.id = UUID.randomUUID().toString();
    }

    public Car(CarDTO carDTO) throws Exception {
        if (carDTO.getLicencePlateNumber() == null || carDTO.getBrand() == null || carDTO.getModel() == null || carDTO.getCategory() == null)
            throw new InvalidParamsException();
        this.licencePlateNumber = carDTO.getLicencePlateNumber();
        this.brand = carDTO.getBrand();
        this.model = carDTO.getModel();
        checkCategory(carDTO.getCategory().getName().toUpperCase());
        this.id = carDTO.getId();

    }

    private void checkCategory(String category) throws Exception {//CAMBIAR
        if (category.equalsIgnoreCase("economy")) this.category = CarType.ECONOMY;
        else if (category.equalsIgnoreCase("luxury")) this.category = CarType.LUXURY;
        else if (category.equalsIgnoreCase("family")) this.category = CarType.FAMILY;
        else if (category.equalsIgnoreCase("mini")) this.category = CarType.MINI;
        else throw new InvalidParamsException();
    }

    public CarType getCategory() {
        return category;
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

    public String getId() {
        return id;
    }

    public double calculatePriceForDays(LocalDateTime firstDate, LocalDateTime lastDate) {
        double totalDays = ChronoUnit.DAYS.between(firstDate, lastDate);
        double amountForDay = 0;
        for (int i = 0; i <= totalDays; i++) {
            LocalDateTime currentDate = firstDate.plusDays(i);
            if (isFridayToSunday(currentDate)) {
                amountForDay += getCategory().getDayPrice() * 1.25;
            } else {
                amountForDay += getCategory().getDayPrice();
            }
        }
        return amountForDay;
    }

    private boolean isFridayToSunday(LocalDateTime date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

}
