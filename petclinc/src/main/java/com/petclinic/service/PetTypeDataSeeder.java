package com.petclinic.service;

import com.petclinic.model.PetType;
import com.petclinic.repository.PetTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PetTypeDataSeeder implements ApplicationRunner {

    private final PetTypeRepository petTypeRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (petTypeRepository.count() > 0) {
            return;
        }

        petTypeRepository.saveAll(List.of(
                buildPetType("Dog", "Companion animal"),
                buildPetType("Cat", "Indoor pet"),
                buildPetType("Bird", "Small avian companion"),
                buildPetType("Rabbit", "Small herbivore"),
                buildPetType("Hamster", "Pocket pet"),
                buildPetType("Lizard", "Reptile companion")
        ));
    }

    private PetType buildPetType(String name, String description) {
        PetType petType = new PetType();
        petType.setName(name);
        petType.setDescription(description);
        return petType;
    }
}
