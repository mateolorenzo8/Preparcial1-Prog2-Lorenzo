package org.vehicleRental.dto;

import java.time.LocalDate;

public class EndReservationParams {
    private long reservationId;
    private LocalDate returnedDate;

    public EndReservationParams(long reservationId, LocalDate returnDate) {
        this.reservationId = reservationId;
        this.returnedDate = returnDate;
    }

    public long getReservationId() {
        return reservationId;
    }

    public LocalDate getReturnDate() {
        return returnedDate;
    }
}
