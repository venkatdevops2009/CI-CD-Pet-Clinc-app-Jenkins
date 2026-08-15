// --------- PetService.java ---------
package com.petclinic.service;

import com.petclinic.model.Pet;
import com.petclinic.model.Owner;
import com.petclinic.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public Pet getPetById(Long id) {
        return petRepository.findById(id).orElseThrow(() -> new RuntimeException("Pet not found"));
    }

    public List<Pet> getPetsByOwner(Owner owner) {
        return petRepository.findByOwner(owner);
    }

    public Pet createPet(Pet pet) {
        return petRepository.save(pet);
    }

    public Pet updatePet(Long id, Pet petDetails) {
        Pet pet = getPetById(id);
        pet.setName(petDetails.getName());
        pet.setPetType(petDetails.getPetType());
        pet.setDateOfBirth(petDetails.getDateOfBirth());
        pet.setBreed(petDetails.getBreed());
        pet.setColor(petDetails.getColor());
        return petRepository.save(pet);
    }

    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }

    public List<Pet> searchPets(String name) {
        return petRepository.findByNameContainingIgnoreCase(name);
    }
}
