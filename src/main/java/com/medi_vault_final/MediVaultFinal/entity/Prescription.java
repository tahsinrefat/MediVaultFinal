package com.medi_vault_final.MediVaultFinal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "prescription_table")
@Builder
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull(message = "Date cannot be null")
    private Date prescriptionDate;

    @JoinColumn(name = "patient_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User patient;

    @JoinColumn(name = "doctor_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User doctor;

    @Column
    private Date nextVisitDate;

    @Column
    @NotBlank(message = "Diagnosis cannot be null")
    private String diagnosis;

    @Column
    @NotBlank(message = "Medicine cannot be null")
    private String medicine;
}
