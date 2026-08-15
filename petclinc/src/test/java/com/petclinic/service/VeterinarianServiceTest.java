package com.petclinic.service;

import com.petclinic.exception.ResourceNotFoundException;
import com.petclinic.model.Veterinarian;
import com.petclinic.repository.VeterinarianRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VeterinarianServiceTest {

    @Mock
    private VeterinarianRepository veterinarianRepository;

    @InjectMocks
    private VeterinarianService veterinarianService;

    @Test
    void getAllVeterinariansReturnsVeterinarians() {
        Veterinarian veterinarian = new Veterinarian();
        veterinarian.setId(1L);
        veterinarian.setFirstName("Dr.");
        veterinarian.setLastName("Smith");

        when(veterinarianRepository.findAll()).thenReturn(List.of(veterinarian));

        List<Veterinarian> result = veterinarianService.getAllVeterinarians();

        assertEquals(1, result.size());
        assertEquals("Smith", result.get(0).getLastName());
    }

    @Test
    void getVeterinarianByIdReturnsVeterinarian() {
        Veterinarian veterinarian = new Veterinarian();
        veterinarian.setId(2L);
        veterinarian.setFirstName("Dr.");
        veterinarian.setLastName("Brown");

        when(veterinarianRepository.findById(2L)).thenReturn(Optional.of(veterinarian));

        Veterinarian result = veterinarianService.getVeterinarianById(2L);

        assertEquals("Brown", result.getLastName());
    }

    @Test
    void getVeterinarianByIdWhenMissingThrowsResourceNotFoundException() {
        when(veterinarianRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> veterinarianService.getVeterinarianById(99L));
    }

    @Test
    void updateVeterinarianUpdatesFields() {
        Veterinarian existing = new Veterinarian();
        existing.setId(3L);
        existing.setFirstName("Old");
        existing.setLastName("Name");
        existing.setEmail("old@example.com");
        existing.setPhoneNumber("111");
        existing.setLicenseNumber("A111");
        existing.setYearsOfExperience("3");

        Veterinarian details = new Veterinarian();
        details.setFirstName("New");
        details.setLastName("Name");
        details.setEmail("new@example.com");
        details.setPhoneNumber("222");
        details.setLicenseNumber("B222");
        details.setYearsOfExperience("7");

        when(veterinarianRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(veterinarianRepository.save(existing)).thenReturn(existing);

        Veterinarian result = veterinarianService.updateVeterinarian(3L, details);

        assertEquals("New", result.getFirstName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("B222", result.getLicenseNumber());
    }
}
