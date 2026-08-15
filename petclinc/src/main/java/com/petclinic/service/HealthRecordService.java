// --------- HealthRecordService.java ---------
package com.petclinic.service;

import com.petclinic.exception.ResourceNotFoundException;
import com.petclinic.model.HealthRecord;
import com.petclinic.model.Pet;
import com.petclinic.repository.HealthRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthRecordService {
    private final HealthRecordRepository healthRecordRepository;

    public List<HealthRecord> getAllHealthRecords() {
        return healthRecordRepository.findAll();
    }

    public HealthRecord getHealthRecordById(Long id) {
        return healthRecordRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Health record not found"));
    }

    public List<HealthRecord> getHealthRecordsByPet(Pet pet) {
        return healthRecordRepository.findByPetOrderByRecordDateDesc(pet);
    }

    public HealthRecord createHealthRecord(HealthRecord healthRecord) {
        return healthRecordRepository.save(healthRecord);
    }

    public HealthRecord updateHealthRecord(Long id, HealthRecord recordDetails) {
        HealthRecord healthRecord = getHealthRecordById(id);
        healthRecord.setRecordType(recordDetails.getRecordType());
        healthRecord.setRecordDate(recordDetails.getRecordDate());
        healthRecord.setDescription(recordDetails.getDescription());
        healthRecord.setMedication(recordDetails.getMedication());
        healthRecord.setDosage(recordDetails.getDosage());
        healthRecord.setDiagnosis(recordDetails.getDiagnosis());
        healthRecord.setTreatment(recordDetails.getTreatment());
        healthRecord.setNotes(recordDetails.getNotes());
        healthRecord.setVeterinarian(recordDetails.getVeterinarian());
        return healthRecordRepository.save(healthRecord);
    }

    public void deleteHealthRecord(Long id) {
        healthRecordRepository.deleteById(id);
    }
}
