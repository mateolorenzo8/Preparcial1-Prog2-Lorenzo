package org.vehicleRental.service;

import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vehicleRental.dto.EndReservationParams;
import org.vehicleRental.dto.NewReservationParams;
import org.vehicleRental.dto.Result;
import org.vehicleRental.dto.SearchReservationParams;
import org.vehicleRental.enums.ReservationStatus;
import org.vehicleRental.models.Reservation;
import org.vehicleRental.models.Vehicle;
import org.vehicleRental.utils.HibernateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VehicleRentalTest {

    Session session;
    Vehicle car1;
    Reservation res1;
    Reservation res2023;
    Reservation res2024;
    Reservation res2024bis;
    VehicleRental service;

    @BeforeEach
    void setUp() {
        session = HibernateUtil.getSession();
        service = service.getInstance();

        car1 = new Vehicle("MNK2654LKK", "Honda", "CR-V", BigDecimal.valueOf(60));
        car1.setAvailable(1);

        res1 = new Reservation("Juan Lopez", car1, LocalDate.now(), LocalDate.now().plusDays(5));

        res2023 = new Reservation("Juan Lopez", car1, LocalDate.now().minusYears(2), LocalDate.now().minusYears(2).plusDays(2));
        res2023.setStatus(ReservationStatus.COMPLETED);

        res2024 = new Reservation("Juan Lopez", car1, LocalDate.now().minusYears(1), LocalDate.now().minusYears(1).plusDays(2));
        res2024.setStatus(ReservationStatus.COMPLETED);

        res2024bis = new Reservation("Juan Lopez", car1, LocalDate.now().minusYears(1), LocalDate.now().minusYears(1).plusDays(2));
        res2024bis.setStatus(ReservationStatus.CANCELLED);

        session.beginTransaction();

        session.persist(car1);
        session.persist(res1);
        session.persist(res2023);
        session.persist(res2024);
        session.persist(res2024bis);

        session.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (session != null && session.isOpen()) {
            session.beginTransaction();
            session.createQuery("delete from Reservation").executeUpdate();
            session.createQuery("delete from Vehicle").executeUpdate();
            session.getTransaction().commit();
            session.close();
        }
    }

    @Test
    void makeReservationNonExistingCar() {
        NewReservationParams params = new NewReservationParams("Juan Pichu", 78985, LocalDate.now(), LocalDate.now().plusDays(5));

        Result res = service.newReservation(params);

        Assertions.assertEquals(false, res.isSuccess());
        Assertions.assertEquals("Vehicle does not exist", res.getMessage());
    }

    @Test
    void makeReservationTakenCar() {
        NewReservationParams params = new NewReservationParams("Juan Pichu", car1.getId(), LocalDate.now(), LocalDate.now().plusDays(5));

        service.newReservation(params);

        Result res = service.newReservation(params);

        Assertions.assertEquals(false, res.isSuccess());
        Assertions.assertEquals("Vehicle is already taken", res.getMessage());
    }

    @Test
    void makeReservationSuccess() {
        NewReservationParams params = new NewReservationParams("Juan Pichu", car1.getId(), LocalDate.now(), LocalDate.now().plusDays(5));

        Result res = service.newReservation(params);

        Assertions.assertEquals(true, res.isSuccess());
        Assertions.assertEquals("Vehicle reserved", res.getMessage());
    }

    @Test
    void endNonExistingReservation() {
        EndReservationParams params = new EndReservationParams(489875, LocalDate.now());

        Result res = service.endReservation(params);

        Assertions.assertEquals(false, res.isSuccess());
        Assertions.assertEquals("Reservation not found", res.getMessage());
    }

    @Test
    void endAlreadyFinishedReservation() {
        EndReservationParams params2 = new EndReservationParams(res1.getId(), LocalDate.now());

        service.endReservation(params2);
        Result res = service.endReservation(params2);

        Assertions.assertEquals(false, res.isSuccess());
        Assertions.assertEquals("Reservation has already finished", res.getMessage());
    }

    @Test
    void endReservationWithOverdue() {
        EndReservationParams params2 = new EndReservationParams(res1.getId(), LocalDate.now().plusDays(10));

        Result res = service.endReservation(params2);

        Assertions.assertEquals(true, res.isSuccess());
        Assertions.assertEquals("Reservation ended, extra charges: " + car1.getDailyRate().multiply(BigDecimal.valueOf(5)).multiply(BigDecimal.valueOf(0.1)).multiply(BigDecimal.valueOf(5)) + "00", res.getMessage());
    }

    @Test
    void endReservationOnTime() {
        NewReservationParams params = new NewReservationParams("Juan", car1.getId(), LocalDate.now(), LocalDate.now().plusDays(5));

        EndReservationParams params2 = new EndReservationParams(res1.getId(), LocalDate.now().plusDays(4));

        service.newReservation(params);
        Result res = service.endReservation(params2);

        Assertions.assertEquals(true, res.isSuccess());
        Assertions.assertEquals("Reservation finished on time", res.getMessage());
    }

    @Test
    void searchWithAllParametersOnlyFrom2024() {
        SearchReservationParams params = new SearchReservationParams("Juan Lopez");
        params.setStatus(ReservationStatus.COMPLETED);
        params.setStartDate(LocalDate.of(2024, 1, 1));
        params.setEndDate(LocalDate.of(2024, 12, 31));
        params.setVehicleBrand("Honda");

        List<Reservation> res = service.searchReservation(params);

        Assertions.assertEquals(1, res.size());
        Assertions.assertEquals(res2024.getId(), res.get(0).getId());
    }
}