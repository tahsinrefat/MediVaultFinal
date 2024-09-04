package com.medi_vault_final.MediVaultFinal.repository;

import com.medi_vault_final.MediVaultFinal.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    @Query("SELECT p FROM Prescription p WHERE MONTH(p.prescriptionDate) = MONTH(:currentDate) AND YEAR(p.prescriptionDate) = YEAR(:currentDate)")
    Page<Prescription> findByCurrentMonth(LocalDate currentDate, Pageable pageable);

    @Query("SELECT p FROM Prescription p WHERE p.prescriptionDate BETWEEN :fromDate AND :toDate")
    Page<Prescription> findAllByPrescriptionDateRange(LocalDate fromDate, LocalDate toDate, Pageable pageable);

    /* has some issue have to work on that later */
    @Query("SELECT u.id, u.name, COUNT(p.id) AS prescriptionCount FROM Prescription p JOIN p.doctor u WHERE p.prescriptionDate = :date GROUP BY p.doctor")
    Page<Object[]> findPrescriptionCountByDate(LocalDate date, Pageable pageable);
    /* has some issue have to work on that later */

    @Query("SELECT p FROM Prescription p WHERE MONTH(p.prescriptionDate) = MONTH(:currentDate) AND YEAR(p.prescriptionDate) = YEAR(:currentDate) AND p.patient.id = :userId")
    Page<Prescription> findByCurrentMonthAndUserId(LocalDate currentDate, Long userId, Pageable pageable);

    @Query("SELECT p FROM Prescription p WHERE p.prescriptionDate BETWEEN :fromDate AND :toDate AND (p.patient.id = :userId)")
    Page<Prescription> findByDateRangeAndUserId(LocalDate fromDate, LocalDate toDate, Long userId, Pageable pageable);
}
