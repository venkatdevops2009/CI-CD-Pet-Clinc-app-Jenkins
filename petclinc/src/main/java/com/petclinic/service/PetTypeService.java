// --------- PetTypeService.java ---------
package com.petclinic.service;

import com.petclinic.model.PetType;
import com.petclinic.repository.PetTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PetTypeService {
    private final PetTypeRepository petTypeRepository;

    public List<PetType> getAllPetTypes() {
        return petTypeRepository.findAll();
    }

    public PetType getPetTypeById(Long id) {
        return petTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("Pet type not found"));
    }

    public PetType createPetType(PetType petType) {
        return petTypeRepository.save(petType);
    }

    public PetType updatePetType(Long id, PetType petTypeDetails) {
        PetType petType = getPetTypeById(id);
        petType.setName(petTypeDetails.getName());
        petType.setDescription(petTypeDetails.getDescription());
        return petTypeRepository.save(petType);
    }

    public void deletePetType(Long id) {
        petTypeRepository.deleteById(id);
    }
}
