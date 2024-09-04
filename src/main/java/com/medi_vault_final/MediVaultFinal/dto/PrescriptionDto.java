package com.medi_vault_final.MediVaultFinal.dto;

import java.time.LocalDate;

public record PrescriptionDto(Long id,
                              LocalDate prescriptionDate,
                              Long patientId,
                              Long doctorId,
                              LocalDate nextVisitDate,
                              String diagnosis,
                              String medicine
) {

}
