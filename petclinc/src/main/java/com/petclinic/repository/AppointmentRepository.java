// --------- AppointmentRepository.java ---------
package com.petclinic.repository;

import com.petclinic.model.Appointment;
import com.petclinic.model.Pet;
import com.petclinic.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPet(Pet pet);
    List<Appointment> findByVeterinarian(Veterinarian veterinarian);
    List<Appointment> findByAppointmentTimeAfter(LocalDateTime dateTime);
    List<Appointment> findByStatusOrderByAppointmentTimeAsc(String status);
    List<Appointment> findByAppointmentTimeBetweenOrderByAppointmentTimeAsc(LocalDateTime start, LocalDateTime end);
}

