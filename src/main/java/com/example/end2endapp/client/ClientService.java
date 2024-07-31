package com.example.end2endapp.client;

import com.example.end2endapp.registration.RegistrationRequest;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    List<Client> getAllClients();
    Client registerClient(RegistrationRequest registrationRequest);
    Client findByEmail(String email);
    Optional<Client> findById(Long id);
    void updateClient(Long id, String firstName, String lastName, String email);
    void deleteClient(Long id);
}
