package com.example.end2endapp.registration;

import com.example.end2endapp.event.RegistrationCompleteEvent;
import com.example.end2endapp.client.Client;
import com.example.end2endapp.client.ClientService;
import com.example.end2endapp.registration.token.VerificationToken;
import com.example.end2endapp.registration.token.VerificationTokenService;
import com.example.end2endapp.utility.UrlUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/registration")
public class RegistrationController {

    private final ClientService clientService;

    private final ApplicationEventPublisher publisher;

    private final VerificationTokenService tokenService;

    @GetMapping("/registration-form")
    public String showRegistrationForm(Model model) {
        model.addAttribute("client", new RegistrationRequest());
        return "registration";
    }

    @PostMapping("/register")
    public String registerClient(@ModelAttribute("client") RegistrationRequest registration,
                                 HttpServletRequest request) {
        Client client = clientService.registerClient(registration);
        // publish the email verification event here
        publisher.publishEvent(new RegistrationCompleteEvent(client, UrlUtil.getApplicationUrl(request)));
        return "redirect:/registration/registration-form?success";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token) {
        Optional<VerificationToken> theToken = tokenService.findByToken(token);
        if (theToken.isPresent() && theToken.get().getClient().isEnabled()) {
            return "redirect:/login?verified";
        }
        String verificationResult = tokenService.validateToken(token);
        switch (verificationResult.toLowerCase()) {
            case "expired":
                return "redirect:/error?expired";
            case "valid":
                return "redirect:/error?valid";
            default:
                return "redirect:/error?invalid";
        }
    }
}
