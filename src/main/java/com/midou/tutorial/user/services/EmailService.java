package com.midou.tutorial.user.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private  final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderUsername;

    public void emailSenderOtp(String email,String otp,String firstName,String lastName) {
        String subject = "Email Verification";
        String body = "Hello " + firstName +" "+ lastName + " This is your Email Verification Code:" + otp;

        sendMail(email, subject, body);
    }



    public void emailSenderForgetPassword(String email,String firstName,String lastName,String resetUrl) {
        String subject = "Reset Password";
        String body = "Hello " + firstName + " " +lastName + " This is your Reset Password link:" + resetUrl;

        sendMail(email, subject, body);
    }


    public void sendMail(String email, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(senderUsername); // Set sender email
            helper.setTo(email); // Set recipient
            helper.setSubject(subject);
            helper.setText(body);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
