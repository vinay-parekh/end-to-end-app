package com.example.end2endapp.client;

import com.example.end2endapp.registration.RegistrationRequest;

import java.util.List;

public interface ClientService {
    List<Client> getAllClients();
    Client registerClient(RegistrationRequest registrationRequest);
    Client findByEmail(String email);
}
