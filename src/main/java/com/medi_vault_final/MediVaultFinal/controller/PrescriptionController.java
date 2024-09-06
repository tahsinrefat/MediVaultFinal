package com.medi_vault_final.MediVaultFinal.controller;

import com.medi_vault_final.MediVaultFinal.dto.DateDto;
import com.medi_vault_final.MediVaultFinal.dto.DateRangeDto;
import com.medi_vault_final.MediVaultFinal.dto.PrescriptionDto;
import com.medi_vault_final.MediVaultFinal.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/prescriptions")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;

    @PostMapping("/doctor/create")
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

    //get all prescription for admin within a certain date range
    @GetMapping("/admin/date-range")
    public ResponseEntity<Page<PrescriptionDto>> getAllPrescriptionsByDateRange(@RequestBody DateRangeDto dateRangeDto, Pageable pageable){
        Page<PrescriptionDto> allPrescriptionsByDateRange = prescriptionService.getAllPrescriptionByDateRange(dateRangeDto.fromDate(), dateRangeDto.toDate(), pageable);
        return ResponseEntity.ok(allPrescriptionsByDateRange);
    }

    /* doesn't work */
    @GetMapping("/admin/prescription-count")
    public ResponseEntity<Page<Object[]>> getPrescriptionCountByDate(@RequestBody DateDto date, Pageable pageable){
        return ResponseEntity.ok(prescriptionService.getPrescriptionCountByDate(date.localDate(), pageable));
    }
    /* doesn't work */

    //get all his/her prescriptions as any user by current month
    @GetMapping("/any/current-month-user-id/{id}")
    public ResponseEntity<Page<PrescriptionDto>> getPrescriptionByCurrentMonthAndUserId(@PathVariable("id") Long userId, Pageable pageable){
        Page<PrescriptionDto> allPrescriptionsByCurrentMonthAndUserId = prescriptionService.getPrescriptionByCurrentMonthAndUser(userId, pageable);
        return ResponseEntity.ok(allPrescriptionsByCurrentMonthAndUserId);
    }

    //get all his/her prescriptions as any user by date range
    @GetMapping("/any/date-range-user-id/{id}")
    public ResponseEntity<Page<PrescriptionDto>> getPrescriptionByDateRangeAndUserID(@PathVariable("id") Long userId, @RequestBody DateRangeDto dateRangeDto, Pageable pageable){
        Page<PrescriptionDto> allPrescriptionsByDateRangeAndUserId = prescriptionService.getPrescriptionByDateRangeAndUserId(dateRangeDto.fromDate(), dateRangeDto.toDate(), userId, pageable);
        return ResponseEntity.ok(allPrescriptionsByDateRangeAndUserId);
    }

    //get all his/her written prescriptions as doctor by current month
    @GetMapping("/doctor/current-month-doctor-id/{id}")
    public ResponseEntity<Page<PrescriptionDto>> getPrescriptionByCurrentMonthAndDoctorId(@PathVariable("id") Long doctorId, Pageable pageable){
        Page<PrescriptionDto> allPrescriptionsByCurrentMonthAndDoctorId = prescriptionService.getPrescriptionByCurrentMonthAndDoctor(doctorId, pageable);
        return ResponseEntity.ok(allPrescriptionsByCurrentMonthAndDoctorId);
    }

    //get all his/her written prescriptions as doctor by current month
    @GetMapping("/doctor/date-range-doctor-id/{id}")
    public ResponseEntity<Page<PrescriptionDto>> getPrescriptionByDateRangeAndDoctorID(@PathVariable("id") Long doctorId, @RequestBody DateRangeDto dateRangeDto, Pageable pageable){
        Page<PrescriptionDto> allPrescriptionsByDateRangeAndDoctorId = prescriptionService.getPrescriptionByDateRangeAndDoctor(dateRangeDto.fromDate(), dateRangeDto.toDate(), doctorId, pageable);
        return ResponseEntity.ok(allPrescriptionsByDateRangeAndDoctorId);
    }
}
