package com.medi_vault_final.MediVaultFinal.dto;

import java.util.Date;

public record PrescriptionDto(Long id,
                              Date prescriptionDate,
                              Long patientId,
                              Long doctorId,
                              Date nextVisitDate,
                              String diagnosis,
                              String medicine
) {

}
