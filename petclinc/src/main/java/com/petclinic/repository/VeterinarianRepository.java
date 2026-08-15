// --------- VeterinarianRepository.java ---------
package com.petclinic.repository;

import com.petclinic.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VeterinarianRepository extends JpaRepository<Veterinarian, Long> {
    Optional<Veterinarian> findByEmail(String email);
    Optional<Veterinarian> findByLicenseNumber(String licenseNumber);
    
    @Query("SELECT v FROM Veterinarian v WHERE v.firstName LIKE %:name% OR v.lastName LIKE %:name%")
    List<Veterinarian> searchByName(@Param("name") String name);
}
