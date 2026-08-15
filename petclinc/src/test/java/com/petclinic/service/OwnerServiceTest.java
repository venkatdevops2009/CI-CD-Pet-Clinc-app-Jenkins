package com.petclinic.service;

import com.petclinic.exception.ResourceNotFoundException;
import com.petclinic.model.Owner;
import com.petclinic.repository.OwnerRepository;
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
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

    @Test
    void getAllOwnersReturnsAllOwners() {
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setAddress("Main Street");
        owner.setCity("New York");
        owner.setTelephone("1234567890");

        when(ownerRepository.findAll()).thenReturn(List.of(owner));

        List<Owner> result = ownerService.getAllOwners();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void getOwnerByIdReturnsOwner() {
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setFirstName("Jane");
        owner.setLastName("Smith");
        owner.setAddress("Maple Ave");
        owner.setCity("Boston");
        owner.setTelephone("5551234567");

        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));

        Owner result = ownerService.getOwnerById(1L);

        assertEquals("Jane", result.getFirstName());
    }

    @Test
    void getOwnerByIdWhenMissingThrowsResourceNotFoundException() {
        when(ownerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ownerService.getOwnerById(99L));
    }

    @Test
    void updateOwnerUpdatesFields() {
        Owner existing = new Owner();
        existing.setId(1L);
        existing.setFirstName("Old");
        existing.setLastName("Name");
        existing.setAddress("Old St");
        existing.setCity("Old City");
        existing.setTelephone("111");

        Owner details = new Owner();
        details.setFirstName("New");
        details.setLastName("Name");
        details.setAddress("New St");
        details.setCity("New City");
        details.setTelephone("222");

        when(ownerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(ownerRepository.save(existing)).thenReturn(existing);

        Owner result = ownerService.updateOwner(1L, details);

        assertEquals("New", result.getFirstName());
        assertEquals("New City", result.getCity());
        assertEquals("222", result.getTelephone());
    }
}
