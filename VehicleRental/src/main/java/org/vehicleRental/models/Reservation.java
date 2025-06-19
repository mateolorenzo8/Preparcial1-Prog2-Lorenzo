package org.vehicleRental.models;

import jakarta.persistence.*;
import org.vehicleRental.enums.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private long id;

    @Column(name = "client_name", length = 80)
    private String clientName;

    @ManyToOne()
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "total_cost", precision = 12, scale = 2)
    private BigDecimal totalCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReservationStatus status;

    public Reservation() {}

    public Reservation(String clientName, Vehicle vehicle, LocalDate startDate, LocalDate endDate) {
        this.clientName = clientName;
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = ReservationStatus.RESERVED;
        setTotalCost();
    }

    public long getId() {
        return id;
    }

    public String getClientName() {
        return clientName;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public void setTotalCost() {
        this.totalCost = this.vehicle.getDailyRate().multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(getStartDate(), getEndDate())));
    }
}
