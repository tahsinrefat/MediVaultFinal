package com.medi_vault_final.MediVaultFinal.controller;

import com.medi_vault_final.MediVaultFinal.dto.DateRangeDto;
import com.medi_vault_final.MediVaultFinal.dto.PrescriptionDto;
import com.medi_vault_final.MediVaultFinal.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/prescriptions")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<PrescriptionDto> createPrescription(@RequestBody PrescriptionDto prescriptionDto){
        PrescriptionDto savedPrescription = prescriptionService.createPrescription(prescriptionDto);
        return ResponseEntity.ok(savedPrescription);
    }

    @GetMapping("{id}")
    public ResponseEntity<PrescriptionDto> getPrescriptionById(@PathVariable("id") Long prescriptionId){
        PrescriptionDto prescriptionDto = prescriptionService.getPrescriptionById(prescriptionId);
        return ResponseEntity.ok(prescriptionDto);
    }

    @GetMapping
    public ResponseEntity<Page<PrescriptionDto>> getAllPrescriptions(Pageable pageable){
        Page<PrescriptionDto> allPrescriptions = prescriptionService.getAllPrescription(pageable);
        return ResponseEntity.ok(allPrescriptions);
    }

    @PutMapping("{id}")
    public ResponseEntity<PrescriptionDto> updatePrescriptionById(@PathVariable("id") Long prescriptionId, @RequestBody PrescriptionDto updatedPrescriptionDto){
        PrescriptionDto updatedPrescriptionDtoAfterSave = prescriptionService.updatePrescriptionById(prescriptionId, updatedPrescriptionDto);
        return ResponseEntity.ok(updatedPrescriptionDtoAfterSave);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") Long prescriptionId){
        prescriptionService.deletePrescriptionById(prescriptionId);
        return ResponseEntity.ok("Prescription deleted successfully");
    }

    //get all the prescription for admin
    @GetMapping("/admin/current-month")
    public ResponseEntity<Page<PrescriptionDto>> getCurrentMonthPrescriptions(Pageable pageable){
        Page<PrescriptionDto> currentMonthPrescriptions = prescriptionService.getPrescriptionByCurrentMonth(pageable);
        return ResponseEntity.ok(currentMonthPrescriptions);
    }

    @GetMapping("/admin/date-range")
    public ResponseEntity<Page<PrescriptionDto>> getAllPrescriptionsByDateRange(@RequestBody DateRangeDto dateRangeDto, Pageable pageable){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate, toDate;
        try {
            fromDate = simpleDateFormat.parse(dateRangeDto.fromDate());
            toDate = simpleDateFormat.parse(dateRangeDto.toDate());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Page<PrescriptionDto> allPrescriptionsByDateRange = prescriptionService.getAllPrescriptionByDateRange(fromDate, toDate, pageable);
        return ResponseEntity.ok(allPrescriptionsByDateRange);
    }
}
