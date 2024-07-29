package com.example.end2endapp.registration;

import com.example.end2endapp.event.RegistrationCompleteEvent;
import com.example.end2endapp.client.Client;
import com.example.end2endapp.client.ClientService;
import com.example.end2endapp.event.listener.RegistrationCompleteEventListener;
import com.example.end2endapp.registration.password.PasswordResetTokenService;
import com.example.end2endapp.registration.token.VerificationToken;
import com.example.end2endapp.registration.token.VerificationTokenService;
import com.example.end2endapp.utility.UrlUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/registration")
public class RegistrationController {

    private final ClientService clientService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenService tokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final RegistrationCompleteEventListener eventListener;

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

    @GetMapping("/forgot-password-request")
    public String forgotPassword() {
        return "forgot-password-form";
    }

    @PostMapping("/forgot-password")
    public String resetPasswordRequest(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        Client client = clientService.findByEmail(email);
        if (client == null) {
            return "redirect:/registration/forgot-password-request?not_found";
        }
        String passwordResetToken = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetTokenForClient(client, passwordResetToken);
        //send password reset verification email to the user
        String url = UrlUtil.getApplicationUrl(request)
                +"/registration/password-reset-form?token="
                +passwordResetToken;
        try {
            eventListener.sendPasswordResetVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/registration/forgot-password-request?success";
    }

    @GetMapping("/reset-password-form")
    public String passwordResetForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "password-reset-form";
    }

    @PostMapping("/reset-password")
    public String resetPassword(HttpServletRequest request) {
        String theToken = request.getParameter("token");
        String password = request.getParameter("password");
        String tokenVerificationResult = passwordResetTokenService.validatePasswordResetToken(theToken);
        if(!tokenVerificationResult.equalsIgnoreCase("valid")) {
            return "redirect:/error?invalid_token";
        }
        Optional<Client> theClient = passwordResetTokenService.findClientByPasswordResetToken(theToken);
        if (theClient.isPresent()) {
            passwordResetTokenService.resetPassword(theClient.get(), password);
            return "redirect:/login?reset_success";
        }
        return "redirect:/error?not_found";
    }
}
