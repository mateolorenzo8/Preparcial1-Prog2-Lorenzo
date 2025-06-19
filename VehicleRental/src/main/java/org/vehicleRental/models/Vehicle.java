package org.vehicleRental.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;

@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "plate_number", length = 10)
    private String plateNumber;

    @Column(name = "brand", length = 50)
    private String brand;

    @Column(name = "model", length = 50)
    private String model;

    @Column(name = "daily_rate", precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @Column(name = "available")
    private int available;

    public Vehicle() {}

    public Vehicle(String plateNumber, String brand, String model, BigDecimal dailyRate) {
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.dailyRate = dailyRate;
    }

    public long getId() {
        return id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }
}
