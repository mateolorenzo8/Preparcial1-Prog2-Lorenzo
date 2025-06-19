package org.vehicleRental.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.vehicleRental.dto.EndReservationParams;
import org.vehicleRental.dto.NewReservationParams;
import org.vehicleRental.dto.Result;
import org.vehicleRental.dto.SearchReservationParams;
import org.vehicleRental.enums.ReservationStatus;
import org.vehicleRental.models.Reservation;
import org.vehicleRental.models.Vehicle;
import org.vehicleRental.utils.HibernateUtil;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public final class VehicleRental {
    private static volatile VehicleRental instance;

    private VehicleRental() {}

    public static VehicleRental getInstance() {
        if (instance == null) {
            instance = new VehicleRental();
        }
        return instance;
    }

    public Result newReservation(NewReservationParams params) {
        Vehicle vehicle;

        try (Session session = HibernateUtil.getSession()) {
            vehicle = session.get(Vehicle.class, params.getVehicleId());

            if (vehicle == null) return new Result(false, "Vehicle does not exist");
        }

        if (vehicle.getAvailable() != 1) return new Result(false, "Vehicle is already taken");

        Reservation reservation = new Reservation(params.getClientName(), vehicle, params.getStartDate(), params.getEndDate());
        vehicle.setAvailable(0);

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();
            session.persist(reservation);
            session.merge(vehicle);
            session.getTransaction().commit();
        }

        return new Result(true, "Vehicle reserved");
    }

    public Result endReservation(EndReservationParams params) {
        Reservation reservation = new Reservation();

        try (Session session = HibernateUtil.getSession()) {
            reservation = session.get(Reservation.class, params.getReservationId());

            if (reservation == null) return new Result(false, "Reservation not found");
        }

        if (reservation.getStatus() != ReservationStatus.RESERVED) return new Result(false, "Reservation has already finished");

        String detail;

        if (reservation.getEndDate().isBefore(params.getReturnDate())) {
            long extraDays = ChronoUnit.DAYS.between(reservation.getEndDate(), params.getReturnDate());
            BigDecimal extraCharges = reservation.getTotalCost().multiply(BigDecimal.valueOf(0.1)).multiply(BigDecimal.valueOf(extraDays));

            reservation.setTotalCost(reservation.getTotalCost().add(extraCharges));

            detail = "Reservation ended, extra charges: " + extraCharges;
        }
        else {
            detail = "Reservation finished on time";
        }

        reservation.setStatus(ReservationStatus.COMPLETED);

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();
            session.update(reservation);
            session.getTransaction().commit();
        }

        return new Result(true, detail);
    }

    public List<Reservation> searchReservation(SearchReservationParams params) {
        try (Session session = HibernateUtil.getSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Reservation> query = builder.createQuery(Reservation.class);
            Root<Reservation> root = query.from(Reservation.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(builder.equal(root.get("clientName"), params.getClientName()));

            if (params.getStartDate() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("startDate"), params.getStartDate()));
            }

            if (params.getEndDate() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("endDate"), params.getEndDate()));
            }

            if (params.getStatus() != null) {
                predicates.add(builder.equal(root.get("status"), params.getStatus()));
            }

            if (params.getVehicleBrand() != null) {
                predicates.add(builder.equal(root.get("vehicle").get("brand"), params.getVehicleBrand()));
            }

            query.where(predicates.toArray(new Predicate[predicates.size()]));

            return session.createQuery(query).list();
        }
    }
}
