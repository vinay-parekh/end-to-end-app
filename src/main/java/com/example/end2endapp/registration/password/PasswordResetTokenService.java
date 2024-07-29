package com.example.end2endapp.registration.password;

import com.example.end2endapp.client.Client;

import java.util.Optional;

public interface PasswordResetTokenService {
    String validatePasswordResetToken(String theToken);

    void createPasswordResetTokenForClient(Client client, String passwordResetToken);

    Optional<Client> findClientByPasswordResetToken(String theToken);

    void resetPassword(Client theClient, String password);
}
