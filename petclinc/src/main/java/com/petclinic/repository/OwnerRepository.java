// --------- OwnerRepository.java ---------
package com.petclinic.repository;

import com.petclinic.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByLastName(String lastName);
    List<Owner> findByCity(String city);
    
    @Query("SELECT o FROM Owner o WHERE o.firstName LIKE %:firstName% OR o.lastName LIKE %:lastName%")
    List<Owner> searchByName(@Param("firstName") String firstName, @Param("lastName") String lastName);
}

