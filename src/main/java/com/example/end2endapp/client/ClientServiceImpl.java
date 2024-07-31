package com.example.end2endapp.client;

import com.example.end2endapp.registration.RegistrationRequest;
import com.example.end2endapp.registration.token.VerificationTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;

    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Override
    public Client registerClient(RegistrationRequest registrationRequest) {

        var client = new Client(registrationRequest.getFirstName(),
                registrationRequest.getLastName(),
                registrationRequest.getEmail(),
                passwordEncoder.encode(registrationRequest.getPassword()),
                Arrays.asList(new Role("ROLE_CLIENT")));
        return clientRepository.save(client);
    }

    @Override
    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Client not found"));
    }

    @Override
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    @Transactional
    @Override
    public void updateClient(Long id, String firstName, String lastName, String email) {
        clientRepository.update(id, firstName, lastName, email);
    }

    @Transactional
    @Override
    public void deleteClient(Long id) {
        Optional<Client> theClient = clientRepository.findById(id);
        theClient.ifPresent(client -> verificationTokenService.deleteClientToken(client.getId()));
        clientRepository.deleteById(id);
    }
}
