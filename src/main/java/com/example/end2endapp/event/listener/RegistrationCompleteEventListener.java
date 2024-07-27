package com.example.end2endapp.event.listener;

import com.example.end2endapp.client.Client;
import com.example.end2endapp.event.RegistrationCompleteEvent;
import com.example.end2endapp.registration.token.VerificationTokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final VerificationTokenService tokenService;
    private final JavaMailSender mailSender;
    private Client client;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        // 1. get the client
        client = event.getClient();
        // 2. generate a token for the client
        String vToken = UUID.randomUUID().toString();
        // 3. save the token for the client
        tokenService.saveVerificationTokenForClient(client, vToken);
        // 4. build the verification url
        String url = event.getConfirmationUrl() + "/registration/verifyEmail?token=" + vToken;
        // 5. send the email to the user
        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String senderName = "Clients Verification Service";
        String mailContent = "<p> Hi, " + client.getFirstName() + ", </p>"+
                "<p>Thank you for registering with us, " +
                "Please follow the link below to complete your registration.</p>"+
                "<a href=\"" +url+ "\">Verify your email to activate your account</a>"+
                "<p> Thank you <br> Clients Registration Portal Service";
        emailMessage(subject, senderName, mailContent, mailSender, client);
    }

    private void emailMessage(String subject, String senderName, String mailContent,
                              JavaMailSender mailSender, Client client)
            throws MessagingException, UnsupportedEncodingException{

        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("jainvinay.4802@gmail.com", senderName);
        messageHelper.setTo(client.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}
