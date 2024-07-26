package com.example.end2endapp.event;

import com.example.end2endapp.client.Client;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private Client client;
    private String confirmationUrl;
    public RegistrationCompleteEvent(Client client, String confirmationUrl) {
        super(client);
        this.client = client;
        this.confirmationUrl = confirmationUrl;
    }
}
