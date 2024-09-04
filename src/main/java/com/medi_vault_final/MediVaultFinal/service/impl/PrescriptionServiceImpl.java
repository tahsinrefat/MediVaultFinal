package com.medi_vault_final.MediVaultFinal.service.impl;

import com.medi_vault_final.MediVaultFinal.dto.PrescriptionDto;
import com.medi_vault_final.MediVaultFinal.entity.Prescription;
import com.medi_vault_final.MediVaultFinal.exception.PrescriptionNotFoundException;
import com.medi_vault_final.MediVaultFinal.exception.UserNotFoundException;
import com.medi_vault_final.MediVaultFinal.mapper.PrescriptionMapper;
import com.medi_vault_final.MediVaultFinal.repository.PrescriptionRepository;
import com.medi_vault_final.MediVaultFinal.repository.UserRepository;
import com.medi_vault_final.MediVaultFinal.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    private final UserRepository userRepository;

    @Override
    public PrescriptionDto createPrescription(PrescriptionDto prescriptionDto) {
        Prescription prescription = PrescriptionMapper.mapToPrescription(prescriptionDto);
        prescription.setPatient(userRepository.findById(prescriptionDto.patientId()).orElseThrow( () -> new UserNotFoundException("No user found with ID "+prescriptionDto.patientId())));
        prescription.setDoctor(userRepository.findById(prescriptionDto.doctorId()).orElseThrow( () -> new UserNotFoundException("User not found with ID "+prescriptionDto.doctorId())));
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        return PrescriptionMapper.mapToPrescriptionDto(savedPrescription);
    }

    @Override
    public PrescriptionDto getPrescriptionById(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId).orElseThrow( () -> new PrescriptionNotFoundException("No prescription found with ID "+prescriptionId));
        return PrescriptionMapper.mapToPrescriptionDto(prescription);
    }

    @Override
    public Page<PrescriptionDto> getAllPrescription(Pageable pageable) {
        Page<Prescription> allPrescriptions = prescriptionRepository.findAll(pageable);
        return allPrescriptions.map(PrescriptionMapper::mapToPrescriptionDto);
    }

    @Override
    public PrescriptionDto updatePrescriptionById(Long prescriptionId, PrescriptionDto updatedPrescriptionDto) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId).orElseThrow( () -> new PrescriptionNotFoundException("No prescription found with ID "+prescriptionId));
        prescription.setPrescriptionDate(updatedPrescriptionDto.prescriptionDate());
        prescription.setPatient(userRepository.findById(updatedPrescriptionDto.patientId()).orElseThrow( () -> new UserNotFoundException("No user found with ID "+ updatedPrescriptionDto.patientId())));
        prescription.setDoctor(userRepository.findById(updatedPrescriptionDto.doctorId()).orElseThrow( () -> new UserNotFoundException("Doctor not found with ID "+updatedPrescriptionDto.doctorId())));
        prescription.setNextVisitDate(updatedPrescriptionDto.nextVisitDate());
        prescription.setDiagnosis(updatedPrescriptionDto.diagnosis());
        prescription.setMedicine(updatedPrescriptionDto.medicine());

        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        return PrescriptionMapper.mapToPrescriptionDto(updatedPrescription);
    }

    @Override
    public void deletePrescriptionById(Long prescriptionId) {
        prescriptionRepository.findById(prescriptionId).orElseThrow( () -> new PrescriptionNotFoundException("No prescription found with ID "+prescriptionId));
        prescriptionRepository.deleteById(prescriptionId);
    }

    @Override
    public Page<PrescriptionDto> getPrescriptionByCurrentMonth(Pageable pageable) {
        Page<Prescription> currentDatePrescriptions = prescriptionRepository.findByCurrentMonth(LocalDate.now(), pageable);
        return currentDatePrescriptions.map(PrescriptionMapper::mapToPrescriptionDto);
    }

    @Override
    public Page<PrescriptionDto> getAllPrescriptionByDateRange(LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        Page<Prescription> allPrescriptionsByDateRange = prescriptionRepository.findAllByPrescriptionDateRange(fromDate, toDate, pageable);
        return allPrescriptionsByDateRange.map(PrescriptionMapper::mapToPrescriptionDto);
    }

    /* has some issue have to work on that later */
    @Override
    public Page<Object[]> getPrescriptionCountByDate(LocalDate date, Pageable pageable) {
//        Page<Object[]> allPrescriptionByDate = prescriptionRepository.findPrescriptionCountByDate(date, pageable);
//        return allPrescriptionByDate.map(PrescriptionMapper::mapToPrescriptionDto);
        return prescriptionRepository.findPrescriptionCountByDate(date, pageable);
    }
    /* has some issue have to work on that later */

    @Override
    public Page<PrescriptionDto> getPrescriptionByCurrentMonthAndUser(Long userId, Pageable pageable) {
        Page<Prescription> allPrescriptionByCurrentMonthAndUserId = prescriptionRepository.findByCurrentMonthAndUserId(LocalDate.now(), userId, pageable);
        return allPrescriptionByCurrentMonthAndUserId.map(PrescriptionMapper::mapToPrescriptionDto);
    }

    @Override
    public Page<PrescriptionDto> getPrescriptionByDateRangeAndUserId(LocalDate fromDate, LocalDate toDate, Long userId, Pageable pageable) {
        Page<Prescription> allPrescriptionByDateRangeAndId = prescriptionRepository.findByDateRangeAndUserId(fromDate, toDate, userId, pageable);
        return allPrescriptionByDateRangeAndId.map(PrescriptionMapper::mapToPrescriptionDto);
    }

    @Override
    public Page<PrescriptionDto> getPrescriptionByCurrentMonthAndDoctor(Long doctorId, Pageable pageable) {
        Page<Prescription> allPrescriptionByCurrentMonthAndDoctorId = prescriptionRepository.findByCurrentMonthAndDoctorId(LocalDate.now(), doctorId, pageable);
        return allPrescriptionByCurrentMonthAndDoctorId.map(PrescriptionMapper::mapToPrescriptionDto);
    }

    @Override
    public Page<PrescriptionDto> getPrescriptionByDateRangeAndDoctor(LocalDate fromDate, LocalDate toDate, Long doctorId, Pageable pageable) {
        Page<Prescription> allPrescriptionByDateRangeAndDoctorId = prescriptionRepository.findByDateRangeAndDoctorId(fromDate, toDate, doctorId, pageable);
        return allPrescriptionByDateRangeAndDoctorId.map(PrescriptionMapper::mapToPrescriptionDto);
    }
}
