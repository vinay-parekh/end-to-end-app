package com.example.end2endapp.registration.token;

import com.example.end2endapp.client.Client;
import com.example.end2endapp.client.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService{

    private final VerificationTokenRepository tokenRepository;
    private final ClientRepository clientRepository;

    @Override
    public String validateToken(String token) {
        Optional<VerificationToken> theToken = tokenRepository.findByToken(token);
        if (theToken.isEmpty()) {
            return "INVALID";
        }
        Client client = theToken.get().getClient();
        Calendar calendar = Calendar.getInstance();
        if ((theToken.get().getExpirationTime().getTime()
                - calendar.getTime().getTime() <= 0)) {
            return "EXPIRED";
        }
        client.setEnabled(true);
        clientRepository.save(client);
        return "VALID";
    }

    @Override
    public void saveVerificationTokenForClient(Client client, String token) {
        var verificationToken = new VerificationToken(token, client);
        tokenRepository.save(verificationToken);
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }
}
