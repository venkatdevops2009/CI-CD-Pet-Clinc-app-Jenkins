package com.petclinic.service;

import com.petclinic.exception.ResourceNotFoundException;
import com.petclinic.model.Owner;
import com.petclinic.model.Pet;
import com.petclinic.model.PetType;
import com.petclinic.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    @Test
    void getAllPetsReturnsPets() {
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("Max");

        when(petRepository.findAll()).thenReturn(List.of(pet));

        List<Pet> result = petService.getAllPets();

        assertEquals(1, result.size());
        assertEquals("Max", result.get(0).getName());
    }

    @Test
    void getPetByIdReturnsPet() {
        Pet pet = new Pet();
        pet.setId(2L);
        pet.setName("Bella");

        when(petRepository.findById(2L)).thenReturn(Optional.of(pet));

        Pet result = petService.getPetById(2L);

        assertEquals("Bella", result.getName());
    }

    @Test
    void getPetByIdWhenMissingThrowsResourceNotFoundException() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> petService.getPetById(99L));
    }

    @Test
    void updatePetUpdatesFields() {
        Pet existing = new Pet();
        existing.setId(3L);
        existing.setName("Old Name");
        existing.setDateOfBirth(LocalDate.of(2020, 1, 1));

        PetType petType = new PetType();
        petType.setId(4L);
        petType.setName("Dog");
        existing.setPetType(petType);

        Owner owner = new Owner();
        owner.setId(7L);
        owner.setFirstName("Alice");
        owner.setLastName("Brown");
        owner.setAddress("One Street");
        owner.setCity("Seattle");
        owner.setTelephone("1234567890");
        existing.setOwner(owner);

        Pet details = new Pet();
        details.setName("New Name");
        details.setDateOfBirth(LocalDate.of(2021, 5, 15));

        PetType newType = new PetType();
        newType.setId(9L);
        newType.setName("Cat");
        details.setPetType(newType);

        when(petRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(petRepository.save(existing)).thenReturn(existing);

        Pet result = petService.updatePet(3L, details);

        assertEquals("New Name", result.getName());
        assertEquals(LocalDate.of(2021, 5, 15), result.getDateOfBirth());
        assertEquals("Cat", result.getPetType().getName());
    }
}
