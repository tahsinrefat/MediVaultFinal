package com.medi_vault_final.MediVaultFinal.service;

import com.medi_vault_final.MediVaultFinal.dto.PrescriptionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

public interface PrescriptionService {
    PrescriptionDto createPrescription(PrescriptionDto prescriptionDto);

    PrescriptionDto getPrescriptionById(Long prescriptionId);

    Page<PrescriptionDto> getAllPrescription(Pageable pageable);

    PrescriptionDto updatePrescriptionById(Long prescriptionId, PrescriptionDto updatedPrescriptionDto);

    void deletePrescriptionById(Long prescriptionId);

    Page<PrescriptionDto> getPrescriptionByCurrentMonth(Pageable pageable);

    Page<PrescriptionDto> getAllPrescriptionByDateRange(Date fromDate, Date toDate, Pageable pageable);
}
