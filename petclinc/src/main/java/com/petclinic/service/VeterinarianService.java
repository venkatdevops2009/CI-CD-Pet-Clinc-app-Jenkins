// --------- VeterinarianService.java ---------
package com.petclinic.service;

import com.petclinic.model.Veterinarian;
import com.petclinic.repository.VeterinarianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VeterinarianService {
    private final VeterinarianRepository veterinarianRepository;

    public List<Veterinarian> getAllVeterinarians() {
        return veterinarianRepository.findAll();
    }

    public Veterinarian getVeterinarianById(Long id) {
        return veterinarianRepository.findById(id).orElseThrow(() -> new RuntimeException("Veterinarian not found"));
    }

    public Veterinarian createVeterinarian(Veterinarian veterinarian) {
        return veterinarianRepository.save(veterinarian);
    }

    public Veterinarian updateVeterinarian(Long id, Veterinarian vetDetails) {
        Veterinarian vet = getVeterinarianById(id);
        vet.setFirstName(vetDetails.getFirstName());
        vet.setLastName(vetDetails.getLastName());
        vet.setEmail(vetDetails.getEmail());
        vet.setPhoneNumber(vetDetails.getPhoneNumber());
        vet.setLicenseNumber(vetDetails.getLicenseNumber());
        vet.setYearsOfExperience(vetDetails.getYearsOfExperience());
        return veterinarianRepository.save(vet);
    }

    public void deleteVeterinarian(Long id) {
        veterinarianRepository.deleteById(id);
    }

    public List<Veterinarian> searchVeterinarians(String name) {
        return veterinarianRepository.searchByName(name);
    }
}
