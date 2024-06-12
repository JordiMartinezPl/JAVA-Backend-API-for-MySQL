package com.tecnocampus.groupfautorentapi.domain;

public enum CarType {
    ECONOMY(500, 30, "economy"),
    LUXURY(1200, 80, "luxury"),
    FAMILY(700, 50, "family"),
    MINI(400, 20, "mini");

    private final String name;
    private final int deposit;
    private final int dayPrice;

    private CarType(int deposit, int dayPrice, String name) {
        this.deposit = deposit;
        this.dayPrice = dayPrice;
        this.name = name;
    }

    public int getDeposit() {
        return deposit;
    }

    public int getDayPrice() {
        return dayPrice;
    }

    public double calculateDeposit(double damagePercentage) {
        return deposit * damagePercentage / 100;
    }

    public String getName() {
        return name;
    }
}