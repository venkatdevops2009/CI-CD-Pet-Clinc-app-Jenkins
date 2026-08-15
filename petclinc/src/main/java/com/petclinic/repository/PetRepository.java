// --------- PetRepository.java ---------
package com.petclinic.repository;

import com.petclinic.model.Pet;
import com.petclinic.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByOwner(Owner owner);
    List<Pet> findByNameContainingIgnoreCase(String name);
}
