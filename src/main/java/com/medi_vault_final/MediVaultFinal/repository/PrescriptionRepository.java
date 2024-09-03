package com.medi_vault_final.MediVaultFinal.repository;

import com.medi_vault_final.MediVaultFinal.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    @Query("SELECT p FROM Prescription p WHERE MONTH(p.prescriptionDate) = MONTH(:currentDate) AND YEAR(p.prescriptionDate) = YEAR(:currentDate)")
    Page<Prescription> findByCurrentMonth(Date currentDate, Pageable pageable);

    @Query("SELECT p FROM Prescription p WHERE p.prescriptionDate BETWEEN :fromDate AND :toDate")
    Page<Prescription> findAllByPrescriptionDateRange(Date fromDate, Date toDate, Pageable pageable);
}
