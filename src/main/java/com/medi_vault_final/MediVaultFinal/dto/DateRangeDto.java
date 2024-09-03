package com.medi_vault_final.MediVaultFinal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record DateRangeDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-DD") String fromDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-DD") String toDate) {
}
