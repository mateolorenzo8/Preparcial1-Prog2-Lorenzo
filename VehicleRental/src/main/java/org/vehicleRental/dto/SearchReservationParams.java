package org.vehicleRental.dto;

import org.vehicleRental.enums.ReservationStatus;

import java.time.LocalDate;

public class SearchReservationParams {
    private String clientName;
    private LocalDate startDate;
    private LocalDate endDate;
    private ReservationStatus status;
    private String vehicleBrand;

    public SearchReservationParams(String clientName) {
        this.clientName = clientName;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getClientName() {
        return clientName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }
}
