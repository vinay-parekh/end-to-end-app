package com.example.end2endapp.registration.token;

import com.example.end2endapp.client.Client;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "verification_token")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private Date expirationTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Client client;

    public VerificationToken(String token, Client client) {
        this.token = token;
        this.client = client;
        this.expirationTime = TokenExpirationTime.getExpirationTime();
    }
}
