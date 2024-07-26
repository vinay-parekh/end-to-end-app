package com.example.end2endapp.registration;

import com.example.end2endapp.event.RegistrationCompleteEvent;
import com.example.end2endapp.client.Client;
import com.example.end2endapp.client.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/registration")
public class RegistrationController {

    private final ClientService clientService;

    private final ApplicationEventPublisher publisher;

    @GetMapping("/registration-form")
    public String showRegistrationForm(Model model) {
        model.addAttribute("client", new RegistrationRequest());
        return "registration";
    }

    @PostMapping("/register")
    public String registerClient(@ModelAttribute("client") RegistrationRequest registration) {
        Client client = clientService.registerClient(registration);
        // publish the email verification event here
        publisher.publishEvent(new RegistrationCompleteEvent(client, ""));
        return "redirect:/registration/registration-form?success";
    }
}
