// --------- HealthRecordRepository.java ---------
package com.petclinic.repository;

import com.petclinic.model.HealthRecord;
import com.petclinic.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    List<HealthRecord> findByPet(Pet pet);
    List<HealthRecord> findByPetOrderByRecordDateDesc(Pet pet);
    List<HealthRecord> findByRecordTypeAndRecordDateBetween(String recordType, LocalDate startDate, LocalDate endDate);
}
