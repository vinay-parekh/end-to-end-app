package com.example.end2endapp.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);

    @Modifying
    @Query(value = "UPDATE Client c " +
            "SET c.firstName =: firstName,c.lastName =: lastName, c.email =: email " +
            "WHERE c.id =: id")
    void update(Long id, String firstName, String lastName, String email);
}
