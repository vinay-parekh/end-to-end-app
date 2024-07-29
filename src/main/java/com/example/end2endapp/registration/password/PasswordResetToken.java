package com.example.end2endapp.registration.password;

import com.example.end2endapp.client.Client;
import com.example.end2endapp.registration.token.TokenExpirationTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.crypto.Data;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private Date expirationTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Client client;

    public PasswordResetToken(String token, Client client) {
        this.token = token;
        this.client = client;
        this.expirationTime = TokenExpirationTime.getExpirationTime();
    }
}
