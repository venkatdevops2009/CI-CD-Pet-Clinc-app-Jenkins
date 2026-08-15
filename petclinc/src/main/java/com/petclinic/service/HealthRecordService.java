// --------- HealthRecordService.java ---------
package com.petclinic.service;

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
        return healthRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("Health record not found"));
    }

    public List<HealthRecord> getHealthRecordsByPet(Pet pet) {
        return healthRecordRepository.findByPetOrderByRecordDateDesc(pet);
    }

    public HealthRecord createHealthRecord(HealthRecord healthRecord) {
        return healthRecordRepository.save(healthRecord);
    }

    public HealthRecord updateHealthRecord(Long id, HealthRecord recordDetails) {
        HealthRecord record = getHealthRecordById(id);
        record.setRecordType(recordDetails.getRecordType());
        record.setRecordDate(recordDetails.getRecordDate());
        record.setDescription(recordDetails.getDescription());
        record.setMedication(recordDetails.getMedication());
        record.setDosage(recordDetails.getDosage());
        record.setDiagnosis(recordDetails.getDiagnosis());
        record.setTreatment(recordDetails.getTreatment());
        record.setNotes(recordDetails.getNotes());
        record.setVeterinarian(recordDetails.getVeterinarian());
        return healthRecordRepository.save(record);
    }

    public void deleteHealthRecord(Long id) {
        healthRecordRepository.deleteById(id);
    }
}
