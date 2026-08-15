// --------- AppointmentService.java ---------
package com.petclinic.service;

import com.petclinic.model.Appointment;
import com.petclinic.model.Pet;
import com.petclinic.model.Veterinarian;
import com.petclinic.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public List<Appointment> getAppointmentsByPet(Pet pet) {
        return appointmentRepository.findByPet(pet);
    }

    public List<Appointment> getAppointmentsByVeterinarian(Veterinarian veterinarian) {
        return appointmentRepository.findByVeterinarian(veterinarian);
    }

    public List<Appointment> getUpcomingAppointments() {
        return appointmentRepository.findByAppointmentTimeAfter(LocalDateTime.now());
    }

    public Appointment createAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
        Appointment appointment = getAppointmentById(id);
        appointment.setPet(appointmentDetails.getPet());
        appointment.setVeterinarian(appointmentDetails.getVeterinarian());
        appointment.setAppointmentTime(appointmentDetails.getAppointmentTime());
        appointment.setReason(appointmentDetails.getReason());
        appointment.setStatus(appointmentDetails.getStatus());
        appointment.setNotes(appointmentDetails.getNotes());
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public Appointment updateStatus(Long id, String status) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }
}

