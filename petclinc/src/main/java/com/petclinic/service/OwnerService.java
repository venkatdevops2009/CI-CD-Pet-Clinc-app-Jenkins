// --------- OwnerService.java ---------
package com.petclinic.service;

import com.petclinic.exception.ResourceNotFoundException;
import com.petclinic.model.Owner;
import com.petclinic.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final OwnerRepository ownerRepository;

    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    public Owner getOwnerById(Long id) {
        return ownerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
    }

    public Owner createOwner(Owner owner) {
        return ownerRepository.save(owner);
    }

    public Owner updateOwner(Long id, Owner ownerDetails) {
        Owner owner = getOwnerById(id);
        owner.setFirstName(ownerDetails.getFirstName());
        owner.setLastName(ownerDetails.getLastName());
        owner.setAddress(ownerDetails.getAddress());
        owner.setCity(ownerDetails.getCity());
        owner.setTelephone(ownerDetails.getTelephone());
        return ownerRepository.save(owner);
    }

    public void deleteOwner(Long id) {
        ownerRepository.deleteById(id);
    }

    public List<Owner> searchByName(String firstName, String lastName) {
        return ownerRepository.searchByName(firstName, lastName);
    }

    public List<Owner> searchByCity(String city) {
        return ownerRepository.findByCity(city);
    }
}
