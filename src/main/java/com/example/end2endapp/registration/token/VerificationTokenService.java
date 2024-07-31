package com.example.end2endapp.registration.token;

import com.example.end2endapp.client.Client;

import java.util.Optional;

public interface VerificationTokenService {

    String validateToken(String token);
    void saveVerificationTokenForClient(Client client, String token);
    Optional<VerificationToken> findByToken(String token);
    void deleteClientToken(Long id);
}
