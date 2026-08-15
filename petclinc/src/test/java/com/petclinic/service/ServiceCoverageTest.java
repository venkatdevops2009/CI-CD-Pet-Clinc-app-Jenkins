package com.petclinic.service;

import com.petclinic.model.Appointment;
import com.petclinic.model.HealthRecord;
import com.petclinic.model.Owner;
import com.petclinic.model.Pet;
import com.petclinic.model.PetType;
import com.petclinic.model.Specialty;
import com.petclinic.model.Veterinarian;
import com.petclinic.repository.AppointmentRepository;
import com.petclinic.repository.HealthRecordRepository;
import com.petclinic.repository.OwnerRepository;
import com.petclinic.repository.PetRepository;
import com.petclinic.repository.PetTypeRepository;
import com.petclinic.repository.SpecialtyRepository;
import com.petclinic.repository.VeterinarianRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceCoverageTest {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private VeterinarianRepository veterinarianRepository;

    @Mock
    private PetTypeRepository petTypeRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private HealthRecordRepository healthRecordRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private OwnerService ownerService;

    @InjectMocks
    private PetService petService;

    @InjectMocks
    private VeterinarianService veterinarianService;

    @InjectMocks
    private PetTypeService petTypeService;

    @InjectMocks
    private SpecialtyService specialtyService;

    @InjectMocks
    private HealthRecordService healthRecordService;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void ownerServiceMethodsAreCovered() {
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setFirstName("Alice");
        owner.setLastName("Brown");
        owner.setAddress("One Street");
        owner.setCity("Seattle");
        owner.setTelephone("1234567890");

        when(ownerRepository.findAll()).thenReturn(List.of(owner));
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(ownerRepository.searchByName("Alice", "Brown")).thenReturn(List.of(owner));
        when(ownerRepository.findByCity("Seattle")).thenReturn(List.of(owner));
        when(ownerRepository.save(owner)).thenReturn(owner);

        assertEquals(1, ownerService.getAllOwners().size());
        assertEquals("Alice", ownerService.getOwnerById(1L).getFirstName());
        assertEquals(1, ownerService.searchByName("Alice", "Brown").size());
        assertEquals(1, ownerService.searchByCity("Seattle").size());

        Owner updated = new Owner();
        updated.setFirstName("Alicia");
        updated.setLastName("Brown");
        updated.setAddress("Two Street");
        updated.setCity("Denver");
        updated.setTelephone("9999999999");
        when(ownerRepository.save(owner)).thenReturn(owner);

        Owner result = ownerService.updateOwner(1L, updated);
        assertEquals("Alicia", result.getFirstName());

        ownerService.createOwner(owner);
        ownerService.deleteOwner(1L);
        verify(ownerRepository).deleteById(1L);
    }

    @Test
    void petServiceMethodsAreCovered() {
        Owner owner = new Owner();
        owner.setId(10L);
        owner.setFirstName("Bob");
        owner.setLastName("Jones");
        owner.setAddress("Main");
        owner.setCity("Austin");
        owner.setTelephone("1111111111");

        PetType petType = new PetType();
        petType.setId(5L);
        petType.setName("Dog");

        Pet pet = new Pet();
        pet.setId(2L);
        pet.setName("Max");
        pet.setOwner(owner);
        pet.setPetType(petType);
        pet.setDateOfBirth(LocalDate.of(2020, 1, 2));

        when(petRepository.findAll()).thenReturn(List.of(pet));
        when(petRepository.findById(2L)).thenReturn(Optional.of(pet));
        when(petRepository.findByOwner(owner)).thenReturn(List.of(pet));
        when(petRepository.findByNameContainingIgnoreCase("Max")).thenReturn(List.of(pet));
        when(petRepository.save(pet)).thenReturn(pet);

        assertEquals(1, petService.getAllPets().size());
        assertEquals("Max", petService.getPetById(2L).getName());
        assertEquals(1, petService.getPetsByOwner(owner).size());
        assertEquals(1, petService.searchPets("Max").size());

        Pet updateDetails = new Pet();
        updateDetails.setName("Buddy");
        updateDetails.setDateOfBirth(LocalDate.of(2021, 3, 4));
        PetType newType = new PetType();
        newType.setName("Cat");
        updateDetails.setPetType(newType);

        when(petRepository.save(pet)).thenReturn(pet);

        Pet updated = petService.updatePet(2L, updateDetails);
        assertEquals("Buddy", updated.getName());

        petService.createPet(pet);
        petService.deletePet(2L);
        verify(petRepository).deleteById(2L);
    }

    @Test
    void veterinarianServiceMethodsAreCovered() {
        Veterinarian veterinarian = new Veterinarian();
        veterinarian.setId(3L);
        veterinarian.setFirstName("Dr.");
        veterinarian.setLastName("Smith");
        veterinarian.setEmail("smith@example.com");
        veterinarian.setPhoneNumber("222");
        veterinarian.setLicenseNumber("LIC-1");
        veterinarian.setYearsOfExperience("6");

        when(veterinarianRepository.findAll()).thenReturn(List.of(veterinarian));
        when(veterinarianRepository.findById(3L)).thenReturn(Optional.of(veterinarian));
        when(veterinarianRepository.searchByName("Smith")).thenReturn(List.of(veterinarian));
        when(veterinarianRepository.save(veterinarian)).thenReturn(veterinarian);

        assertEquals(1, veterinarianService.getAllVeterinarians().size());
        assertEquals("Smith", veterinarianService.getVeterinarianById(3L).getLastName());
        assertEquals(1, veterinarianService.searchVeterinarians("Smith").size());

        Veterinarian updatedDetails = new Veterinarian();
        updatedDetails.setFirstName("Dr.");
        updatedDetails.setLastName("Jones");
        updatedDetails.setEmail("jones@example.com");
        updatedDetails.setPhoneNumber("333");
        updatedDetails.setLicenseNumber("LIC-2");
        updatedDetails.setYearsOfExperience("8");

        Veterinarian result = veterinarianService.updateVeterinarian(3L, updatedDetails);
        assertEquals("Jones", result.getLastName());

        veterinarianService.createVeterinarian(veterinarian);
        veterinarianService.deleteVeterinarian(3L);
        verify(veterinarianRepository).deleteById(3L);
    }

    @Test
    void petTypeServiceMethodsAreCovered() {
        PetType petType = new PetType();
        petType.setId(7L);
        petType.setName("Dog");
        petType.setDescription("Friendly companion");

        when(petTypeRepository.findAll()).thenReturn(List.of(petType));
        when(petTypeRepository.findById(7L)).thenReturn(Optional.of(petType));
        when(petTypeRepository.save(petType)).thenReturn(petType);

        assertEquals(1, petTypeService.getAllPetTypes().size());
        assertEquals("Dog", petTypeService.getPetTypeById(7L).getName());

        PetType updatedDetails = new PetType();
        updatedDetails.setName("Large Dog");
        updatedDetails.setDescription("Big dog");

        PetType updated = petTypeService.updatePetType(7L, updatedDetails);
        assertEquals("Large Dog", updated.getName());

        petTypeService.createPetType(petType);
        petTypeService.deletePetType(7L);
        verify(petTypeRepository).deleteById(7L);
    }

    @Test
    void specialtyServiceMethodsAreCovered() {
        Specialty specialty = new Specialty();
        specialty.setId(11L);
        specialty.setName("Surgery");
        specialty.setDescription("Surgical procedures");

        when(specialtyRepository.findAll()).thenReturn(List.of(specialty));
        when(specialtyRepository.findById(11L)).thenReturn(Optional.of(specialty));
        when(specialtyRepository.save(specialty)).thenReturn(specialty);

        assertEquals(1, specialtyService.getAllSpecialties().size());
        assertEquals("Surgery", specialtyService.getSpecialtyById(11L).getName());

        Specialty updatedDetails = new Specialty();
        updatedDetails.setName("Orthopedics");
        updatedDetails.setDescription("Bone care");

        Specialty updated = specialtyService.updateSpecialty(11L, updatedDetails);
        assertEquals("Orthopedics", updated.getName());

        specialtyService.createSpecialty(specialty);
        specialtyService.deleteSpecialty(11L);
        verify(specialtyRepository).deleteById(11L);
    }

    @Test
    void healthRecordServiceMethodsAreCovered() {
        Pet pet = new Pet();
        pet.setId(20L);
        pet.setName("Luna");

        HealthRecord record = new HealthRecord();
        record.setId(30L);
        record.setPet(pet);
        record.setRecordType("Vaccination");
        record.setRecordDate(LocalDate.of(2024, 5, 1));
        record.setDescription("Annual vaccine");

        when(healthRecordRepository.findAll()).thenReturn(List.of(record));
        when(healthRecordRepository.findById(30L)).thenReturn(Optional.of(record));
        when(healthRecordRepository.findByPetOrderByRecordDateDesc(pet)).thenReturn(List.of(record));
        when(healthRecordRepository.save(record)).thenReturn(record);

        assertEquals(1, healthRecordService.getAllHealthRecords().size());
        assertEquals("Vaccination", healthRecordService.getHealthRecordById(30L).getRecordType());
        assertEquals(1, healthRecordService.getHealthRecordsByPet(pet).size());

        HealthRecord updatedDetails = new HealthRecord();
        updatedDetails.setRecordType("Checkup");
        updatedDetails.setRecordDate(LocalDate.of(2024, 5, 10));
        updatedDetails.setDescription("Routine checkup");
        updatedDetails.setMedication("Vitamin");
        updatedDetails.setDosage("1 tablet");
        updatedDetails.setDiagnosis("Healthy");
        updatedDetails.setTreatment("Monitor");
        updatedDetails.setNotes("No issues");

        HealthRecord updated = healthRecordService.updateHealthRecord(30L, updatedDetails);
        assertEquals("Checkup", updated.getRecordType());

        healthRecordService.createHealthRecord(record);
        healthRecordService.deleteHealthRecord(30L);
        verify(healthRecordRepository).deleteById(30L);
    }

    @Test
    void appointmentServiceMethodsAreCovered() {
        Pet pet = new Pet();
        pet.setId(40L);
        pet.setName("Rex");

        Veterinarian veterinarian = new Veterinarian();
        veterinarian.setId(50L);
        veterinarian.setFirstName("Dr.");
        veterinarian.setLastName("Kim");

        Appointment appointment = new Appointment();
        appointment.setId(60L);
        appointment.setPet(pet);
        appointment.setVeterinarian(veterinarian);
        appointment.setAppointmentTime(LocalDateTime.now().plusDays(1));
        appointment.setReason("Checkup");
        appointment.setStatus("SCHEDULED");
        appointment.setNotes("Routine visit");

        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));
        when(appointmentRepository.findById(60L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.findByPet(pet)).thenReturn(List.of(appointment));
        when(appointmentRepository.findByVeterinarian(veterinarian)).thenReturn(List.of(appointment));
        when(appointmentRepository.findByAppointmentTimeAfter(any(LocalDateTime.class))).thenReturn(List.of(appointment));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        assertEquals(1, appointmentService.getAllAppointments().size());
        assertEquals("Checkup", appointmentService.getAppointmentById(60L).getReason());
        assertEquals(1, appointmentService.getAppointmentsByPet(pet).size());
        assertEquals(1, appointmentService.getAppointmentsByVeterinarian(veterinarian).size());
        assertEquals(1, appointmentService.getUpcomingAppointments().size());

        Appointment updatedDetails = new Appointment();
        updatedDetails.setPet(pet);
        updatedDetails.setVeterinarian(veterinarian);
        updatedDetails.setAppointmentTime(LocalDateTime.now().plusDays(2));
        updatedDetails.setReason("Follow-up");
        updatedDetails.setStatus("CONFIRMED");
        updatedDetails.setNotes("Follow-up after exam");

        Appointment updated = appointmentService.updateAppointment(60L, updatedDetails);
        assertEquals("Follow-up", updated.getReason());

        appointmentService.createAppointment(appointment);
        appointmentService.updateStatus(60L, "COMPLETED");
        appointmentService.deleteAppointment(60L);
        verify(appointmentRepository).deleteById(60L);
    }
}
