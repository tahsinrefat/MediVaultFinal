package com.medi_vault_final.MediVaultFinal.mapper;

import com.medi_vault_final.MediVaultFinal.dto.PrescriptionDto;
import com.medi_vault_final.MediVaultFinal.entity.Prescription;

public class PrescriptionMapper {

    public static PrescriptionDto mapToPrescriptionDto(Prescription prescription) {
        return new PrescriptionDto(
                prescription.getId(),
                prescription.getPrescriptionDate(),
                prescription.getPatient().getId(),
                prescription.getDoctor().getId(),
                prescription.getNextVisitDate(),
                prescription.getDiagnosis(),
                prescription.getMedicine()
        );
    }

    public static Prescription mapToPrescription(PrescriptionDto prescriptionDto){
        return Prescription.builder()
                .id(prescriptionDto.id())
                .prescriptionDate(prescriptionDto.prescriptionDate())
                .nextVisitDate(prescriptionDto.nextVisitDate())
                .diagnosis(prescriptionDto.diagnosis())
                .medicine(prescriptionDto.medicine())
                .build();
    }
}
