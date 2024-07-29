package com.example.end2endapp.registration.password;

import com.example.end2endapp.client.Client;
import com.example.end2endapp.client.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService{

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;

    @Override
    public String validatePasswordResetToken(String theToken) {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(theToken);
        if(passwordResetToken.isEmpty()) {
            return "invalid";
        }
        Calendar calendar = Calendar.getInstance();
        if ((passwordResetToken.get().getExpirationTime().getTime() - calendar.getTime().getTime() <= 0)) {
            return "expired";
        }
        return "valid";
    }

    @Override
    public void createPasswordResetTokenForClient(Client client, String passwordResetToken) {
        PasswordResetToken resetToken = new PasswordResetToken(passwordResetToken, client);
        passwordResetTokenRepository.save(resetToken);
    }

    @Override
    public Optional<Client> findClientByPasswordResetToken(String theToken) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(theToken).get().getClient());
    }

    @Override
    public void resetPassword(Client theClient, String password) {
        theClient.setPassword(passwordEncoder.encode(password));
        clientRepository.save(theClient);
    }
}
