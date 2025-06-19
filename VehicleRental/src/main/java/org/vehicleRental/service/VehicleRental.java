package org.vehicleRental.service;

import org.hibernate.Session;
import org.vehicleRental.dto.EndReservationParams;
import org.vehicleRental.dto.NewReservationParams;
import org.vehicleRental.dto.Result;
import org.vehicleRental.enums.ReservationStatus;
import org.vehicleRental.models.Reservation;
import org.vehicleRental.models.Vehicle;
import org.vehicleRental.utils.HibernateUtil;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

public final class VehicleRental {
    private static volatile VehicleRental instance;

    private VehicleRental() {}

    public static VehicleRental getInstance() {
        if (instance == null) {
            instance = new VehicleRental();
        }
        return instance;
    }

    public Result appointReservation(NewReservationParams params) {
        Vehicle vehicle = new Vehicle();

        try (Session session = HibernateUtil.getSession()) {
            vehicle = session.get(Vehicle.class, params.getVehicleId());

            if (vehicle == null) return new Result(false, "Vehicle does not exist");
        }

        if (vehicle.getAvailable() != 1) return new Result(false, "Vehicle is already taken");

        Reservation reservation = new Reservation(params.getClientName(), vehicle, params.getStartDate(), params.getEndDate());

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();
            session.save(reservation);
            session.getTransaction().commit();
        }

        return new Result(true, "Vehicle appointed, total: " + reservation.getTotalCost());
    }

    public Result endReservation(EndReservationParams params) {
        Reservation reservation = new Reservation();

        try (Session session = HibernateUtil.getSession()) {
            reservation = session.get(Reservation.class, params.getReservationId());

            if (reservation == null) return new Result(false, "Reservation not found");
        }

        if (reservation.getStatus() != ReservationStatus.RESERVED) return new Result(false, "Reservation has already finished");

        String detail = new String();

        if (reservation.getEndDate().isBefore(params.getReturnDate())) {
            long extraDays = ChronoUnit.DAYS.between(reservation.getEndDate(), params.getReturnDate());
            BigDecimal extraCharges = reservation.getTotalCost().multiply(BigDecimal.valueOf(0.1)).multiply(BigDecimal.valueOf(extraDays));

            reservation.setTotalCost(reservation.getTotalCost().add(extraCharges));

            detail = "Reservation ended, extra charges: " + extraCharges;
        }
        else {
            detail = "Reservation already finished";
        }

        reservation.setStatus(ReservationStatus.COMPLETED);

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();
            session.update(reservation);
            session.getTransaction().commit();
        }

        return new Result(true, detail);
    }
}
