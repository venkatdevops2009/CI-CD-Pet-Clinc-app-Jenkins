// --------- SpecialtyService.java ---------
package com.petclinic.service;

import com.petclinic.model.Specialty;
import com.petclinic.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyService {
    private final SpecialtyRepository specialtyRepository;

    public List<Specialty> getAllSpecialties() {
        return specialtyRepository.findAll();
    }

    public Specialty getSpecialtyById(Long id) {
        return specialtyRepository.findById(id).orElseThrow(() -> new RuntimeException("Specialty not found"));
    }

    public Specialty createSpecialty(Specialty specialty) {
        return specialtyRepository.save(specialty);
    }

    public Specialty updateSpecialty(Long id, Specialty specialtyDetails) {
        Specialty specialty = getSpecialtyById(id);
        specialty.setName(specialtyDetails.getName());
        specialty.setDescription(specialtyDetails.getDescription());
        return specialtyRepository.save(specialty);
    }

    public void deleteSpecialty(Long id) {
        specialtyRepository.deleteById(id);
    }
}
