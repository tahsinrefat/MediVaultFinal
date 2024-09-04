package com.medi_vault_final.MediVaultFinal.service;

import com.medi_vault_final.MediVaultFinal.dto.PrescriptionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PrescriptionService {
    PrescriptionDto createPrescription(PrescriptionDto prescriptionDto);

    PrescriptionDto getPrescriptionById(Long prescriptionId);

    Page<PrescriptionDto> getAllPrescription(Pageable pageable);

    PrescriptionDto updatePrescriptionById(Long prescriptionId, PrescriptionDto updatedPrescriptionDto);

    void deletePrescriptionById(Long prescriptionId);

    Page<PrescriptionDto> getPrescriptionByCurrentMonth(Pageable pageable);

    Page<PrescriptionDto> getAllPrescriptionByDateRange(LocalDate fromDate, LocalDate toDate, Pageable pageable);

    Page<Object[]> getPrescriptionCountByDate(LocalDate date, Pageable pageable);
}
