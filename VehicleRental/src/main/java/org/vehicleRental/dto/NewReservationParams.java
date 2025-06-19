package org.vehicleRental.dto;

import java.time.LocalDate;

public class NewReservationParams {
    private String clientName;
    private long vehicleId;
    private LocalDate startDate;
    private LocalDate endDate;

    public NewReservationParams(String clientName, long vehicleId, LocalDate startDate, LocalDate endDate) {
        this.clientName = clientName;
        this.vehicleId = vehicleId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getClientName() {
        return clientName;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
